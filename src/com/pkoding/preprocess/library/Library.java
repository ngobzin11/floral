package com.pkoding.preprocess.library;

import java.util.*;
import java.util.regex.*;

import com.pkoding.universal.util.*;
import com.pkoding.preprocess.warning.*;
import com.pkoding.universal.data.PreprocessData;

public class Library {
	
	public final String name;
	
	public final String path;
	
	private Pattern keywords;
	
	private Map<String, ArrayList<String>> suffix_map = new HashMap<String, ArrayList<String>>();
	
	private HashMap<String, Pattern> suffixes = new HashMap<String, Pattern>();
	
	private HashMap<String, Pattern> substitutions = new HashMap<String, Pattern>();
	
	private HashSet<String> dependencies = new HashSet<String>();
	
	private HashMap<String, LibraryFunction> functions = new HashMap<String, LibraryFunction>();
	
	private ArrayList<String> used_code = new ArrayList<String>();
	
	public Library(String name, String path) {
		this.name = name;
		this.path = path;
	}
	
	public void addFunction(String keyword, String keyphrase, String phrase_regex, LibraryFunction function) {
		ArrayList<String> p_regex = (suffix_map.get(keyword) != null) ? suffix_map.get(keyword) : 
			new ArrayList<String>();
		p_regex.add(phrase_regex);
		
		suffix_map.put(keyword, p_regex);
		functions.put(keyphrase, function);
		substitutions.put(keyphrase, function.regex);
	}
	
	public void clearDependencies() {
		dependencies = new HashSet<String>();
	}
	
	public void addDependency(String dependency) {
		dependencies.add(dependency);
	}
	
	public HashSet<String> dependencies() {
		return dependencies;
	}
	
	public HashMap<String, LibraryFunction> functions() {
		return functions;
	}
	
	public void generate() {
		// Compile prefix pattern and phrase patterns
		String pref = RegexDefinition.NC_BEGIN + "(";
		int sz = suffix_map.size();
		for (Map.Entry<String, ArrayList<String>> entry : suffix_map.entrySet()) {
			String key = entry.getKey();
			String value = Services.join(entry.getValue(), "|");
			pref += (--sz == 0) ? key : key + "|";
			
			if (value != null)
				suffixes.put(key, Pattern.compile(value));
			
		}
		keywords = Pattern.compile(pref + ")" + RegexDefinition.NC_END);
		suffix_map = null;
	}
	
	public ArrayList<String> usedCode() {
		return (used_code.size() == 0) ? null : used_code;
	}
	
	public void clearCode() {
		used_code = new ArrayList<String>();
	}
	
	public String iterativeSubstitute(String input) {
		String untouched = input, output = null, prev = "";
		
		while ((capture(input) != null) && (!prev.equals(input))) {
			prev = input;
			String temp = Services.strip(substitute(input));
			if (temp != null)
				output = temp;
			
			input = temp;
		}
		
		return (output == null) ? untouched : output;
	}
	
	private String substitute(String input) { 
		String key = capture(input);
		
		if (key == null)
			return null;
		
		LibraryFunction function = functions.get(key);
		if (function == null) {
			PreprocessData.addWarning(new MissingSuffixWarning(name + " " + key));
			return null;
		}
		
		Matcher matcher = function.regex.matcher(input);
		if (matcher.find()) {
			String[] args = new String[matcher.groupCount()];
			for (int i = 1; i <= matcher.groupCount(); i++)
				args[i - 1] = matcher.group(i);
			
			Tuple<String, LibraryFunction> fcall = function.call(name, args);
			
			// If non-native code is used, add this code to the list of functions used from this library
			//		Only add function if it hasn't been used before
			LibraryFunction func = fcall.second;
			String func_name = "__" + func.name.toUpperCase() + "__";
			if ((!PreprocessData.containsLibraryFunction(func_name)) && (!PreprocessData.isNative(func_name))) {
				// Load library function dependencies
				if (func.dependencies != null)
					for (int i = 0; i < func.dependencies.length; i++)
						if (func.dependencies[i] != null)
							addDependency(func.dependencies[i]);
				
				// Add function code
				PreprocessData.addLibraryFunction(func_name);
				for (String line : func.code())
					used_code.add(line);
			}
			
			return fcall.first;
		}
		
		PreprocessData.addWarning(new MissingParamsWarning(name + " " + key));
		return null;
	}
	
	private String capture(String input) {
		if (input == null)
			return null;
		
		String pref_match = null;
		Matcher matcher = keywords.matcher(input);
		if (matcher.find())
			for (int i = 1; i <= matcher.groupCount(); i++)
				if (matcher.group(i) != null)
					pref_match = matcher.group(i);
		
		if (pref_match == null)
			return null;
		
		Pattern extract = suffixes.get(pref_match);
		if (extract != null) {
			ArrayList<String> matches = new ArrayList<String>();
			matcher = extract.matcher(input);
			if (matcher.find()) {
				for (int i = 1; i <= matcher.groupCount(); i++)
					if (matcher.group(i) != null)
						matches.add(matcher.group(i).toLowerCase());
			
				return Services.asString(matches);
			}
			
			// This part is meant for error handling: MissingSuffixWarning
			else
				return pref_match;
		}
		
		return null;
	}

}