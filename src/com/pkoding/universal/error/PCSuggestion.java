package com.pkoding.universal.error;

public class PCSuggestion {
	
	public final String pattern;
	
	public final String keyphrase;
	
	public PCSuggestion(String keyphrase, String pattern) {
		this.keyphrase = keyphrase;
		this.pattern = pattern;
	}
	
	public String toString() {
		return keyphrase + ":\t" + pattern;
	}

}