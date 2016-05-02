package com.pkoding.translator.error;

import java.util.HashMap;

public class TranslationErrorList {
	
	private static final TranslationErrorType[] types = {
		TranslationErrorType.DuplicateLocalVariable, TranslationErrorType.UndeclaredLocalVariable, TranslationErrorType.TrailingEndMarker, 
		TranslationErrorType.MissingEndMarker, TranslationErrorType.UnexpectedCharacter, TranslationErrorType.MissingCharacter
	};
	
	private static final String[] error_fmts = {
		// Duplicate Local Variable
		"The variable [%s] has already been declared. Use \"set [variable] to [value]\" to update it.",
		// Unknown Variable Error
		"The variable [%s] is being used before declaration. Use \"declare [variable] as [value]\" to declare it",
		// Trailing End Marker
		"Extra end token does not match any opening block. Delete the token",
		// Missing End Marker
		"One or more of your opening blocks was not closed. Add the missing \"end\" to fix this",
		// Unexpected Character: First substitution is the token and the second substitution is a detailed explanation on the error
		"Unexpected character [%s]. %s",
		// Missing Character: First substitution is the token and the second substitution is a detailed explanation on the error
		"Missing character [%s]. %s"
	};
	
	private static final HashMap<TranslationErrorType, String> errors = load();
	
	public static String format(TranslationErrorType type, String[] args) {
		return String.format(errors.get(type), (Object[]) args);
	}
	
	private static HashMap<TranslationErrorType, String> load() {
		HashMap<TranslationErrorType, String> temp = new HashMap<TranslationErrorType, String>();
		for (int i = 0; i < types.length; i++)
			temp.put(types[i], error_fmts[i]);
		
		return temp;
	}
	
}
