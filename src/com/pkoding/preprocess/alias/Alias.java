package com.pkoding.preprocess.alias;

import java.util.regex.Pattern;

public class Alias {
	
	public final Pattern regex;
	
	public final String output;
	
	public Alias(Pattern regex, String output) {
		this.regex = regex;
		this.output = output;
	}
	
	public String toString() {
		return "[" + regex.toString() + "\t-->\t" + output + "]";
	}

}
