package com.pkoding.recognizer.scope;

public class BlockScope extends Scope {

	public BlockScope(Scope parent, String name) {
		super(parent, name, ScopeName.BLOCK);
	}

}
