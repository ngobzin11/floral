package com.pkoding.optimizer;

import java.util.*;
import java.util.regex.*;

import com.pkoding.translator.error.*;
import com.pkoding.recognizer.lexer.*;
import com.pkoding.recognizer.scope.*;
import com.pkoding.universal.util.*;

public class Optimizer {
	
	private HashMap<String, Tuple<Integer, ArrayList<Token>>> token_map;
	private final Pattern space_after = Pattern.compile("[,;]");
	private ArrayList<ArrayList<Token>> output_code;
	private final Lexer lexer;
	private Scope cur_scope;
	private int line_number;
	private int block_num;
	private int depth;
	
	public Optimizer(Lexer lexer, int optimization_code) {
		token_map = new HashMap<String, Tuple<Integer, ArrayList<Token>>>();
		output_code = new ArrayList<ArrayList<Token>>();
		cur_scope = new GlobalScope();
		this.lexer = lexer;
		
		// Line numbers start at zero because they are indices of an ArrayList
		line_number = 0;
		depth = 0;
		
		define();
		execute(optimization_code);
	}
	
	public ArrayList<String> readableCode() {
		ArrayList<String> code = new ArrayList<String>();
		for (ArrayList<Token> tokens : output_code) {
			String line = "";
			for (Token token : tokens) {
				switch (token.type) {
					case FUNCTION_DECL: case CONTROL_BEGIN: case FOR: case CL_PAREN: case SEMI_COLON:
					case IF: case WHILE: case TRY: case CATCH: case FINALLY:
						line += token.name + " ";
						break;
						
					case KEYWORD: 
						line += " " + token.name + " ";
						break;
						
					case DO: case THEN:
						line += " " + token.name;
						break;
						
					case OTHER:
						line += (space_after.matcher(token.name).find() ? token.name + " " : token.name);
						break;
						
					default:
						line += token.name;
						break; 
				}
			}
			code.add(line.trim());
		}
		
		return code;
	}
	
	/**
	 * Optimization level will depend on the optimization code. The format of the optimization 
	 * 	code will be the same as the UNIX chmod code. Each digit is a category and the level of 
	 * 	optimization for that category is based on the value of the digit; the higher the value
	 * 	the higher the level of optimization. 
	 * 
	 * As of now there is only one category, code compression, and in it there is variable 
	 * 	replacement, so the code will be one number
	 */
	private void execute(int optimization_code) {
		switch (optimization_code) {
			case 1:
				output_code = VariableReplacement.execute(token_map, output_code);
				break;
				
			// If the code is zero do nothing
			default:
				break;
		}
	}
	
	/**
	 * This is essentially the definition phase of the compiler, run 2.
	 * Tags the stream of characters coming from the tokenizer:
	 * 		- Adds the scope to variables
	 * 		- Updates the variable's usage statistics
	 * 		- TODO: Fix Scope Bug
	 * 		- TODO: Think of variables being used as they are declared 
	 */
	private void define() {
		boolean declare_nxt = false, update_nxt = false, nxt_arg = false, nxt_value = false, 
				nxt_for_var = false, in_else = false, in_else_if = false, q_if_change = false;
		
		// Counting open and closing structures
		Stack<TokenType> blocks = new Stack<TokenType>();
		int parens = 0;
		
		ArrayList<Token> var_value = new ArrayList<Token>();
		ArrayList<Token> line = new ArrayList<Token>();
		Token token = lexer.nextToken();
		String prev_var = null;
		
		// This string dictionary holds a stream of 1's and 0's for each variable declared in
		//	an if/else-if/else statement
		//		- 1 if the variable shows up and 0 otherwise
		HashMap<String, String> if_var_map = new HashMap<String, String>();
		
		block_num = 0;
		while (token != null) {
			updateScope(token, q_if_change);
			
			// Mark the next couple of variables for declaration: Close ==> )
			if (token.type == TokenType.FUNCTION_DECL) {
				blocks.add(token.type);
				nxt_arg = true;
			
			// Mark the next variable that shows up for declaration
			} else if (token.type == TokenType.VAR_DECL) {
				nxt_for_var = false;
				declare_nxt = true;
			
			// Mark the next variable that shows up for update
			} else if (token.type == TokenType.VAR_UPDATE) {
				nxt_for_var = false;
				update_nxt = true;
			
			// Beginning a new control statement
			} else if ((token.type == TokenType.FOR) || (token.type == TokenType.WHILE) || 
					(token.type == TokenType.TRY) || (token.type == TokenType.SWITCH)) {
				
				blocks.add(token.type);
				
				// In case a loop variable is being declared
				if (token.type == TokenType.FOR)
					nxt_for_var = true;
			
			// Remember that you're in an if statement and remember the depth too
			} else if (token.type == TokenType.IF) {
				q_if_change = true;
				blocks.add(token.type);
				
			// Remember that you just entered an else block
			} else if (token.type == TokenType.ELSE_IF) {
				in_else_if = true;
			
			// Remember that you just entered an else block: reset else-if
			} else if (token.type == TokenType.ELSE) {
				in_else_if = false;
				in_else = true;
				
			// End If/Else-if Declaration
			} else if (token.type == TokenType.THEN) {
				q_if_change = false;
			
			} else if (token.type == TokenType.OP_PAREN) {
				parens++;
				
			// End of an if-else block: reset else-if/else
			} else if (token.type == TokenType.END) {
				if (blocks.size() != 0) {
					if (in_else || in_else_if || (blocks.pop() == TokenType.IF))
						in_else_if = in_else = false;
				
				} else {
					String[] error = {token.name};
					System.err.println(
						new TranslationError(TranslationErrorType.TrailingEndMarker, error, line_number)
					);
				}
				
			// Add variable and value to token map
			} else if (token.type == TokenType.VARIABLE) {
				String key = token.scope().name + "_" + token.name;
				// If the token is being declared
				if (declare_nxt) {
					// Add only if variable has not been declared
					if (!token_map.containsKey(key)) {
						// If variable has been declared in upper block scope
						if (inUpperBlockScope(token, cur_scope) != null) {
							String[] error = {token.name};
							System.err.println(
								new TranslationError(TranslationErrorType.DuplicateLocalVariable, error, line_number)
							);
						}
						
						// If the variable is being declared in an if statement 
						//		It can't be in an else/elseif statement
						if (((blocks.size() > 0) && (blocks.peek() == TokenType.IF)) && !(in_else || in_else_if))
							if_var_map.put(key, "1");
						
						token_map.put(key, new Tuple<Integer, ArrayList<Token>>(0, null));
						prev_var = key;
					
					// If this variable is being declared in an else if block
					} else if (in_else_if) {
						// Update the variable in the map if it exists in the map otherwise 
						//	add a zero in there to make sure that the variable doesn't get hoisted
						if_var_map.put(
							key, 
							if_var_map.containsKey(key) ? if_var_map.get(key) + "1" : "01"
						);
						
					// If this variable is being declared in an else block
					} else if (in_else) {
						// If the variable has been declared in every accompanying block there won't
						//	be a 0 in the if value of that map
						if (if_var_map.containsKey(key)) {
							// "Hoist" the variable if it doesn't contain a 0 in it
							if (!if_var_map.get(key).contains("0"))
								token = hoist(token);
							
							// Remove the variable from the map
							if_var_map.remove(key);
						}
						
					// Variable has been declared before
					} else {
						String[] error = {token.name};
						System.err.println(
							new TranslationError(TranslationErrorType.DuplicateLocalVariable, error, line_number)
						);
					}
				// If the token is being updated:
				//		A variable that's being updated should not be replaced, so add 2 instead of one to its usage stats
				} else if (update_nxt) {
					// Update Variable Usage Count If Already Declared
					if (token_map.containsKey(key)) {
						token_map.put(key, new Tuple<Integer, ArrayList<Token>>(token_map.get(key).first + 2, null));
						prev_var = key;
						
					// Variable not declared in this scope
					} else {
						// Update the usage statistics if the variable is from a higher scope
						String temp_key = upperScopeKey(token);
						if (temp_key != null) {
							key = temp_key;
							token_map.put(
								key, 
								new Tuple<Integer, ArrayList<Token>>(
									token_map.get(key).first + 1, 
									token_map.get(key).second
								)
							);
							
						// This variable is being used before it's declared
						} else {
							String[] error = {token.name};
							System.err.println(
								new TranslationError(TranslationErrorType.UndeclaredLocalVariable, error, line_number)
							);
						}
					}
					
				// If the token is an argument
				} else if (nxt_arg) {
					token_map.put(key, new Tuple<Integer, ArrayList<Token>>(0, null));
				
				
				// If the variable is declared implicitly in a for loop: 
				//		This variable has a value of <null> just like arguments. Prevents them from being  
				//		compressed/replaced if they are used once
				} else if (nxt_for_var) {
					token_map.put(key, new Tuple<Integer, ArrayList<Token>>(0, null));
					
				// If the variable is part of the definition of another
				} else {
					if (nxt_value) {
						// If the variable has not been declared
						if (!token_map.containsKey(key)) {
							String temp_key = upperScopeKey(token);
							if (temp_key != null) {
								key = temp_key;
								token_map.put(
									key, 
									new Tuple<Integer, ArrayList<Token>>(
										token_map.get(key).first + 1, 
										token_map.get(key).second
									)
								);
								
							// Variable not declared at all
							} else {
								String[] error = {token.name};
								System.err.println(
									new TranslationError(TranslationErrorType.UndeclaredLocalVariable, error, line_number)
								);
							}
							
						// Update the variable's usage statistics 
						} else {
							token_map.put(
								key, 
								new Tuple<Integer, ArrayList<Token>>(
									token_map.get(key).first + 1, 
									token_map.get(key).second
								)
							);
						}
						var_value.add(token);
						
					// Update the variable's usage statistics
					} else if (token_map.containsKey(key)) {
						token_map.put(
							key, 
							new Tuple<Integer, ArrayList<Token>>(
								token_map.get(key).first + 1, 
								token_map.get(key).second
							)
						);
						
					// Variable being used without declaration in this scope
					} else {
						// Update the usage statistics if the variable is from a higher scope
						String temp_key = upperScopeKey(token);
						if (temp_key != null) {
							key = temp_key;
							token_map.put(
								key, 
								new Tuple<Integer, ArrayList<Token>>(
									token_map.get(key).first + 1, 
									token_map.get(key).second
								)
							);
							
						// Variable not declared at all
						} else {
							String[] error = {token.name};
							System.err.println(
								new TranslationError(TranslationErrorType.UndeclaredLocalVariable, error, line_number)
							);
						}
					}
				}
				
			// Close Argument Declaration: 
			//		Parentheses are not allowed in function declarations so no other checks necessary
			} else if ((token.type == TokenType.CL_PAREN) && (declare_nxt || update_nxt)) {
				// Closing Parenthesis Before An Opening Parenthesis
				if (--parens < 0) {
					String[] error = {token.name, "Closing parenthesis before an opening parenthesis"};
					System.err.println(
						new TranslationError(TranslationErrorType.UnexpectedCharacter, error, line_number)
					);
				}
				
				// If a variable was being declared or updated: start capturing the variable's value
				if (prev_var != null)
					nxt_value = declare_nxt || update_nxt;
				
				nxt_arg = declare_nxt = update_nxt = false;
			
			} else if (token.type == TokenType.CL_PAREN) {
				if (--parens < 0) {
					String[] error = {token.name, "Closing parenthesis before an opening parenthesis"};
					System.err.println(
						new TranslationError(TranslationErrorType.UnexpectedCharacter, error, line_number)
					);
				}
				
			// End the capturing of a variable if we've been doing it 
			} else if (token.type == TokenType.NEWLINE || token.type == TokenType.SEMI_COLON) {
				// If we've been capturing the value of a variable: Update it's value in the token map 
				if (nxt_value && (prev_var != null)) {
					token_map.put(
						prev_var, 
						new Tuple<Integer, ArrayList<Token>>(
							token_map.get(prev_var).first, 
							var_value
						)
					);
					
					// Reset everything
					var_value = new ArrayList<Token>();
					prev_var = null;
				}
				
				// Add the line in the output code and reset line
				if (token.type == TokenType.NEWLINE) {
					line_number++;
					output_code.add(line);
					line = new ArrayList<Token>();
				}
				nxt_arg = declare_nxt = update_nxt = nxt_value = false;
				
			// Capture the value of a variable 
			} else if (nxt_value) {
				var_value.add(token);
			}
			
			// Add the token to the line as long as it's not a new line
			if (token.type != TokenType.NEWLINE)
				line.add(token);
			
			// If it's a new line
			else {
				// Missing Closing Parenthesis
				if (parens > 0) {
					String[] error = {")", "Missing closing parenthesis. Either remove an opening parenthesis or close it."};
					System.err.println(
						new TranslationError(TranslationErrorType.UnexpectedCharacter, error, line_number)
					);
				}
			}
			
			token = lexer.nextToken();
		}
		
		// If there is an end marker missing. The error does not necessarily mean the end marker is 
		//	missing at the very end
		if (!blocks.isEmpty()) {
			String[] error = {"__END__"};
			System.err.println(
				new TranslationError(TranslationErrorType.MissingEndMarker, error, line_number - 1)
			);
		}
		
		// Doesn't really matter, but reset the tokenizer in case it's used again later
		lexer.reset();
	}
	
	/**
	 * Redeclares a variable from a block scope to an upper scope (block/function).
	 * 
	 * @param token
	 * @return
	 */
	private Token hoist(Token token) {
		Scope scope = token.scope();
		
		// Update the key in the token map to null, because it's technically being 
		//	instantiated as null then changed based on certain conditions. Keeping  
		//	it as null and starting off it's value at 2 also makes sure the value 
		//	won't be compressed.
		token_map.remove(scope.name + "_" + token.name);
		token_map.put(
			scope.parent.name + "_" + token.name, 
			new Tuple<Integer, ArrayList<Token>>(2, null)
		);
		
		token.setScope(scope.parent);
		return token;
	}
	
	private String upperScopeKey(Token token) {
		Scope scope = token.scope();
		while (scope.parent != null) {
			String key = scope.parent.name + "_" + token.name;
			if (token_map.containsKey(key))
				return key;
			
			scope = scope.parent;
		}
		
		return null;
	}
	
	private void updateScope(Token token, boolean q_if_change) {
		switch (token.type) {
			// function
			case FUNCTION_DECL:
				cur_scope = new FunctionScope(cur_scope, lexer.peekNext().name);
				depth++;
				break;
			
			// for|while|try|catch|finally|if
			case FOR: case WHILE: case TRY: case CATCH: case FINALLY:
				cur_scope = new BlockScope(cur_scope, cur_scope.name + token.name + block_num++);
				depth++;
				break;
			
			case THEN: 
				if (q_if_change) {
					cur_scope = new BlockScope(cur_scope, cur_scope.name + "__IF__" + block_num++);
					depth++;
				}
				break;
			
			case VARIABLE:
				token.setScope(cur_scope);
				break;
			
			case END:
				if (--depth == 0) {
					// Reset the block counter: So each function has it's own block counter
					block_num = 0;
					cur_scope = new GlobalScope();
					
				} else
					cur_scope = cur_scope.parent;
				
				break;
			
			// Just coz Java 7 wants a default case
			default:
				break;
		}
	}
	
	private Scope inUpperBlockScope(Token token, Scope scope) {
		while (scope.type == ScopeName.BLOCK) {
			// Checking if the parent of a block scope is not null is not necessary, coz 
			//		the global scope will always be a parent of a block scope
			//	========================================================================
			//	Loop through the block numbers to see if the variable has been declared 
			//		in an upper block scope
			int num = block_num;
			while (num-- >= 0)
				if (token_map.containsKey(scope.parent.name + token.name + num))
					return scope.parent;
			
			scope = scope.parent;
		}
		// Now check the upper scope to see if the variable has been declared there
		if (token_map.containsKey(scope.name + "_" + token.name))
			return scope;
		
		return null;
	}

}
