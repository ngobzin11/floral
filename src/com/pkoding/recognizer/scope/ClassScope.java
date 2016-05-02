package com.pkoding.recognizer.scope;

public class ClassScope extends Scope {

	public ClassScope(Scope parent, String name) {
		super(parent, name, ScopeName.CLASS);
	}

}
