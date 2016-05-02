package com.pkoding.universal.util;

public class Triple<E, T, F> {
	
	public final E first;
	
	public final T second;
	
	public final F third;
	
	public Triple(E first, T second, F third) {
		this.first = first;
		this.second = second;
		this.third = third;
	}
	
	public String toString() {
		return "(" + ((first == null) ? null : first.toString()) + ", " + 
				((second == null) ? null : second.toString()) + ", " +
				((third == null) ? null : third.toString()) + ")";
	}

}
