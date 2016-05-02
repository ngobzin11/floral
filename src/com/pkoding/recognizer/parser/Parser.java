package com.pkoding.recognizer.parser;

import java.util.*;

import com.pkoding.recognizer.error.SyntaxError;
import com.pkoding.recognizer.lexer.*;
import com.pkoding.recognizer.scope.*;
import com.pkoding.universal.data.PreprocessData;
import com.pkoding.universal.util.Services;
import com.pkoding.universal.util.Tuple;

public class Parser {
	
	private HashMap<String, Tuple<Integer, ArrayList<Token>>> token_map;
	private ArrayList<Integer> path = new ArrayList<Integer>();
	private Stack<Tuple<TokenType, Integer>> blocks;
	private Stack<TokenGroup> expr_stack;
	private VariableUseReason var_type;
	private final Lexer lexer;
	private Scope cur_scope;
	private int block_num;
	private int line = 0;
	private int depth;
	
	private HashMap<String, String> if_var_map = new HashMap<String, String>();
	private ArrayList<Token> var_value = new ArrayList<Token>();
	private boolean nxt_value = false, debug = false;
	
	public Parser(Lexer lexer) {
		token_map = new HashMap<String, Tuple<Integer, ArrayList<Token>>>();
		blocks = new Stack<Tuple<TokenType, Integer>>();
		expr_stack = new Stack<TokenGroup>();
		cur_scope = new GlobalScope();
		this.lexer = lexer;
	}
	
	public void execute() throws SyntaxError {
		Token token = lexer.nextToken();
		while ((token != null) && (token.type != TokenType.EOF)) {
			updateScope(token);
			
			if (debug) {
				System.out.println(token + "\t" + PreprocessData.getLineNumber(line));
				try {
					if (nxt_value)
						var_value.add(token);
					
					File(token);
				} catch (SyntaxError e) {
					if (debug)
						e.printStackTrace();
					else
						System.err.println(e.toString());
					break;
				}
				System.out.println("\n");
			} else {
				if (nxt_value)
					var_value.add(token);
				
				File(token);
			}
			token = lexer.nextToken();
		}
	}
	
	// Tuple Structure: (Match, Done)
	private Tuple<Boolean, Boolean> File(Token token) throws SyntaxError {
		return Statement(token, 0);
	}
	
	private Tuple<Boolean, Boolean> Statement(Token token, int depth) throws SyntaxError {
		if (debug) System.out.println(Services.indent("In Statement:\t" + path + "\t" + depth, depth));
		// If we've been down this path before
		if (depth < path.size()) {
			Tuple<Boolean, Boolean> done;
			switch (path.get(depth)) {
				case 0:
					done = SimpleStatement(token, depth + 1);
					if (done.first) {
						if (done.second) {
							// TODO: Check if stack only contains expression
							expr_stack = new Stack<TokenGroup>();
							return update(depth, 2, false);
						}
						return done;
					}
					throw(new SyntaxError("Incorrectly formed simple statement in line %s", line));
				case 1:
					done = CompoundStatement(token, depth + 1);
					if (done.first) {
						if (done.second) return update(depth, 2, false);
						return new Tuple<Boolean, Boolean>(true, false);
					}
					throw(new SyntaxError("Incorrectly formed compound statement in line %s", line));
				case 2:
					if (token.type == TokenType.NEWLINE) {
						line++;
						return remove(depth, true, true);
					}
					throw(new SyntaxError("Unexpected token " + token.name + " in line %s, expecting end of line", line));
				default:
			}
			return new Tuple<Boolean, Boolean>(false, false);
			
		// This is the first time down this path
		} else {
			boolean added = false;
			if (depth >= path.size()) {
				added = true;
				add(-1, false);
			}
			
			Tuple<Boolean, Boolean> done = SimpleStatement(token, depth + 1);
			if (done.first) return done.second ? update(depth, 2, false) : update(depth, 0, false);
			
			done = CompoundStatement(token, depth + 1);
			if (done.first) return update(depth, 1, done.second);
			
			// Remove -1 we just added
			if (added) return remove(depth, false, false);
		}
		return new Tuple<Boolean, Boolean>(false, false);
	}
	
	private Tuple<Boolean, Boolean> SimpleStatement(Token token, int depth) throws SyntaxError {
		if (debug) System.out.println(Services.indent("In Simple Statement:\t" + path + "\t" + depth, depth));
		if ((depth < path.size()) && (path.get(depth) != -1)) {
			Tuple<Boolean, Boolean> done;
			switch (path.get(depth)) {
				case 0:
					done = ExpressionStatement(token, depth + 1);
					return (done.first && done.second) ? remove(depth, true, true) : done;
				case 1: 
					done = FlowStatement(token, depth + 1);
					return (done.first && done.second) ? remove(depth, true, true) : done;
				case 2: 
					done = AssertStatement(token, depth + 1);
					return (done.first && done.second) ? remove(depth, true, true) : done;
				default: 
			}
		} else {
			boolean added = false;
			if (depth >= path.size()) {
				added = true;
				add(-1, false);
			}
			
			Tuple<Boolean, Boolean> done = ExpressionStatement(token, depth + 1);
			if (done.first) return update(depth, 0, done.second);
			
			done = FlowStatement(token, depth + 1);
			if (done.first) return done.second ? remove(depth, true, true) : update(depth, 1, false);
			
			done = AssertStatement(token, depth + 1);
			if (done.first) return update(depth, 2, done.second);
			
			// Remove -1 we just added
			if (added) return remove(depth, false, false);
		}
		return new Tuple<Boolean, Boolean>(false, false);
	}
	
	private Tuple<Boolean, Boolean> CompoundStatement(Token token, int depth) throws SyntaxError {
		if (debug) System.out.println(Services.indent("In Compound Statement:\t" + path + "\t" + depth, depth));
		if ((depth < path.size()) && (path.get(depth) != -1)) {
			Tuple<Boolean, Boolean> done;
			switch (path.get(depth)) {
				case 0: 
					done = IfStatement(token, depth + 1);
					return (done.second && done.first) ? remove(depth, true, true) : done;
				case 1:
					done = WhileStatement(token, depth + 1);
					return (done.second && done.first) ? remove(depth, true, true) : done;
				case 2:
					done = ForStatement(token, depth + 1);
					return (done.second && done.first) ? remove(depth, true, true) : done;
				case 3:
					done = TryStatement(token, depth + 1);
					return (done.second && done.first) ? remove(depth, true, true) : done;
				case 4:
					done = FunctionDefinition(token, depth + 1);
					return (done.second && done.first) ? remove(depth, true, true) : done;
				case 5:
					done = LoopConstruct(token, depth + 1);
					return (done.second && done.first) ? remove(depth, true, true) : done;
				case 6:
					done = ClassDefinition(token, depth + 1);
					return (done.second && done.first) ? remove(depth, true, true) : done;
				default: 
			}
		} else {
			boolean added = false;
			if (depth >= path.size()) {
				added = true;
				add(-1, false);
			}
			
			Tuple<Boolean, Boolean> done = IfStatement(token, depth + 1);
			if (done.first) return update(depth, 0, false);
			
			done = WhileStatement(token, depth + 1);
			if (done.first) return update(depth, 1, false);
			
			done = ForStatement(token, depth + 1);
			if (done.first) return update(depth, 2, false);
			
			done = TryStatement(token, depth + 1);
			if (done.first) return update(depth, 3, false);
			
			done = FunctionDefinition(token, depth + 1);
			if (done.first) return update(depth, 4, false);
			
			done = LoopConstruct(token, depth + 1);
			if (done.first) return update(depth, 5, false);
			
			done = ClassDefinition(token, depth + 1);
			if (done.first) return update(depth, 6, false);
			
			if (added) return remove(depth, false, false);
		}
		return new Tuple<Boolean, Boolean>(false, false);
	}
	
	private Tuple<Boolean, Boolean> ExpressionStatement(Token token, int depth) throws SyntaxError {
		if (debug) System.out.println(Services.indent("In Expression Statement:\t" + path + "\t" + depth, depth));
		if ((depth < path.size()) && (path.get(depth) != -1)) {
			Tuple<Boolean, Boolean> done;
			switch (path.get(depth)) {
				case 0:
					done = VariableDeclaration(token, depth + 1);
					return (done.second && done.first) ? remove(depth, true, true) : done;
				case 1:
					done = AssignmentStatement(token, depth + 1);
					return (done.second && done.first) ? remove(depth, true, true) : done;
				case 2:
					done = FunctionCall(token, depth + 1);
					return (done.second && done.first) ? remove(depth, true, true) : done;
				case 3:
					done = ClassInstance(token, depth + 1);
					return (done.second && done.first) ? remove(depth, true, true) : done;
				default: 
			}
		} else {
			boolean added = false;
			if (depth >= path.size()) {
				added = true;
				add(-1, false);
			}
			
			Tuple<Boolean, Boolean> done = VariableDeclaration(token, depth + 1);
			if (done.first) return update(depth, 0, false);
			
			done = AssignmentStatement(token, depth + 1);
			if (done.first) return update(depth, 1, false);
			
			done = FunctionCall(token, depth + 1);
			if (done.first) return update(depth, 2, false);
			
			done = ClassInstance(token, depth + 1);
			if (done.first) return update(depth, 3, false);
			
			// Remove -1 we just added
			if (added) return remove(depth, false, false);
		}
		return new Tuple<Boolean, Boolean>(false, false);
	}
	
	private Tuple<Boolean, Boolean> FlowStatement(Token token, int depth) throws SyntaxError {
		if (debug) System.out.println(Services.indent("In Flow Statement:\t" + path + "\t" + depth, depth));
		if (depth < path.size()) {
			switch (path.get(depth)) {
				case 1: 
					Tuple<Boolean, Boolean> done = ReturnStatement(token, depth + 1);
					return (done.first && done.second) ? remove(depth, true, true) : done;
				default: 
			}
		} else {
			boolean added = false;
			if (depth >= path.size()) {
				added = true;
				add(-1, false);
			}
			
			Tuple<Boolean, Boolean> done = BreakStatement(token, depth + 1);
			if (done.first) return (added) ? remove(depth, true, true) : done;
			
			done = ContinueStatement(token, depth + 1);
			if (done.first) return (added) ? remove(depth, true, true) : done;
			
			done = ReturnStatement(token, depth + 1);
			if (done.first) return done.second ? remove(depth, true, true) : update(depth, 1, false);
			
			// Remove the -1 we just added 
			if (added) return remove(depth, false, false);
		}
		return new Tuple<Boolean, Boolean>(false, false);
	}
	
	//	__ASSERT__ expr __COMPARISON_OP__ expr
	private Tuple<Boolean, Boolean> AssertStatement(Token token, int depth) throws SyntaxError {
		if (debug) System.out.println(Services.indent("In Assert Statement:\t" + path + "\t" + depth, depth));
		if (depth < path.size()) {
			Tuple<Boolean, Boolean> done;
			switch (path.get(depth)) {
				case 1: 
					done = Expression(token, depth + 1);
					if (done.first) return (done.second) ? update(depth, 2, false) : done;
					throw(new SyntaxError("Incorrectly formed assert statement expression in line %s", line));
				case 2: 
					if (lexer.ComparisonOperation(token.type)) return update(depth, 3, false);
					throw(new SyntaxError("Unexpected token " + token.name + " in line %s, expecting comparison operation", line));
				case 3: 
					done = Expression(token, depth + 1);
					if (done.first) return (done.second) ? remove(depth, true, true) : done;
					throw(new SyntaxError("Incorrectly formed assert statement expression in line %s", line));
				default: // Find a way to notify the parent that this was successful
			}
		} else {
			if (token.type == TokenType.ASSERT) return add(1, false);
		}
		return new Tuple<Boolean, Boolean>(false, false);
	}
	
	//	__IF__ test __THEN__  block (__ELSE_IF__ test __THEN__ block)* (__ELSE__ block)? __END__
	private Tuple<Boolean, Boolean> IfStatement(Token token, int depth) throws SyntaxError {
		if (debug) System.out.println(Services.indent("In If Statement:\t" + path + "\t" + depth, depth));
		if (depth < path.size()) {
			Tuple<Boolean, Boolean> done;
			switch (path.get(depth)) {
				case 1: 
					done = Expression(token, depth + 1);
					if (done.first) return done.second ? update(depth, 2, false) : done;
					throw(new SyntaxError("Incorrectly formed if condition in line %s", line));
				case 2: 
					if (token.type == TokenType.THEN) {
						blocks.push(new Tuple<TokenType, Integer>(TokenType.IF, depth));
						return update(depth, 3, false);
					}
					throw(new SyntaxError("Unexpected token " + token.name + " in line %s, expecting 'then' keyword", line));
				case 3: 
					done = Block(token, depth + 1);
					if (done.first) {
						if (done.second) {
							Token peek = lexer.peekNext();
							if (peek.type == TokenType.END) return update(depth, 10, false);
							else if (peek.type == TokenType.ELSE_IF) return update(depth, 4, false);
							return update(depth, 8, false);
						}
						return done;
					}
					throw(new SyntaxError("Incorrectly formed if block in line %s", line));
				case 4:
					if (token.type == TokenType.ELSE_IF) return update(depth, 5, false);
					throw(new SyntaxError("Unexpected token " + token.name + " in line %s, expecting 'else if' keyphrase", line));
				case 5: 
					done = Expression(token, depth + 1);
					if (done.first) return done.second ? update(depth, 6, false) : done;
					throw(new SyntaxError("Incorrectly formed if condition in line %s", line));
				case 6: 
					if (token.type == TokenType.THEN) {
						// Remove extra else if blocks in stack so only one remains
						if ((!blocks.empty()) && (blocks.peek().first == TokenType.ELSE_IF)) blocks.pop();
						
						blocks.push(new Tuple<TokenType, Integer>(token.type, depth));
						return update(depth, 7, false);
					}
					throw(new SyntaxError("Unexpected token " + token.name + " in line %s, "
						+ "expecting 'then' keyword to end else-if condition", line));
				case 7:
					done = Block(token, depth + 1);
					if (done.first) {
						if (done.second) {
							Token peek = lexer.peekNext();
							if (peek != null) {
								if (peek.type == TokenType.END) return update(depth, 10, false);
								else if (peek.type == TokenType.ELSE_IF) return update(depth, 4, false);
							}
							return update(depth, 8, false);
						}
						return done;
					}
					throw(new SyntaxError("Incorrectly formed else-if block in line %s", line));
				case 8: 
					if (token.type == TokenType.ELSE) {
						// Remove extra else if blocks in stack so only one remains
						if ((!blocks.empty()) && (blocks.peek().first == TokenType.ELSE_IF)) blocks.pop();
						
						blocks.push(new Tuple<TokenType, Integer>(TokenType.ELSE, depth));
						return update(depth, 9, false);
					}
					throw(new SyntaxError("Unexpected token " + token.name + " in line %s, "
							+ "expecting 'else' or 'else if' block or 'end' keyword", line));
				case 9:
					done = Block(token, depth + 1);
					if (done.first) return done.second ? update(depth, 10, false) : done;
					throw(new SyntaxError("Incorrectly formed else block in line %s", line));
				case 10: 
					if (token.type == TokenType.END) {
						// Pop all the way until you see an if block
						while (!blocks.empty())
							if (blocks.pop().first == TokenType.IF)
								break;
						
						return remove(depth, true, true);
					}
					throw(new SyntaxError("Unexpected token " + token.name + " in line %s, expecting 'end' keyword", line));
				default: // Find a way to notify the parent that this was successful
			}
		} else {
			if (token.type == TokenType.IF) return add(1, false);
		}
		return new Tuple<Boolean, Boolean>(false, false);
	}
	
	// __WHILE__ test __DO__ block __END__
	private Tuple<Boolean, Boolean> WhileStatement(Token token, int depth) throws SyntaxError {
		if (debug) System.out.println(Services.indent("In While Statement:\t" + path + "\t" + depth, depth));
		if (depth < path.size()) {
			Tuple<Boolean, Boolean> done;
			switch (path.get(depth)) {
				case 1: 
					done = Expression(token, depth + 1);
					if (done.first) return done.second ? update(depth, 2, false) : done;
					throw(new SyntaxError("Incorrectly formed while loop condition in line %s", line));
				case 2: 
					if (token.type == TokenType.DO) {
						blocks.push(new Tuple<TokenType, Integer>(TokenType.WHILE, depth));
						return update(depth, 3, false);
					}
					throw(new SyntaxError("Unexpected token " + token.name + " in line %s, expecting 'do' keyword", line));
				case 3: 
					done = Block(token, depth + 1);
					if (done.first) return done.second ? update(depth, 4, false) : done;
					throw(new SyntaxError("Incorrectly formed while loop block in line %s", line));
				case 4: 
					if (token.type == TokenType.END) {
						if (!blocks.empty()) blocks.pop();
						return remove(depth, true, true);
					}
					throw(new SyntaxError("Unexpected token " + token + " in line %s, expecting 'end' keyword", line));
				default: // Find a way to notify the parent that this was successful
			}
		} else {
			if (token.type == TokenType.WHILE) return add(1, false);
		}
		return new Tuple<Boolean, Boolean>(false, false);
	}
	
	private Tuple<Boolean, Boolean> LoopConstruct(Token token, int depth) throws SyntaxError {
		if (debug) System.out.println(Services.indent("In Loop Construct:\t" + path + "\t" + depth, depth));
		if (depth < path.size()) {
			Tuple<Boolean, Boolean> done;
			switch (path.get(depth)) {
				case 1:
					if (token.type == TokenType.OP_PAREN) return update(depth, 2, false);
					throw(new SyntaxError("Unexpected token " + token.name + " in line %s, " +
						"expecting opening parenthesis", line));
				case 2:
					done = ExpressionList(token, depth + 1);
					if (done.first) return done.second ? update(depth, 3, false) : done;
					throw(new SyntaxError("Incorrectly formed loop arguments in line %s", line));
				case 3:
					if (token.type == TokenType.CL_PAREN) {
						blocks.push(new Tuple<TokenType, Integer>(TokenType.LOOP, depth));
						return update(depth, 4, false);
					}
					throw(new SyntaxError("Unexpected token " + token.name + " in line %s, " +
						"expecting closing parenthesis", line));
				case 4:
					done = Block(token, depth + 1);
					if (done.first) return done.second ? update(depth, 5, false) : done;
					throw(new SyntaxError("Incorrectly formed for loop block in line %s", line));
				case 5:
					if (token.type == TokenType.END) {
						if (!blocks.empty()) blocks.pop();
						return remove(depth, true, true);
					}
					throw(new SyntaxError("Unexpected token " + token + " in line %s, expecting 'end' keyword", line));
				default:
			}
			
		} else if (token.type == TokenType.LOOP) return add(1, false);
		
		return new Tuple<Boolean, Boolean>(false, false);
	}
	
	// __FOR__ enhanced_for_args __DO__ block __END__
	private Tuple<Boolean, Boolean> ForStatement(Token token, int depth) throws SyntaxError {
		if (debug) System.out.println(Services.indent("In For Statement:\t" + path + "\t" + depth, depth));
		if (depth < path.size()) {
			Tuple<Boolean, Boolean> done;
			switch (path.get(depth)) {
				case 1: 
					if (token.type == TokenType.VARIABLE) return update(depth, 2, false);
					throw(new SyntaxError("Unexpected token " + token.name + " in enhanced for loop, line %s" + 
						". Expecting a variable", line));
				case 2:
					if (token.type == TokenType.IN) return update(depth, 3, false);
					throw(new SyntaxError("Missing 'in' keyword in enhanced for loop in line %s", line));
				case 3:
					done = Expression(token, depth + 1);
					if (done.first)
						return done.second ? update(depth, 4, false) : done;
					throw(new SyntaxError("Incorrectly formed expression in line %s", line));
				case 4:
					if (token.type == TokenType.DO) {
						blocks.push(new Tuple<TokenType, Integer>(TokenType.FOR, depth));
						return update(depth, 5, false);
					}
					throw(new SyntaxError("Unexpected token " + token + " in line %s, expecting 'do' keyword", line));
				case 5: 
					done = Block(token, depth + 1);
					if (done.first) return done.second ? update(depth, 6, false) : done;
					throw(new SyntaxError("Incorrectly formed for loop block in line %s", line));
				case 6: 
					if (token.type == TokenType.END) {
						if (!blocks.empty()) blocks.pop();
						return remove(depth, true, true);
					}
					throw(new SyntaxError("Unexpected token " + token + " in line %s, expecting 'end' keyword", line));
				default: // Find a way to notify the parent that this was successful
			}
			
		} else if (token.type == TokenType.FOR) return add(1, false);
		
		return new Tuple<Boolean, Boolean>(false, false);
	}
	
	//	__TRY__ block (__CATCH__ expr_list block)* (__FINALLY__ block)? __END__
	private Tuple<Boolean, Boolean> TryStatement(Token token, int depth) throws SyntaxError {
		if (debug) System.out.println(Services.indent("In Try Statement:\t" + path + "\t" + depth, depth));
		if (depth < path.size()) {
			Tuple<Boolean, Boolean> done;
			switch (path.get(depth)) {
				case 1: 
					done = Block(token, depth + 1);
					if (done.first) return done.second ? update(depth, 2, false) : done;
					throw(new SyntaxError("Incorrectly formed try block in line %s", line));
				case 2: 
					if (token.type == TokenType.CATCH) return update(depth, 3, false);
					throw(new SyntaxError("Unexpected token " + token + " in line %s, expecting a catch block", line));
				case 3: 
					done = FormalParameterList(token, depth + 1);
					if (done.first) return done.second ? update(depth, 4, false) : done;
					throw(new SyntaxError("Incorrectly formed catch declaration in line %s, expecting at least "
						+ "one exception", line));
				case 4: 
					if (token.type == TokenType.THEN) return update(depth, 5, false);
					throw(new SyntaxError("Unexpected token " + token + " in line %s, expecting 'then' keyword", line));
				case 5: 
					done = Block(token, depth + 1);
					if (done.first) {
						if (done.second) {
							Token peek = lexer.peekNext();
							if (peek != null) {
								if (peek.type == TokenType.CATCH) return update(depth, 2, false);
								else if (peek.type == TokenType.FINALLY) return update(depth, 6, false);
							}
							return update(depth, 8, false);
						}
						return done;
					}
					throw(new SyntaxError("Incorrectly formed catch block. Check line %s", line));
				case 6: 
					if (token.type == TokenType.FINALLY) return update(depth, 7, false);
					throw(new SyntaxError("Unexpected token " + token + " in line %s, expecting a finally/catch "
						+ "block or the 'end' keyword", line));
				case 7:
					done = Block(token, depth + 1);
					if (done.first) return done.second ? update(depth, 8, false) : done;
					throw(new SyntaxError("Incorrectly formed finally block. Check line %s", line));
				case 8: 
					if (token.type == TokenType.END) return remove(depth, true, true);
					throw(new SyntaxError("Unexpected token " + token + " in line %s, expecting 'end' keyword to "
						+ "terminate try/catch block", line));
				default: // Find a way to notify the parent that this was successful
			}
		} else {
			if (token.type == TokenType.TRY) {
				blocks.push(new Tuple<TokenType, Integer>(token.type, depth));
				return add(1, false);
			}
		}
		return new Tuple<Boolean, Boolean>(false, false);
	}
	
	// class ClassName
	private Tuple<Boolean, Boolean> ClassDefinition(Token token, int depth) throws SyntaxError {
		if (debug) System.out.println(Services.indent("In Class Definition:\t" + path + "\t" + depth, depth));
		if (depth < path.size()) {
			Tuple<Boolean, Boolean> done;
			switch (path.get(depth)) {
				case 1:
					if (token.type == TokenType.CLASS_NAME) return update(depth, 2, false);
					throw(new SyntaxError("Unexpected token " + token + " in line %s, expecting a class name", line));
				case 2:
					if (token.type == TokenType.NEWLINE) {
						Token peek = lexer.peekNext();
						if (peek.type == TokenType.VISIBILITY) return update(depth, 3, false);			// Visibility is optional
						else if (peek.type == TokenType.FUNCTION_DECL) return update(depth, 4, false);
						else if (peek.type == TokenType.VAR_DECL) return update(depth, 5, false);
						else if (peek.type == TokenType.END) return update(depth, 6, false);
						throw(new SyntaxError("Unexpected token " + token + " in line %s, expecting end of line", line));
					}
					throw(new SyntaxError("Unexpected token " + token + " in line %s, expecting end of line", line));
				case 3:
					if (token.type == TokenType.VISIBILITY) return update(depth, 4, false);
					throw(new SyntaxError("Unexpected token " + token + " in line %s, expecting public/private", line));
				case 4:
					done = FunctionDefinition(token, depth + 1);
					if (done.first) return done.second ? update(depth, 2, false) : done;
					throw(new SyntaxError("Unexpected token " + token + " in line, expecting function definition", line));
				case 5:
					done = VariableDeclaration(token, depth + 1);
					if (done.first) return done.second ? update(depth, 2, false) : done;
					throw(new SyntaxError("Unexpected token " + token + " in line, expecting variable declaration", line));
				case 6:
					if (token.type == TokenType.END) return remove(depth, true, true);
					throw(new SyntaxError("Unexpected token " + token + " in line %s, expecting 'end' token to " +
							"end class declaration", line));
				default:
			}
		} else {
			if (token.type == TokenType.CLASS_DECL) {
				blocks.push(new Tuple<TokenType, Integer>(token.type, depth));
				return add(1, false);
			}
		}
		return new Tuple<Boolean, Boolean>(false, false);
	}
	
	// __FUNCTION__ func_name(expr_list?)
	private Tuple<Boolean, Boolean> FunctionDefinition(Token token, int depth) throws SyntaxError {
		if (debug) System.out.println(Services.indent("In Function Definition:\t" + path + "\t" + depth, depth));
		if (depth < path.size()) {
			Tuple<Boolean, Boolean> done;
			switch (path.get(depth)) {
				case 1: 
					if (token.type == TokenType.FUNCTION_NAME) return update(depth, 2, false);
					throw(new SyntaxError("Unexpected token " + token + " in line %s, expecting a function name after " +
						"declaration keyword 'function'", line));
				case 2:
					Token peek = lexer.peekNext();
					boolean no_args = (peek != null) && (peek.type == TokenType.CL_PAREN);
					if (token.type == TokenType.OP_PAREN) return update(depth, no_args ? 4 : 3, false);
					throw(new SyntaxError("Unexpected token " + token + " in line %s, expecting opening parenthesis "
						+ "after function name", line));
					
				case 3:
					// Let the parser know the next set of variables are function arguments
					var_type = VariableUseReason.ARGUMENT;			
					done = FormalParameterList(token, depth + 1);
					if (done.first) return done.second ? update(depth, 4, false) : done;
					throw(new SyntaxError("Incorrectly formed function parameter list in line %s", line));
					
				case 4:
					var_type = null;
					if (token.type == TokenType.CL_PAREN) return update(depth, 5, false);
					throw(new SyntaxError("Unexpected token " + token + " in line %s, expecting a closing parenthesis to " +
						"enclose function declaration arguments", line));
					
				case 5: 
					done = Block(token, depth + 1);
					if (done.first) {
						if (done.second) {
							Token next = lexer.peekNext();
							// TODO: What happens when next != END
							if ((next != null) && (next.type == TokenType.END))
								return update(depth, 6, false);
						}
						return done;
					}
					throw(new SyntaxError("Incorrectly formed function block in line %s", line));
					
				case 6: 
					if (token.type == TokenType.END) {
						//if (!((!blocks.empty()) && (blocks.pop().first == TokenType.FUNCTION_DECL)))
						//	throw(new SyntaxError("Dangling 'end' keyword in line " + PreprocessData.getLineNumber(line)));
						
						return remove(depth, true, true);
					}
					throw(new SyntaxError("Unexpected token " + token + " in line %s, expecting function-block "
						+ "closing keyword 'end'", line));
				default: // Find a way to notify the parent that this was successful
			}
		} else {
			if (token.type == TokenType.FUNCTION_DECL) {
				blocks.push(new Tuple<TokenType, Integer>(token.type, depth));
				return add(1, false);
			}
		}
		return new Tuple<Boolean, Boolean>(false, false);
	}
	
	// __DECLARE_AS__(VARIABLE) expr
	private Tuple<Boolean, Boolean> VariableDeclaration(Token token, int depth) throws SyntaxError {
		if (debug) System.out.println(Services.indent("In Variable Declaration:\t" + path + "\t" + depth, depth));
		if (depth < path.size()) {
			switch (path.get(depth)) {
				case 1: 
					if (token.type == TokenType.OP_PAREN) return update(depth, 2, false);
					throw(new SyntaxError("Unexpected token " + token + " in line %s, expecting opening parenthesis to " +
						"hold the variable being declared", line));
				case 2: 
					if (token.type == TokenType.VARIABLE) {
						logVariable(token, VariableUseReason.DECLARATION, depth);
						return update(depth, 3, false);
					}
					throw(new SyntaxError("Unexpected token " + token + " in line %s, expecting the variable being "
						+ "declared", line));
				case 3: 
					if (token.type == TokenType.CL_PAREN) {
						nxt_value = true;
						return update(depth, 4, false);
					}
					throw(new SyntaxError("Unexpected token " + token + " in line %s, expecting closing parenthesis", line));
				case 4: 
					Tuple<Boolean, Boolean> done = Expression(token, depth + 1);
					if (done.first) {
						if (done.second) {
							nxt_value = false;
							return remove(depth, true, true);
						}
						return done;
					}
					throw(new SyntaxError("Incorrectly formed variable declaration expression in line %s", line));
				default: // Find a way to notify the parent that this was successful
			}
		} else {
			if (token.type == TokenType.VAR_DECL) return add(1, false);
		}
		return new Tuple<Boolean, Boolean>(false, false);
	}
	
	// __SET_TO__( (VARIABLE | ELEMENT_AT_FROM) ) expr
	private Tuple<Boolean, Boolean> AssignmentStatement(Token token, int depth) throws SyntaxError {
		if (debug) System.out.println(Services.indent("In Assignment Statement:\t" + path + "\t" + depth, depth));
		if (depth < path.size()) {
			Tuple<Boolean, Boolean> done;
			switch (path.get(depth)) {
				case 1: 
					if (token.type == TokenType.OP_PAREN) {
						Token next = lexer.peekNext();
						return update(depth, ((next != null) && (next.type == TokenType.ARRAY_ELEMENT)) ? 3 : 2, false);
					}
					throw(new SyntaxError("Unexpected token " + token.name + " in line %s, opening parenthesis " +
							"to hold variable or array element", line));
				case 2: 
					if (token.type == TokenType.VARIABLE) {
						logVariable(token, VariableUseReason.UPDATE, depth);
						return update(depth, 4, false);
					}
					throw(new SyntaxError("Unexpected token " + token.name + " in line %s, expecting a variable", line));
				case 3:
					done = ArrayElement(token, depth + 1);
					if (done.first) return done.second ? update(depth, 4, false) : done;
					throw(new SyntaxError("Unexpected token " + token.name + " in line %s, expecting an array element", line));
				case 4: 
					if (token.type == TokenType.CL_PAREN) {
						nxt_value = true;
						return update(depth, 5, false);
					}
					throw(new SyntaxError("Unexpected token " + token.name + " in line %s, expecting closing parenthesis", line));
				case 5: 
					done = Expression(token, depth + 1);
					if (done.first) {
						if (done.second) {
							nxt_value = false;
							return remove(depth, true, true);
						}
						return done;
					}
					throw(new SyntaxError("Incorrectly formed assignment statement expression in line %s", line));
				default: // Find a way to notify the parent that this was successful
			}
		} else {
			if (token.type == TokenType.VAR_UPDATE) return add(1, false);
		}
		return new Tuple<Boolean, Boolean>(false, false);
	}
	
	// TODO: FUNCTION_NAME ( expr_list? )
	private Tuple<Boolean, Boolean> FunctionCall(Token token, int depth) throws SyntaxError {
		if (debug) System.out.println(Services.indent("In Function Call:\t" + path + "\t" + depth, depth));
		if (depth < path.size()) {
			switch (path.get(depth)) {
				case 1: 
					Token peek = lexer.peekNext();
					boolean no_args = (peek != null) && (peek.type == TokenType.CL_PAREN);
					if (token.type == TokenType.OP_PAREN) return update(depth, no_args ? 3 : 2, false);
					throw(new SyntaxError("Unexpected token " + token.name + " in line %s, expecting opening parenthesis "
						+ "after function name", line));
				case 2: 
					Tuple<Boolean, Boolean> done = ExpressionList(token, depth + 1);
					if (done.first) return update(depth, done.second ? 3 : 2, false);
					throw(new SyntaxError("Incorrectly formed expression list in line %s", line));
				case 3: 
					if (token.type == TokenType.CL_PAREN) return remove(depth, true, true);
					throw(new SyntaxError("Unexpected token " + token.name + " in line %s, expecting closing parenthesis "
						+ "to end function call", line));
				default: // Find a way to notify the parent that this was successful
			}
		} else {
			if (token.type == TokenType.FUNCTION_NAME) return add(1, false);
		}
		return new Tuple<Boolean, Boolean>(false, false);
	}
	
	// __NEW__ CLASS_NAME ( expr_list? )
	private Tuple<Boolean, Boolean> ClassInstance(Token token, int depth) throws SyntaxError {
		if (debug) System.out.println(Services.indent("In Class Instance:\t" + path + "\t" + depth, depth));
		if (depth < path.size()) {
			switch (path.get(depth)) {
				case 1: 
					if (token.type == TokenType.CLASS_NAME) return update(depth, 2, false);
					throw(new SyntaxError("Unexpected token " + token.name + " in line %s, expecting a class name "
							+ "after 'new' keyword", line));
				case 2:
					Token peek = lexer.peekNext();
					boolean no_args = (peek != null) && (peek.type == TokenType.CL_PAREN);
					if (token.type == TokenType.OP_PAREN) return update(depth, no_args ? 4 : 3, false);
					throw(new SyntaxError("Unexpected token " + token.name + " in line %s, expecting an opening "
						+ "parenthesis after class name", line));
					
				case 3: 
					Tuple<Boolean, Boolean> done = ExpressionList(token, depth + 1);
					if (done.first) return update(depth, done.second ? 4 : 3, false);
					else { break; }
				case 4: 
					if (token.type == TokenType.CL_PAREN) return remove(depth, true, true);
					throw(new SyntaxError("Unexpected token " + token.name + " in line , expecting a closing "
						+ "parenthesis to end class instance", line));
					
				default: // Find a way to notify the parent that this was successful
			}
		} else {
			if (token.type == TokenType.NEW) return add(1, false);
		}
		return new Tuple<Boolean, Boolean>(false, false);
	}
	
	// __BREAK__
	private Tuple<Boolean, Boolean> BreakStatement(Token token, int depth) {
		if (debug) System.out.println(Services.indent("In Break Statement:\t" + path + "\t" + depth, depth));
		return (token.type == TokenType.BREAK) ? new Tuple<Boolean, Boolean>(true, true) : 
			new Tuple<Boolean, Boolean>(false, false);
	}
	
	// __CONTINUE__
	private Tuple<Boolean, Boolean> ContinueStatement(Token token, int depth) {
		if (debug) System.out.println(Services.indent("In Continue Statement:\t" + path + "\t" + depth, depth));
		return (token.type == TokenType.CONTINUE) ? new Tuple<Boolean, Boolean>(true, true) : 
			new Tuple<Boolean, Boolean>(false, false);
	}
	
	// __RETURN__ (expr)?
	private Tuple<Boolean, Boolean> ReturnStatement(Token token, int depth) throws SyntaxError {
		if (debug) System.out.println(Services.indent("In Return Statement:\t" + path + "\t" + depth, depth));
		if (depth < path.size()) {
			Tuple<Boolean, Boolean> done = Expression(token, depth + 1);
			if (done.first) return done.second ? remove(depth, true, true) : done;
			throw(new SyntaxError("Unexpected token " + token.name + " in return statement expression in line %s", line));
		} else {
			if (token.type == TokenType.RETURN) {
				Token peek = lexer.peekNext();
				if ((peek != null) && (peek.type == TokenType.NEWLINE))
					return new Tuple<Boolean, Boolean>(true, true);
				
				return add(1, false);
			}
		}
		return new Tuple<Boolean, Boolean>(false, false);
	}
	
	private Tuple<Boolean, Boolean> Expression(Token token, int depth) throws SyntaxError {
		if (debug) System.out.println(Services.indent("In Expression:\t" + path + "\t" + depth, depth));
		if (depth < path.size()) {
			Tuple<Boolean, Boolean> done;
			switch (path.get(depth)) {
				case 1: 
					done = ClassInstance(token, depth + 1);
					if (done.first) return expressionDone(done, depth);
					throw(new SyntaxError("Incorrectly formed class instance in line %s", line));
				case 2:
					done = ParenthesizedExpression(token, depth + 1);
					if (done.first) return expressionDone(done, depth);
					throw(new SyntaxError("Incorrectly formed parenthesized expression in line %s", line));
				case 3:
					done = FunctionCall(token, depth + 1);
					if (done.first) return expressionDone(done, depth);
					throw(new SyntaxError("Incorrectly formed function call in line %s", line));
				case 4:
					done = ArrayElement(token, depth + 1);
					if (done.first) return expressionDone(done, depth);
					throw(new SyntaxError("Incorrectly formed array element expression in line %s", line));
				default:
					done = VerifyExpression(token);
					return (done.first && done.second) ? remove(depth, true, true) : done;
			}
		} else {
			boolean added = false;
			if (depth >= path.size()) {
				added = true;
				add(-1, false);
			}
			
			if (token.type == TokenType.NEW) {
				ClassInstance(token, depth + 1);
				return update(depth, 1, false);
				
			} else if (token.type == TokenType.OP_PAREN) {
				if ((!expr_stack.empty()) && (expr_stack.peek() == TokenGroup.EXPR))
					throw(new SyntaxError("Consecutive expressions without separating operator in line %s", line));
				
				// Create the parenthesis 
				ParenthesizedExpression(token, depth + 1);
				return update(depth, 2, false);
				
			} else if (token.type == TokenType.FUNCTION_NAME) {
				FunctionCall(token, depth + 1);
				return update(depth, 3, false);
				
			} else if (token.type == TokenType.ARRAY_ELEMENT) {
				ArrayElement(token, depth + 1);
				return update(depth, 4, false);
			}
			
			if (added) remove(depth, false, false);
			
			Tuple<Boolean, Boolean> done = VerifyExpression(token);
			return done;
		}
	}
	
	// NEWLINE statement+
	private Tuple<Boolean, Boolean> Block(Token token, int depth) throws SyntaxError {
		if (debug) System.out.println(Services.indent("In Block:\t" + path + "\t" + depth, depth));
		if (depth < path.size()) {
			Tuple<Boolean, Boolean> done;
			switch (path.get(depth)) {
				case 1: 
					done = Statement(token, depth + 1);
					if (done.first) {
						if (done.second) {
							Token peek = lexer.peekNext();
							if ((peek != null) && (blockEnd(peek.type)))
								return remove(depth, true, true);
						}
						return new Tuple<Boolean, Boolean>(true, false);
					}
					// Because of Python we can't allow languages to have empty blocks
					throw(new SyntaxError("Incorrectly formed statement in line %s", line));
					
				default:
					throw(new SyntaxError("Unexpected token " + token.name + " in line %s, expecting the beginning "
						+ "of a simple or compound statement", line));
			}
		} else {
			if (token.type == TokenType.NEWLINE) {
				line++;
				return add(1, false);
			}
			throw(new SyntaxError("Unexpected token " + token.name + " in line %s. Start a block in a new line", line));
		}
	}
	
	/*
	// (var_declaration | var_update) ; expr ; expr
	private Tuple<Boolean, Boolean> StandardForArgs(Token token, int depth) throws SyntaxError {
		if (debug) System.out.println(Services.indent("In Standard For Arguments:\t" + path + "\t" + depth, depth));
		if (depth >= path.size())
			path.add(((token.type == TokenType.VAR_DECL)) ? 0 : 1);
		
		Tuple<Boolean, Boolean> done;
		switch (path.get(depth)) {
			case 0:
				done = VariableDeclaration(token, depth + 1);
				if (done.first) return done.second ? update(depth, 2, false) : done;
				throw(new SyntaxError("Incorrectly formed for loop variable declaration in line %s", line));
			case 1:
				done = AssignmentStatement(token, depth + 1);
				if (done.first) return done.second ? update(depth, 2, false) : done;
				throw(new SyntaxError("Incorrectly formed expression in line %s", line));
			case 2:
				if (token.type == TokenType.SEMI_COLON) return update(depth, 3, false);
				throw(new SyntaxError("Unexpected token " + token.name + " in line %s. Expecting ';' after for "
					+ "loop variable assignment", line));
				
			case 3:
				done = Expression(token, depth + 1);
				if (done.first) return update(depth, done.second ? 4 : 3, false);
				throw(new SyntaxError("Incorrectly formed expression in line %s", line));
			case 4:
				if (token.type == TokenType.SEMI_COLON) return update(depth, 5, false);
				throw(new SyntaxError("Expecting ';' after for loop condition expression in line %s", line));
				
			case 5: 
				done = ExpressionStatement(token, depth + 1);
				if (done.first) 
					return done.second ? remove(depth, true, true) : new Tuple<Boolean, Boolean>(true, false);
				throw(new SyntaxError("Unrecognized expression in for loop, line %s. Expecting a step expression "
					+ "like decrement [variable], increment [variable]", line));
			default: // Find a way to notify the parent that this was successful
		}
		return new Tuple<Boolean, Boolean>(false, false);
	}
	
	// VARIABLE __IN__ expr
	private Tuple<Boolean, Boolean> EnhancedForArgs(Token token, int depth) throws SyntaxError {
		if (debug) System.out.println(Services.indent("In Enhanced For Args:\t" + path + "\t" + depth, depth));
		if (depth >= path.size())
			path.add(0);
		
		switch (path.get(depth)) {
			case 0:
				if (token.type == TokenType.VARIABLE) return update(depth, 1, false);
				throw(new SyntaxError("Unexpected token " + token.name + " in enhanced for loop, line %s" + 
					". Expecting a variable", line));
			case 1:
				if (token.type == TokenType.IN) return update(depth, 2, false);
				throw(new SyntaxError("Missing 'in' keyword in enhanced for loop in line %s", line));
				
			case 2:
				Tuple<Boolean, Boolean> done = Expression(token, depth + 1);
				if (done.first)
					return done.second ? remove(depth, true, true) : update(depth, 2, false);
				throw(new SyntaxError("Incorrectly formed expression in line %s", line));
			default: // Find a way to notify the parent that this was successful
		}
		return new Tuple<Boolean, Boolean>(false, false);
	}
	*/
	
	private Tuple<Boolean, Boolean> ExpressionList(Token token, int depth) throws SyntaxError {
		if (debug) System.out.println(Services.indent("In Expression List:\t" + path + "\t" + depth, depth));
		if (depth >= path.size())
			path.add(0);
		
		Tuple<Boolean, Boolean> done;
		switch (path.get(depth)) {
			case 0:
				done = Expression(token, depth + 1);
				if (done.first) {
					Token next = lexer.peekNext();
					boolean comma = ((next != null) && (next.type == TokenType.COMMA));
					if (done.second)
						return comma ? update(depth, 1, false) : remove(depth, true, true);
					
					return update(depth, 0, false);
				}
				throw(new SyntaxError("Incorrectly formed expression in line %s", line));
			case 1:
				if (token.type == TokenType.COMMA) return update(depth, 2, false);
				throw(new SyntaxError("Unexpected token " + token.name + " in line %s, expecting a comma", line));
			case 2:
				done = Expression(token, depth + 1);
				if (done.first) {
					Token next = lexer.peekNext();
					boolean comma = ((next != null) && (next.type == TokenType.COMMA));
					if (done.second)
						return comma ? update(depth, 1, false) : remove(depth, true, true);
					
					return done;
				}
				throw(new SyntaxError("Incorrectly formed expression in line %s", line));
			default: // Find a way to notify the parent that this was successful
		}
		return new Tuple<Boolean, Boolean>(false, false);
	}
	
	private Tuple<Boolean, Boolean> FormalParameterList(Token token, int depth) throws SyntaxError {
		if (debug) System.out.println(Services.indent("In Formal Parameter List:\t" + path + "\t" + depth, depth));
		if (depth >= path.size())
			path.add(0);
		
		switch (path.get(depth)) {
			case 0:
				if (token.type == TokenType.VARIABLE) {
					Token next = lexer.peekNext();
					logVariable(token, var_type, depth);
					return ((next != null) && (next.type != TokenType.COMMA)) ? 
						remove(depth, true, true) : update(depth, 1, false);
				}
				throw(new SyntaxError("Unexpected token " + token.name + " in line %s, expecting a variable", line));
			case 1:
				if (token.type == TokenType.COMMA) return update(depth, 2, false);
				throw(new SyntaxError("Unexpected token " + token.name + " in line %s, expecting a comma", line));
			case 2:
				if (token.type == TokenType.VARIABLE) {
					Token next = lexer.peekNext();
					logVariable(token, var_type, depth);
					return ((next != null) && (next.type != TokenType.COMMA)) ? remove(depth, true, true) : update(depth, 1, false);
				}
				throw(new SyntaxError("Dangling comma in formal parameter list expression in line %s", line));
			default: // Find a way to notify the parent that this was successful
		}
		return new Tuple<Boolean, Boolean>(false, false);
	}
	
	private Tuple<Boolean, Boolean> ArrayElement(Token token, int depth) throws SyntaxError {
		if (debug) System.out.println(Services.indent("In Array Element:\t" + path + "\t" + depth, depth));
		if (depth < path.size()) {
			switch (path.get(depth)) {
				case 1:
					if (token.type == TokenType.OP_PAREN) return update(depth, 2, false);
					throw(new SyntaxError("Missing opening parenthesis in array element in line %s", line));
				case 2:
					Tuple<Boolean, Boolean> done = ExpressionList(token, depth + 1);
					if (done.first) {
						if (done.second) {
							Token next = lexer.peekNext();
							if ((next != null) && (next.type == TokenType.CL_PAREN))
								return update(depth, 3, false);
						}
						return done;
					}
					throw(new SyntaxError("Incorrectly formed expression list in array literal in line %s", line));
				case 3:
					if (token.type == TokenType.CL_PAREN) return remove(depth, true, true);
					throw(new SyntaxError("Missing closing parenthesis in literal array declaration in line %s", line));
				default: // Find a way to notify the parent that this was successful
					System.out.println("Went to default");
			}
		} else {
			if (token.type == TokenType.ARRAY_ELEMENT) return add(1, false);
		}
		return new Tuple<Boolean, Boolean>(false, false);
	}
	
	private Tuple<Boolean, Boolean> ParenthesizedExpression(Token token, int depth) throws SyntaxError {
		if (debug) System.out.println(Services.indent("In Parenethesized Expression:\t" + path + "\t" + depth, depth));
		if (depth < path.size()) {
			switch (path.get(depth)) {
				case 1:
					Tuple<Boolean, Boolean> done = Expression(token, depth + 1);
					if (done.first) return done.second ? update(depth, 2, false) : done;
					throw(new SyntaxError("Incorrectly formed expression in line %s", line));
				case 2:
					if (token.type == TokenType.CL_PAREN) return remove(depth, true, true);
					throw(new SyntaxError("Missing closing parenthesis in parenthesized expression in line %s", line));
					
				default: // Find a way to notify the parent that this was successful
			}
		} else {
			if (token.type == TokenType.OP_PAREN) return add(1, false);
		}
		return new Tuple<Boolean, Boolean>(false, false);
	}
	
	private Tuple<Boolean, Boolean> VerifyExpression(Token token) throws SyntaxError {
		if (debug) System.out.println(Services.indent("In Expression Verification:\t" + path + "\t" + depth, depth));
		if (lexer.ExpressionTerm(token.type)) {
			if (!expr_stack.empty()) {
				TokenGroup grp = expr_stack.pop();
				if (grp == TokenGroup.UNARY_OP) {
					Token peek = lexer.peekNext();
					if ((peek != null) && (lexer.TerminatesExpression(peek.type))) 
						return new Tuple<Boolean, Boolean>(true, true);
					
					else {
						expr_stack.push(TokenGroup.EXPR);
						return new Tuple<Boolean, Boolean>(true, false);
					}
				
				} else if (grp == TokenGroup.BINARY_OP) {
					if (!expr_stack.empty()) {
						if (expr_stack.pop() == TokenGroup.EXPR) {
							Token peek = lexer.peekNext();
							if ((peek != null) && (lexer.TerminatesExpression(peek.type)))  
								return new Tuple<Boolean, Boolean>(true, true);
							
							expr_stack.push(TokenGroup.EXPR);
							return new Tuple<Boolean, Boolean>(true, false);
						} else {
							throw(new SyntaxError("Unsupported consecutive operators in line %s", line));
						}
					} else {
						throw(new SyntaxError("Use of binary/comparison operator without left expression in line %s", line));
					}
				} else if (grp == TokenGroup.EXPR) {
					throw(new SyntaxError("Consecutive expressions without separating operator in line %s", line));
				} else {
					expr_stack.push(grp);
					expr_stack.push(TokenGroup.EXPR);
					return new Tuple<Boolean, Boolean>(true, false);
				}
			} else {
				Token peek = lexer.peekNext();
				if ((peek != null) && (lexer.TerminatesExpression(peek.type)))
					return new Tuple<Boolean, Boolean>(true, true);
				
				expr_stack.push(TokenGroup.EXPR);
				return new Tuple<Boolean, Boolean>(true, false);
			}
		} else if (lexer.UnaryOperator(token.type)) {
			if (!expr_stack.empty()) {
				expr_stack.push(TokenGroup.UNARY_OP);
				return new Tuple<Boolean, Boolean>(true, false);
				/* TODO: This generates an error for the valid expression: set m to 5 minus positive 9
				TokenGroup grp = expr_stack.peek();
				if (grp == TokenGroup.BINARY_OP || grp == TokenGroup.UNARY_OP) {
					throw(new SyntaxError("Unsupported consecutive operators in line %s", line));
				} else {
					expr_stack.push(TokenGroup.UNARY_OP);
					return new Tuple<Boolean, Boolean>(true, false);
				}
				*/
			} else {
				expr_stack.push(TokenGroup.UNARY_OP);
				// Token peek = lexer.peekNext(); (peek != null) && (peek.type == TokenType.NEWLINE)
				return new Tuple<Boolean, Boolean>(true, false);
			}
		} else if (lexer.BinaryOperator(token.type)) {
			if (!expr_stack.empty()) {
				TokenGroup grp = expr_stack.peek();
				if (grp != TokenGroup.EXPR) {
					System.err.println(token + "\t" + line);
					throw(new SyntaxError("Use of binary/comparison operator without left expression in line %s", line));
				} else {
					expr_stack.push(TokenGroup.BINARY_OP);
					return new Tuple<Boolean, Boolean>(true, false);
				}
			} else {
				throw(new SyntaxError("Use of binary/comparison operator without left expression in line %s", line));
			}
		} else {
			// TODO: Check stack to come up with more specific error
			if (!expr_stack.empty()) {
				if (expr_stack.peek() == TokenGroup.BINARY_OP)
					throw(new SyntaxError("Dangling operator in line %s, expecting an expression on the right", line));
				
			} else { 
				throw(new SyntaxError("Unexpected token " + (token.name == null ? "'newline'" : token.name) + " in line %s", line));
			}
		}
		return new Tuple<Boolean, Boolean>(false, false);
	}
	
	private void updateScope(Token token) throws SyntaxError {
		switch (token.type) {
			// function
			case FUNCTION_DECL:
				cur_scope = new FunctionScope(cur_scope, lexer.peekNext().name);
				depth++;
				break;
			
			// for|while|try|catch|finally|if
			case FOR: case WHILE: case TRY: case CATCH: case FINALLY: case LOOP:
				cur_scope = new BlockScope(cur_scope, cur_scope.name + token.name + block_num++);
				depth++;
				break;
			
			case THEN: 
				cur_scope = new BlockScope(cur_scope, cur_scope.name + "__IF__" + block_num++);
				depth++;
				break;
			
			case VARIABLE:
				token.setScope(cur_scope);
				break;
			
			case END:
				if (--depth == 0) {
					// Reset the block counter: So each function has it's own block counter
					block_num = 0;
					cur_scope = new GlobalScope();
					
				} else {
					if (cur_scope == null)
						throw(new SyntaxError("Unexpected token 'end' in line %s", line));
					cur_scope = cur_scope.parent;
				}
				break;
			
			// Just coz Java 7 wants a default case
			default:
				break;
		}
	}
	
	private enum TokenGroup {
		PAREN, EXPR, FUNC, INST, UNARY_OP, BINARY_OP
	}
	
	private enum VariableUseReason {
		DECLARATION, UPDATE, ARGUMENT, FOR_VAR
	}

	private void logVariable(Token token, VariableUseReason reason, int depth) throws SyntaxError {
		String key = token.scope().name + "_" + token.name;
		switch (reason) {
			case DECLARATION: 
				if (!token_map.containsKey(key)) {
					// If variable has been declared in upper block scope
					if (upperBlockScope(token, cur_scope) != null)
						System.err.println((debug ? "1286:\t" : "") + "Duplicate local variable [" + token.name + "] in line " + 
								PreprocessData.getLineNumber(line));
						//throw(new SyntaxError("Duplicate local variable [" + token.name + "] in line " + 
						//		PreprocessData.getLineNumber(line)));
					
					// Declared in an if statement
					if ((!blocks.empty()) && (blocks.peek().first == TokenType.IF))
						if_var_map.put(key + "_" + depth, "1");
					
					token_map.put(key, new Tuple<Integer, ArrayList<Token>>(0, null));
					
				// In an else-if statement
				} else if (!blocks.empty()) {
					String if_key = key  + "_" + depth;
					if (blocks.peek().first == TokenType.ELSE_IF) {
						// Update the variable in the map if it exists in the map otherwise 
						//	add a zero in there to make sure that the variable doesn't get hoisted
						if_var_map.put(
							if_key, 
							if_var_map.containsKey(if_key) ? if_var_map.get(if_key) + "1" : "01"
						);
					} else if (blocks.peek().first == TokenType.ELSE) {
						// If the variable has been declared in every accompanying block there won't
						//	be a 0 in the if value of that map
						if (if_var_map.containsKey(if_key)) {
							// "Hoist" the variable if it doesn't contain a 0 in it
							if (!if_var_map.get(if_key).contains("0"))
								token = hoist(token);
							
							// Remove the variable from the map
							if_var_map.remove(if_key);
						}
					} else {
						// throw(new SyntaxError("Duplicate local variable [" + token.name + "] in line " + 
						//		PreprocessData.getLineNumber(line)));
						System.err.println((debug ? "1318:\t" : "") + "Duplicate local variable [" + token.name + "] in line " + 
								PreprocessData.getLineNumber(line));
					}
				} else {
					// throw(new SyntaxError("Duplicate local variable [" + token.name + "] in line " + 
					//	PreprocessData.getLineNumber(line)));
					System.err.println((debug ? "1322:\t" : "") + "Duplicate local variable [" + token.name + "] in line " + 
							PreprocessData.getLineNumber(line));
				}
				break;
			case UPDATE:
				// Update Variable Usage Count If Already Declared
				if (token_map.containsKey(key)) {
					token_map.put(key, new Tuple<Integer, ArrayList<Token>>(token_map.get(key).first + 2, null));
					// prev_var = key;
					
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
						System.err.println(new SyntaxError((debug ? "1347:\t" : "") + "Undeclared local variable [" + 
								token.name + "] in line %s", line));
						// throw(new SyntaxError("Undeclared local variable [" + token.name + "] in line " + 
						//	PreprocessData.getLineNumber(line)));
					}
				}
				break;
			// Function argument and enhanced for loop variable
			case ARGUMENT: case FOR_VAR:
				token_map.put(key, new Tuple<Integer, ArrayList<Token>>(0, null));
				break;
			default:
				if (token_map.containsKey(key)) {
					token_map.put(
						key, 
						new Tuple<Integer, ArrayList<Token>>(
							token_map.get(key).first + 1, 
							token_map.get(key).second
						)
					);
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
						// throw(new SyntaxError("Undeclared local variable [" + token.name + "] in line " + 
						//		PreprocessData.getLineNumber(line)));
						System.err.println((debug ? "1381:\t" : "") + "Duplicate local variable [" + token.name + "] in line " + 
								PreprocessData.getLineNumber(line));
					}
				}
				break;
		}
	}
	
	private Scope upperBlockScope(Token token, Scope scope) {
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
	
	private boolean blockEnd(TokenType token) {
		switch (token) {
			case ELSE_IF: case ELSE: case END: case CATCH: case FINALLY:
				return true;
			default:
				return false;
		}
	}
	
	private Tuple<Boolean, Boolean> add(int i, boolean done) {
		path.add(i);
		return new Tuple<Boolean, Boolean>(true, done);
	}
	
	private Tuple<Boolean, Boolean> update(int index, int i, boolean done) {
		path.set(index, i);
		return new Tuple<Boolean, Boolean>(true, done);
	}
	
	// This could throw an error if index is not 
	private Tuple<Boolean, Boolean> remove(int index, boolean first, boolean second) {
		path.remove(index);
		return new Tuple<Boolean, Boolean>(first, second);
	}
	
	private Tuple<Boolean, Boolean> expressionDone(Tuple<Boolean, Boolean> done, int depth) {
		if (done.second) {
			Token next = lexer.peekNext();
			if ((next != null) && (lexer.BinaryOperator(next.type))) {
				expr_stack.push(TokenGroup.EXPR);
				return remove(depth, true, false);
			}
			return remove(depth, true, true);
		}
		return done;
	}
	
}
