package com.pkoding.translator.error;

import com.pkoding.universal.data.PreprocessData;

public class TranslationError {
	
	private final TranslationErrorType type;
	private final String[] error;
	private final int line;
	
	public TranslationError(TranslationErrorType type, String[] error, int line) {
		this.error = error;
		this.type = type;
		this.line = line;
	}
	
	public String toString() {
		return type + " in line " + PreprocessData.getLineNumber(line) + ":\n\t" + 
				TranslationErrorList.format(type, error);
	}

}
