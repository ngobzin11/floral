package com.pkoding.translator;

/**
 * A TokenTree is basically the parse-tree of a function  
 * 
 * @author ngobzin11
 *
 */
public class TokenTree {
	
	public TokenTree next_sibling;
	
	public TokenTree first_child;
	
	public final TokenTree parent;
	
	// This is the depth of the subtree
	public int subtree_depth = 0;
	
	public int num_children = 0;
	
	public boolean is_visited;
	
	public String value;
	
	public TokenTree(TokenTree parent, String value) {
		this.parent = parent;
		this.value = value;
	}
	
	public String toString() {
		return value + "\t-->\t" + num_children;
	}
}
