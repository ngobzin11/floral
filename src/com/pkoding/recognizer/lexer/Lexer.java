package com.pkoding.recognizer.lexer;

import java.util.ArrayList;
import java.util.regex.*;

import com.pkoding.universal.data.PreprocessData;
// import com.pkoding.universal.util.Debug;
import com.pkoding.universal.util.Regex;
import com.pkoding.universal.util.RegexDefinition;

/**
 * Line by Line Tokenization of the code. The Lexer is given the full code 
 * 	to begin with and requests are only for each each.
 * 
 * @author ngobzin11
 *
 */
public class Lexer {
	
	private final Pattern varchar = Pattern.compile(RegexDefinition.VARIABLE_CHAR);
	private final Pattern number = Pattern.compile(RegexDefinition.STRICT_NUMBER);
	private final Pattern string = Pattern.compile("^__AUTO_STRING_([0-9]+)__$");
	private ArrayList<ArrayList<String>> tokens;
	private int current_token, current_line;
	public final ArrayList<Token> tagged;
	private final int tot_lines;
	private int index = 0;
	
	public Lexer(ArrayList<String> lines) {
		tokens = new ArrayList<ArrayList<String>>();
		current_line = current_token = 0;
		tot_lines = tokenize(lines);
		tagged = tagTokens();
	}
	
	private ArrayList<Token> tagTokens() {
		ArrayList<Token> result = new ArrayList<Token>();
		while (hasNext()) {
			ArrayList<String> line = tokens.get(current_line);
			Token prev = null;
			while (current_token < line.size()) {
				String token = line.get(current_token);
				if (number.matcher(token).find())
					result.add(new Token(TokenType.NUMBER, line.get(current_token++)));
				
				else if (Regex.VARIABLE.matcher(token).find()) {
					Matcher sm = string.matcher(token);
					if (token.equals("__DECLARE_AS__")) {
						result.add(new Token(TokenType.VAR_DECL, line.get(current_token++)));
						PreprocessData.addPlaceholderFunction(result.get(result.size() - 1).name);
					
					} else if (token.equals("__MINUS__")) {
						result.add(new Token(TokenType.MINUS, line.get(current_token++)));
						PreprocessData.addPlaceholderFunction(result.get(result.size() - 1).name);
					
					} else if (token.equals("__PLUS__")) {
						result.add(new Token(TokenType.PLUS, line.get(current_token++)));
						PreprocessData.addPlaceholderFunction(result.get(result.size() - 1).name);
					
					} else if (token.equals("__NEGATIVE__")) {
						result.add(new Token(TokenType.NEGATIVE, line.get(current_token++)));
						PreprocessData.addPlaceholderFunction(result.get(result.size() - 1).name);
					
					} else if (token.equals("__POSITIVE__")) {
						result.add(new Token(TokenType.POSITIVE, line.get(current_token++)));
						PreprocessData.addPlaceholderFunction(result.get(result.size() - 1).name);
					
					} else if (token.equals("__TIMES__")) {
						result.add(new Token(TokenType.TIMES, line.get(current_token++)));
						PreprocessData.addPlaceholderFunction(result.get(result.size() - 1).name);
					
					} else if (token.equals("__MODULUS__")) {
						result.add(new Token(TokenType.MODULUS, line.get(current_token++)));
						PreprocessData.addPlaceholderFunction(result.get(result.size() - 1).name);
					
					} else if (token.equals("__OVER__")) {
						result.add(new Token(TokenType.OVER, line.get(current_token++)));
						PreprocessData.addPlaceholderFunction(result.get(result.size() - 1).name);
					
					} else if (token.equals("__NULL__")) {
						result.add(new Token(TokenType.NULL, line.get(current_token++)));
						PreprocessData.addPlaceholderFunction(result.get(result.size() - 1).name);
					
					} else if (token.equals("__BREAK__")) {
						result.add(new Token(TokenType.BREAK, line.get(current_token++)));
						PreprocessData.addPlaceholderFunction(result.get(result.size() - 1).name);
					
					} else if (token.equals("__CONTINUE__")) {
						result.add(new Token(TokenType.CONTINUE, line.get(current_token++)));
						PreprocessData.addPlaceholderFunction(result.get(result.size() - 1).name);
					
					} else if (token.equals("__RETURN__")) {
						result.add(new Token(TokenType.RETURN, line.get(current_token++)));
						PreprocessData.addPlaceholderFunction(result.get(result.size() - 1).name);
					
					} else if (token.equals("__IN__")) {
						result.add(new Token(TokenType.IN, line.get(current_token++)));
						PreprocessData.addPlaceholderFunction(result.get(result.size() - 1).name);
					
					} else if (token.equals("__SET_TO__")) {
						result.add(new Token(TokenType.VAR_UPDATE, line.get(current_token++)));
						PreprocessData.addPlaceholderFunction(result.get(result.size() - 1).name);
					
					} else if (token.equals("__ELEMENT_AT_FROM__")) {
						result.add(new Token(TokenType.ARRAY_ELEMENT, line.get(current_token++)));
						PreprocessData.addPlaceholderFunction(result.get(result.size() - 1).name);
					
					} else if (token.equals("__IDENTICAL_TO__")) {
						result.add(new Token(TokenType.IDT, line.get(current_token++)));
						PreprocessData.addPlaceholderFunction(result.get(result.size() - 1).name);
					
					} else if (token.equals("__NOT_IDENTICAL_TO__")) {
						result.add(new Token(TokenType.NIDT, line.get(current_token++)));
						PreprocessData.addPlaceholderFunction(result.get(result.size() - 1).name);
					
					} else if (token.equals("__LESS_THAN__")) {
						result.add(new Token(TokenType.LT, line.get(current_token++)));
						PreprocessData.addPlaceholderFunction(result.get(result.size() - 1).name);
					
					} else if (token.equals("__LESS_THAN_OR_EQUAL_TO__")) {
						result.add(new Token(TokenType.LEQ, line.get(current_token++)));
						PreprocessData.addPlaceholderFunction(result.get(result.size() - 1).name);
					
					} else if (token.equals("__GREATER_THAN__")) {
						result.add(new Token(TokenType.GT, line.get(current_token++)));
						PreprocessData.addPlaceholderFunction(result.get(result.size() - 1).name);
					
					} else if (token.equals("__GREATER_THAN_OR_EQUAL_TO__")) {
						result.add(new Token(TokenType.GEQ, line.get(current_token++)));
						PreprocessData.addPlaceholderFunction(result.get(result.size() - 1).name);
					
					} else if (token.equals("__EQUAL_TO__")) {
						result.add(new Token(TokenType.EQ, line.get(current_token++)));
						PreprocessData.addPlaceholderFunction(result.get(result.size() - 1).name);
					
					} else if (token.equals("__NOT_EQUAL_TO__")) {
						result.add(new Token(TokenType.NEQ, line.get(current_token++)));
						PreprocessData.addPlaceholderFunction(result.get(result.size() - 1).name);
					
					} else if (token.equals("__FALSE__")) {
						result.add(new Token(TokenType.FALSE, line.get(current_token++)));
						PreprocessData.addPlaceholderFunction(result.get(result.size() - 1).name);
					
					} else if (token.equals("__TRUE__")) {
						result.add(new Token(TokenType.TRUE, line.get(current_token++)));
					
					} else if (token.equals("__OR__")) {
						result.add(new Token(TokenType.OR, line.get(current_token++)));
						PreprocessData.addPlaceholderFunction(result.get(result.size() - 1).name);
					
					} else if (token.equals("__AND__")) {
						result.add(new Token(TokenType.AND, line.get(current_token++)));
					
					} else if (token.equals("__NOT__")) {
						result.add(new Token(TokenType.NOT, line.get(current_token++)));
						PreprocessData.addPlaceholderFunction(result.get(result.size() - 1).name);
					
					//else if (token.equals("__CONCATENATE__"))
					//	result.add(new Token(TokenType.CONCAT, line.get(current_token++)));
					
					} else if (token.equals("__ASSERT__")) {
						result.add(new Token(TokenType.ASSERT, line.get(current_token++)));
						PreprocessData.addPlaceholderFunction(result.get(result.size() - 1).name);
					
					} else if (token.equals("__BITWISE_NOT__")) {
						result.add(new Token(TokenType.BITWISE_NOT, line.get(current_token++)));
						PreprocessData.addPlaceholderFunction(result.get(result.size() - 1).name);
					
					} else if (token.equals("__BITWISE_OR__")) {
						result.add(new Token(TokenType.BITWISE_OR, line.get(current_token++)));
						PreprocessData.addPlaceholderFunction(result.get(result.size() - 1).name);
					
					} else if (token.equals("__BITWISE_XOR__")) {
						result.add(new Token(TokenType.BITWISE_XOR, line.get(current_token++)));
						PreprocessData.addPlaceholderFunction(result.get(result.size() - 1).name);
					
					} else if (token.equals("__BITWISE_AND__")) {
						result.add(new Token(TokenType.BITWISE_AND, line.get(current_token++)));
						PreprocessData.addPlaceholderFunction(result.get(result.size() - 1).name);
					
					} else if (token.equals("__NEW__")) {
						result.add(new Token(TokenType.NEW, line.get(current_token++)));
						PreprocessData.addPlaceholderFunction(result.get(result.size() - 1).name);
					
					} else if (token.equals("__SET_TO__")) {
						result.add(new Token(TokenType.VAR_UPDATE, line.get(current_token++)));
						PreprocessData.addPlaceholderFunction(result.get(result.size() - 1).name);
					
					// Function Declaration: Needed for scope change
					} else if (token.equals("__FUNCTION__")) {
						result.add(new Token(TokenType.FUNCTION_DECL, line.get(current_token++)));
						PreprocessData.addPlaceholderFunction(result.get(result.size() - 1).name);
					
					} else if (token.equals("__THEN__")) {
						result.add(new Token(TokenType.THEN, line.get(current_token++)));
						PreprocessData.addPlaceholderFunction(result.get(result.size() - 1).name);
					
					} else if (token.equals("__DO__")) {
						result.add(new Token(TokenType.DO, line.get(current_token++)));
						PreprocessData.addPlaceholderFunction(result.get(result.size() - 1).name);
					
					} else if (token.equals("__ELSE__")) {
						result.add(new Token(TokenType.ELSE, line.get(current_token++)));
						PreprocessData.addPlaceholderFunction(result.get(result.size() - 1).name);
					
					} else if (token.equals("__ELSE_IF__")) {
						result.add(new Token(TokenType.ELSE_IF, line.get(current_token++)));
						PreprocessData.addPlaceholderFunction(result.get(result.size() - 1).name);
					
					} else if (token.equals("__CLASS__")) {
						result.add(new Token(TokenType.CLASS_DECL, line.get(current_token++)));
						PreprocessData.addPlaceholderFunction(result.get(result.size() - 1).name);
						
					} else if (token.equals("__EMPTY_STRING__")) {
						result.add(new Token(TokenType.EMPTY_STRING, line.get(current_token++)));
					
					} else if (sm.find())
						result.add(new Token(TokenType.STRING, line.get(current_token++)));
					
					/*****************Control Flow Beginning*****************************/
					else if (token.equals("__LOOP_UP_FROM_TO_BY__"))
						result.add(new Token(TokenType.LOOP, line.get(current_token++)));
						
					else if (token.equals("__LOOP_DOWN_FROM_TO_BY__"))
						result.add(new Token(TokenType.LOOP, line.get(current_token++)));
						
					else if (token.equals("__FOR__"))
						result.add(new Token(TokenType.FOR, line.get(current_token++)));
						
					else if (token.equals("__IF__"))
						result.add(new Token(TokenType.IF, line.get(current_token++)));
					
					else if (token.equals("__WHILE__"))
						result.add(new Token(TokenType.WHILE, line.get(current_token++)));
					
					else if (token.equals("__TRY__"))
						result.add(new Token(TokenType.TRY, line.get(current_token++)));
					
					else if (token.equals("__CATCH__"))
						result.add(new Token(TokenType.CATCH, line.get(current_token++)));
					
					else if (token.equals("__FINALLY__"))
						result.add(new Token(TokenType.FINALLY, line.get(current_token++)));
						
					else if (token.equals("__SWITCH__"))
						result.add(new Token(TokenType.SWITCH, line.get(current_token++)));
					
					else if (token.equals("__END__"))
						result.add(new Token(TokenType.END, line.get(current_token++)));
					
					// Visibility
					else if (token.equals("__GLOBAL__") || token.equals("__STATIC__") || 
							token.equals("__PUBLIC__") || token.equals("__PRIVATE__"))
						result.add(new Token(TokenType.VISIBILITY, line.get(current_token++)));
					
					else if (token.equals("__EMPTY_ARRAY__")) {
						result.add(new Token(TokenType.EMPTY_ARRAY, line.get(current_token++)));
						PreprocessData.addPlaceholderFunction(result.get(result.size() - 1).name);
					
					// If the next token is an opening parentheses then we are dealing with a function or a class instance
					} else if (lookAhead(1).equals("("))
						result.add(new Token(
							(lookAhead(-1).equals("__NEW__")) ? TokenType.CLASS_NAME : TokenType.FUNCTION_NAME, 
							line.get(current_token++)
						));
					
					// If the token preceding this one is the class declaration token then this is a class name
					else if (lookAhead(-1).equals("__CLASS__"))
						result.add(new Token(TokenType.CLASS_NAME, line.get(current_token++)));
						
					else if (PreprocessData.isKeyword(line.get(current_token)) || PreprocessData.isBuiltIn(line.get(current_token))) {
						result.add(new Token(TokenType.KEYWORD, line.get(current_token++)));
						
						// TODO: Not sure about this, might need to uncomment this it
						// PreprocessData.addPlaceholderFunction(result.get(result.size() - 1).name);
					
					} else
						result.add(new Token(TokenType.VARIABLE, line.get(current_token++)));
					
				} else if (token.equals("("))
					result.add(new Token(TokenType.OP_PAREN, line.get(current_token++)));
				
				else if (token.equals("^")) {
					current_token++;
					result.add(new Token(TokenType.BITWISE_XOR, "__BITWISE_XOR__"));
					PreprocessData.addPlaceholderFunction(result.get(result.size() - 1).name);
				
				} else if (token.equals("|")) {
					if (lookAhead(1).equals("|")) {
						result.add(new Token(TokenType.OR, "__OR__"));
						current_token++;
					} else {
						result.add(new Token(TokenType.BITWISE_OR, "__BITWISE_OR__"));
					}
					current_token++;
					PreprocessData.addPlaceholderFunction(result.get(result.size() - 1).name);
					
				} else if (token.equals("&")) {
					if (lookAhead(1).equals("&")) {
						result.add(new Token(TokenType.AND, "__AND__"));
						current_token++;
					} else {
						result.add(new Token(TokenType.BITWISE_AND, "__BITWISE_AND__"));
					}
					PreprocessData.addPlaceholderFunction(result.get(result.size() - 1).name);
					current_token++;
				
				} else if (token.equals(")"))
					result.add(new Token(TokenType.CL_PAREN, line.get(current_token++)));
				
				else if (token.equals(","))
					result.add(new Token(TokenType.COMMA, line.get(current_token++)));
				
				else if (token.equals("+")) {
					if ((prev != null) && (precedesUnaryOp(prev.type, result)))
						result.add(new Token(TokenType.POSITIVE, "__POSITIVE__"));
					else 
						result.add(new Token(TokenType.PLUS, "__PLUS__"));
					current_token++;
					PreprocessData.addPlaceholderFunction(result.get(result.size() - 1).name);
				
				// This might not be necessary, check Substitute.replace line 16
				} else if (token.equals("__{_}__")) {
					current_token++;
					result.add(new Token(TokenType.ARRAY_LIT, "__ARRAY__"));
					PreprocessData.addPlaceholderFunction(result.get(result.size() - 1).name);
				
				} else if (token.equals(";"))
					result.add(new Token(TokenType.SEMI_COLON, line.get(current_token++)));
				
				else if (token.equals("-")) {
					if ((prev != null) && (precedesUnaryOp(prev.type, result)))
						result.add(new Token(TokenType.NEGATIVE, "__NEGATIVE__"));
					else 
						result.add(new Token(TokenType.MINUS, "__MINUS__"));
					current_token++;
					PreprocessData.addPlaceholderFunction(result.get(result.size() - 1).name);
				
				} else if (token.equals("*")) {
					current_token++;
					result.add(new Token(TokenType.TIMES, "__TIMES__"));
					PreprocessData.addPlaceholderFunction(result.get(result.size() - 1).name);
				
				} else if (token.equals("/")) {
					current_token++;
					result.add(new Token(TokenType.OVER, "__OVER__"));
					PreprocessData.addPlaceholderFunction(result.get(result.size() - 1).name);
				
				} else if (token.equals("%")) {
					current_token++;
					result.add(new Token(TokenType.MODULUS, "__MODULUS__"));
					PreprocessData.addPlaceholderFunction(result.get(result.size() - 1).name);
				
				// ! / != / !== 
				} else if (token.equals("!")) {
					// Not Equal To 
					if (lookAhead(1).equals("=")) {
						// Not Identical To
						if (lookAhead(2).equals("=")) {
							result.add(new Token(TokenType.NIDT, "__NOT_IDENTICAL_TO__"));
							current_token++;
						} else {
							result.add(new Token(TokenType.NEQ, "__NOT_EQUAL_TO__"));
						}
						current_token++;
					} else {
						result.add(new Token(TokenType.NOT, "__NOT__"));
					}
					current_token++;
					PreprocessData.addPlaceholderFunction(result.get(result.size() - 1).name);
				
				// = | == | ===
				} else if (token.equals("=")) {
					//== / ===
					if (lookAhead(1).equals("=")) {
						if (lookAhead(2).equals("=")) {
							current_token++;
							result.add(new Token(TokenType.IDT, "__IDENTICAL_TO__"));
						} else {
							result.add(new Token(TokenType.EQ, "__EQUAL_TO__"));
						}
						current_token++;
						PreprocessData.addPlaceholderFunction(result.get(result.size() - 1).name);
					// = 
					} else {
						int size = result.size();
						result.add(size - 1, new Token(TokenType.VAR_UPDATE, "__SET_TO__"));
						PreprocessData.addPlaceholderFunction("__SET_TO__");
						result.add(size, new Token(TokenType.OP_PAREN, "("));
						result.add(new Token(TokenType.CL_PAREN, ")"));
					}
					current_token++;
					
				// > | >=
				// TODO: Consider >> | <<
				} else if (token.equals(">")) {
					String s = "__GREATER_THAN__";
					if (lookAhead(1).equals("=")) {
						current_token++;
						result.add(new Token(TokenType.GEQ, "__GREATER_THAN_OR_EQUAL_TO__"));	
					} else {
						result.add(new Token(TokenType.EQ, s));
					}
					current_token++;
					PreprocessData.addPlaceholderFunction(result.get(result.size() - 1).name);
					
				// < | <=
				} else if (token.equals("<")) {
					if (lookAhead(1).equals("=")) {
						current_token++;
						result.add(new Token(TokenType.LEQ, "__LESS_THAN_OR_EQUAL_TO__"));
					} else {
						result.add(new Token(TokenType.LT, "__LESS_THAN__"));
					}
					current_token++;
					PreprocessData.addPlaceholderFunction(result.get(result.size() - 1).name);
				} else {
					result.add(new Token(TokenType.OTHER, line.get(current_token++)));
					PreprocessData.addPlaceholderFunction(result.get(result.size() - 1).name);
				}
				prev = (result.size() > 0) ? result.get(result.size() - 1) : null;
			}
			
			current_token = 0;
			result.add(new Token(TokenType.NEWLINE, null));
			if (nextLine() == null)
				result.add(new Token(TokenType.EOF, null));
		}
		
		return result;
	}
	
	public void print() {
		for (Token token : tagged) {
			if (token.type == TokenType.NEWLINE)
				System.out.println();
			else 
				System.out.print(token + " ");
		}
	}
	
	public Token nextToken() {
		return (index < tagged.size()) ? tagged.get(index++) : null;
	}
	
	public Token peekNext() {
		return (index < tagged.size()) ? tagged.get(index) : null;
	}
	
	/**
	 * Searches for a specific TokenType, needle, or terminate if you see the stopping TokenType
	 * 
	 * @param needle		- The TokenType being searched for
	 * @param terminate		- The terminating TokenType
	 * @return				- True if tokentype is found, false otherwise
	 */
	public boolean searchOrTerminateOn(TokenType needle, TokenType stop) {
		int temp = index;
		while (temp < tagged.size()) {
			Token token = tagged.get(temp++);
			if (token.type == needle)
				return true;
			
			else if (token.type == stop)
				return false;
		}
		return false;
	}
	
	private String lookAhead(int pos) {
		ArrayList<String> line = tokens.get(current_line);
		if ((current_token + pos < line.size()) && (current_token + pos >= 0))
			return line.get(current_token + pos);
		
		return "";
	}
	
	public void reset() {
		current_line = current_token = index = 0;
	}
	
	private int tokenize(ArrayList<String> lines) {
		int size = 0;
		for (String line : lines) {
			ArrayList<String> line_tokens = new ArrayList<String>();
			String token = "";
			for (char c : line.toCharArray()) {
				// If the character is a space and the token is not an empty string
				Matcher vc = varchar.matcher("" + c);
				if ((c == ' ') && (token.length() > 0)) {
					line_tokens.add(token);
					token = "";
					
				// Ignore the spaces following other spaces or characters
				} else if ((vc.find()) || (c == '.')) {
					token += c;
					
				// If the token is an operation or a symbol other than what is in a variable
				} else if (c != ' ') {
					// If there is already a token in the running string
					if (token.length() > 0) {
						line_tokens.add(token);
						token = "";
					}
					
					// Then add the special character
					line_tokens.add("" + c);
					
				// Character that could be part of a variable
				}
			}
			// Add the last token if it's not an empty string
			if (token.length() > 0)
				line_tokens.add(token);
			
			tokens.add(line_tokens);
			size++;
		}
		return size;
	}
	
	private boolean precedesUnaryOp(TokenType token, ArrayList<Token> line) {
		switch (token) {
			case COMMA: case SEMI_COLON: case OP_PAREN: case RETURN:
				return true;
			case CL_PAREN:
				// Does this close a sub-expression
				int braces = 0;
				for (int i = line.size() - 1; i > 0; i--) {
					Token tk = line.get(i);
					if (tk.type == TokenType.NEWLINE) break;
					else if (tk.type == TokenType.OP_PAREN) braces--;
					else if (tk.type == TokenType.CL_PAREN) braces++;
					
					// No braces: Peek behind to see if 
					if (braces == 0) {
						Token prev = (i - 1 > -1) ? line.get(i - 1) : null;
						if (prev != null) {
							switch (prev.type) {
								case VAR_DECL: case VAR_UPDATE:
									return true;
								default:
									return false;
							}
						}
						return false;
					}
				}
				return false;
			default:
				return BinaryOperator(token) || UnaryOperator(token);
		}
	}
	
	public boolean ExpressionTerm(TokenType token) {
		switch (token) {
			case VARIABLE: case NUMBER: case STRING: case FALSE: case TRUE: case NULL: case EMPTY_ARRAY: case EMPTY_STRING:
				return true;
			default:
				return false;
		}
	}
	
	public boolean BinaryOperator(TokenType token) {
		switch (token) {
			case PLUS: case MINUS: case TIMES: case OVER: case MODULUS: case BITWISE_AND: case BITWISE_OR: case BITWISE_XOR:
				return true;
			default:
				return ComparisonOperation(token) || BooleanOperator(token);
		}
	}
	
	public boolean UnaryOperator(TokenType token) {
		switch (token) {
			case POSITIVE: case NEGATIVE: case BITWISE_NOT: case NOT:
				return true;
			default:
				return false;
		}
	}
	
	public boolean ComparisonOperation(TokenType token) {
		switch (token) {
			case NEQ: case EQ: case GT: case GEQ: case LT: case LEQ: case IDT: case NIDT:
				return true;
			default: 
				return false;
		}
	}
	
	public boolean BooleanOperator(TokenType token) {
		switch (token) {
			case AND: case OR:
				return true;
			default:
				return false;
		}
	}
	
	public boolean TerminatesExpression(TokenType token) {
		switch (token) {
			case NEWLINE: case COMMA: case SEMI_COLON: case CL_PAREN: case THEN: case DO:
				return true;
			default:
				return false;
		}
	}
	
	private ArrayList<String> nextLine() {
		return (++current_line < tot_lines) ? tokens.get(current_line) : null;
	}
	
	private boolean hasNext() {
		return (current_line < tot_lines) && (tokens.get(current_line).size() > current_token);
	}

}
