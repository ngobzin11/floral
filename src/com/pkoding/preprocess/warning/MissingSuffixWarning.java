package com.pkoding.preprocess.warning;

import java.util.ArrayList;
import com.pkoding.universal.error.*;
import com.pkoding.universal.util.*;
import com.pkoding.universal.warning.*;

public class MissingSuffixWarning implements PCWarning {
	
	private final String cause;
	
	private final ArrayList<PCSuggestion> suggestions;
	
	private final PCClarification clarifications;
	
	public MissingSuffixWarning(String prefix) {
		Tuple<ArrayList<PCSuggestion>, PCClarification> data = build(prefix);
		
		suggestions = data.first;
		clarifications = data.second;
		cause = "You either used the reserved prefix (" + prefix + ") as a variable or you used one " +
				"of the following key-phrases incorrectly:";
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
	public Tuple<ArrayList<PCSuggestion>, PCClarification> build(String prefix) {
		return PCSuggestionRouter.suggest(type(), prefix);
	}

	@Override
	public WarningType type() {
		return WarningType.MISSING_SUFFIX;
	}

	@Override
	public String name() {
		return "MissingSuffixWarning";
	}

}
