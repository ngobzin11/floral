package com.pkoding.preprocess.alias;

import java.util.*;
import java.util.regex.*;

import com.pkoding.universal.util.RegexDefinition;
import com.pkoding.universal.util.Services;

public class AliasCollection {
	
	private HashMap<String, ArrayList<String>> suffix_map = new HashMap<String, ArrayList<String>>();
	private HashMap<String, Pattern> substitutions = new HashMap<String, Pattern>();
	private HashMap<String, Pattern> suffixes = new HashMap<String, Pattern>();
	private HashMap<String, Alias> aliases = new HashMap<String, Alias>();
	private Pattern keywords;
	
	public void addAlias(String keyword, String keyphrase, String phrase_regex, Alias alias) {
		ArrayList<String> p_regex = (suffix_map.get(keyword) != null) ? suffix_map.get(keyword) : 
			new ArrayList<String>();
		p_regex.add(phrase_regex);
		
		suffix_map.put(keyword, p_regex);
		aliases.put(keyphrase, alias);
		substitutions.put(keyphrase, alias.regex);
	}
	
	public void generate() {
		String pref = RegexDefinition.NC_BEGIN + "(";
		int sz = suffix_map.size();
		for (Map.Entry<String, ArrayList<String>> entry : suffix_map.entrySet()) {
			String key = Services.escapeMetaCharacters(entry.getKey(), false);
			String value = Services.join(entry.getValue(), "|");
			pref += (--sz == 0) ? key : key + "|";
			
			if (value != null)
				suffixes.put(entry.getKey(), Pattern.compile(value));
		}
		keywords = Pattern.compile(pref + ")" + RegexDefinition.NC_END);
		suffix_map = null;
	}
	
	public boolean hasAliases() {
		return aliases.size() > 0;
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
		
		Alias alias = aliases.get(key);
		if (alias == null) {
			// TODO: Add Error Handling Here
			return null;
		}
		
		Matcher matcher = alias.regex.matcher(input);
		if (matcher.find()) {
			String[] args = new String[matcher.groupCount()];
			for (int i = 1; i <= matcher.groupCount(); i++)
				args[i - 1] = matcher.group(i);
			
			return ((args[0] == null) ? "" : Services.strip(args[0]) + " ") + 
						String.format(alias.output, (Object[]) args) + 
					((args[args.length - 1] == null) ? "" : " " + args[args.length - 1]);
		}
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
