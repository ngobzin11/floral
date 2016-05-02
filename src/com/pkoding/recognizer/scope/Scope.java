package com.pkoding.recognizer.scope;

public abstract class Scope {
	
	public final String name;
	
	public final Scope parent;
	
	public final ScopeName type;
	
	public Scope(Scope parent, String name, ScopeName type) {
		this.parent = parent;
		this.type = type;
		this.name = name;
	}

}
