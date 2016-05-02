package com.pkoding.preprocess.substitute;

import java.util.*;
import java.util.regex.*;
import com.pkoding.universal.util.*;
import com.pkoding.universal.data.PreprocessData;
import com.pkoding.preprocess.warning.*;

public class Substitute {
	
	public static String replace(String origin, String key, String[] params) {
		// Turn key into function name
		String func_name = "__" + Services.join(key.toUpperCase().split(" "), "_") + "__";
		
		// Change the array declaration here
		if (func_name.equals("__{_}__"))
			func_name = "__ARRAY__";
		
		PreprocessData.addPlaceholderFunction(func_name);
		
		// If the number of parameters is equal to 2:
		//		The function has no parameters so use the function name
		if (params.length == 2) {
			// Another keyword
			return ((params[0] == null) ? "" : params[0] + " ") + func_name + 
				((params[params.length - 1] == null) ? "" : " " + params[params.length - 1]); 
		}
		
		// Take care of loop construct
		if (key.contains("loop")) {
			String[] temp = {"", "", "0", "", "1", ""};
			if (func_name.equals("__LOOP_UP_FROM_TO__")) {
				for (int i = 0; i < params.length - 1; i++)
					temp[i] = params[i];
				
				// Step
				temp[params.length - 1] = "1";
				temp[params.length] = params[params.length - 1];
				func_name = "__LOOP_UP_FROM_TO_BY__";
				params = temp;
				
			} else if (func_name.equals("__LOOP_DOWN_FROM_TO__")) {
				for (int i = 0; i < params.length - 1; i++)
					temp[i] = params[i];
				
				// Step
				temp[params.length - 1] = "__NEGATIVE__ 1";
				temp[params.length] = params[params.length - 1];
				func_name = "__LOOP_DOWN_FROM_TO_BY__";
				params = temp;
				
			} else if (func_name.equals("__LOOP_TO__")) {
				temp[0] = params[0];
				temp[1]	= params[1];									// Variable
				temp[2] = "0";											// Start
				temp[3] = params[2];									// Stop
				temp[4] = "1";											// Step
				temp[5] = params[params.length - 1];
				params = temp;
				func_name = "__LOOP_UP_FROM_TO_BY__";
			}
		}
		
		String[] used_params = new String[params.length - 2];
		
		// Get the used params and create the function
		for (int i = 1; i < params.length - 1; i++)
			used_params[i - 1] = params[i];
		
		// Used arguments are used to create the function and combined with string
		return ((params[0] == null) ? "" : params[0] + " ") + func_name + "(" + Services.join(used_params, ", ") 
				+ ")" + ((params[params.length - 1] == null) ? "" : " " + params[params.length - 1]);
	}
	
	public static Tuple<String, String[]> extract(Pattern keywords, Map<String, Pattern> suffixes, 
			Map<String, Pattern> substitutions, String input) {
		
		String key = capture(keywords, suffixes, input);
		if (key == null)
			return null;
		
		Pattern sub = substitutions.get(key);
		if (sub == null) {
			PreprocessData.addWarning(new MissingSuffixWarning(key));
			return null;
		}
		Matcher matcher = sub.matcher(input);
		String[] args = null;
		
		if (matcher.find()) {
			args = new String[matcher.groupCount()];
			for (int i = 1; i <= matcher.groupCount(); i++)
				args[i - 1] = matcher.group(i);
			
		} else {
			PreprocessData.addWarning(new MissingParamsWarning(key));
			return null;
		}
		
		return new Tuple<String, String[]>(key, args);
	}
	
	public static String capture(Pattern keywords, Map<String, Pattern> suffixes, String input) {
		if (input == null)
			return null;
		
		ArrayList<String> matches = new ArrayList<String>();
		Matcher matcher = keywords.matcher(input);
		if (matcher.find())
			for (int i = 1; i <= matcher.groupCount(); i++)
				if (matcher.group(i) != null)
					matches.add(matcher.group(i));
		
		if (matches.size() == 0)
			return null;
		
		Pattern extract = suffixes.get(Services.asString(matches));
		if (extract != null) {
			matcher = extract.matcher(keywords.matcher(input).replaceFirst(" "));
			if (matcher.find())
				for (int i = 1; i <= matcher.groupCount(); i++)
					if (matcher.group(i) != null)
						matches.add(matcher.group(i));
		}
		
		return Services.asString(matches);
	}

}
