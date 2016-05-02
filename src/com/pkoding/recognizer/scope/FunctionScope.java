package com.pkoding.recognizer.scope;

public class FunctionScope extends Scope {

	public FunctionScope(Scope parent, String name) {
		super(parent, name, ScopeName.FUNCTION);
	}

}
