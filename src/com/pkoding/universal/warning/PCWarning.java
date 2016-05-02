package com.pkoding.universal.warning;

import java.util.ArrayList;
import com.pkoding.universal.util.*;
import com.pkoding.universal.error.*;

public interface PCWarning {
	
	public Tuple<ArrayList<PCSuggestion>, PCClarification> build(String prefix);
	
	public String cause();
	
	public ArrayList<PCSuggestion> suggestions();
	
	public PCClarification clarifications();
	
	public WarningType type();
	
	public String name();
	
}
