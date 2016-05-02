package com.pkoding.universal.util;

public class Tuple<E, T> {
	
	public final E first;
	
	public final T second;
	
	public Tuple(E first, T second) {
		this.first = first;
		this.second = second;
	}
	
	public String toString() {
		return "(" + ((first == null) ? null : first.toString()) + ", " + 
				((second == null) ? null : second.toString()) + ")";
	}

}
