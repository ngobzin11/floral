package com.pkoding.universal.error;

import java.util.ArrayList;

public abstract class PCException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public abstract String cause();

	public abstract PCSuggestion suggestion();

	public abstract ArrayList<PCClarificationDetail> clarifications();

	public abstract String name();

}
