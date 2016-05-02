package com.pkoding.recognizer.error;

import com.pkoding.universal.data.PreprocessData;

public class SyntaxError extends Exception {

	private static final long serialVersionUID = 1L;
	
	private final boolean preprocess;
	
	private final String error;
	
	public final int line;
	
	public SyntaxError(String error, int line) {
		super(error);
		this.preprocess = true;
		this.error = error;
		this.line = line;
	}
	
	public SyntaxError(String error, int line, boolean preprocess) {
		super(error);
		this.preprocess = preprocess;
		this.error = error;
		this.line = line;
	}
	
	public String toString() {
		return "SyntaxError:\t" + String.format(error, preprocess ? PreprocessData.getLineNumber(line) : line);
	}

}
