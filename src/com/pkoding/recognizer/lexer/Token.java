package com.pkoding.recognizer.lexer;

import com.pkoding.recognizer.scope.*;

public class Token {
	
	public final TokenType type;
	
	public final String name;
	
	private Scope scope;
 	
	public Token(TokenType type, String name) {
		scope = new GlobalScope();
		this.type = type;
		this.name = name;
	}
	
	public void setScope(Scope scope) {
		this.scope = scope;
	}
	
	public Scope scope() {
		return scope;
	}
	
	public String toString() {
		if (type == TokenType.VARIABLE)
			return "(" + name + ", " + type + ", " + ((scope != null) ? scope.name : scope) + ")";
		
		return "(" + name + ", " + type + ")";
	}

}
