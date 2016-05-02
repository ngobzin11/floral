package com.pkoding.preprocess.exception;

import java.util.ArrayList;
import com.pkoding.universal.error.*;

public class InvalidOperationException extends PCException {

	private static final long serialVersionUID = 1L;
	
	private final String name = "InvalidOperationException";
	
	private final String cause;
	
	private final PCSuggestion suggestion;
	
	private final ArrayList<PCClarificationDetail> clarifications;
	
	public InvalidOperationException(String cause, PCSuggestion suggestion, 
			ArrayList<PCClarificationDetail> clarifications) {
		
		super();
		this.cause = cause;
		this.suggestion = suggestion;
		this.clarifications = clarifications;
	}
	
	@Override
	public String cause() {
		return cause;
	}
	
	@Override
	public PCSuggestion suggestion() {
		return suggestion;
	}
	
	@Override
	public ArrayList<PCClarificationDetail> clarifications() {
		return clarifications;
	}
	
	@Override
	public String name() {
		return name;
	}
	
	public String toString() {
		String str = name + ":\t" + cause + "\n";
		
		if (suggestion != null)
			str += suggestion + "\n";
		
		if (clarifications != null)
			for (PCClarificationDetail detail : clarifications)
				str += detail + "\n";
		
		return str;
	}

}
