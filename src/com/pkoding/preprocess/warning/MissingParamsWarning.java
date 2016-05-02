package com.pkoding.preprocess.warning;

import java.util.ArrayList;
import com.pkoding.universal.error.*;
import com.pkoding.universal.util.*;
import com.pkoding.universal.warning.*;

public class MissingParamsWarning implements PCWarning {
	
	private final String cause;
	
	private final ArrayList<PCSuggestion> suggestions;
	
	private final PCClarification clarifications;
	
	public MissingParamsWarning(String keyphrase) {
		Tuple<ArrayList<PCSuggestion>, PCClarification> data = build(keyphrase);
		
		suggestions = data.first;
		clarifications = data.second;
		cause = "The " + (Services.isPhrase(keyphrase) ? "keyphrase" : "keyword") + " (" + keyphrase + 
				") was used incorrectly:";
	}
	
	@Override
	public String cause() {
		return cause;
	}
	
	@Override
	public ArrayList<PCSuggestion> suggestions() {
		return suggestions;
	}
	
	@Override
	public PCClarification clarifications() {
		return clarifications;
	}

	@Override
	public Tuple<ArrayList<PCSuggestion>, PCClarification> build(String keyphrase) {
		return PCSuggestionRouter.suggest(type(), keyphrase);
	}

	@Override
	public WarningType type() {
		return WarningType.MISSING_PARAMS;
	}

	@Override
	public String name() {
		return "MissingParamsWarning";
	}
	
}