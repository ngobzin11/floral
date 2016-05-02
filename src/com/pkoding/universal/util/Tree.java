package com.pkoding.universal.util;

public interface Tree {
	
	public Tree getParent();
	
	public Object getPayload();
	
	public int getChildCount();
	
	public Tree getChild(int pos);

}
