package com.pkoding.translator;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.*;

import com.pkoding.universal.data.PreprocessData;
import com.pkoding.universal.util.*;

public class LanguageLoader {
	
	private final Pattern con_pattern = Pattern.compile(RegexDefinition.C_LANG_CONS_FMT);
	private final Pattern sub_pattern = Pattern.compile(RegexDefinition.C_LANG_SUB_FMT);
	private HashMap<String, Tuple<Pattern, String>> constructs;
	private final String[] files = {"defined", "keywords"};
	private HashMap<String, String> substitutions;
	private final ArrayList<String> libraries;
	private final String root;
	
	public LanguageLoader(ArrayList<String> libraries) {
		root = PreprocessData.CMD + "substitutions/" + PreprocessData.getOutputLanguage() + "/";
		constructs = new HashMap<String, Tuple<Pattern, String>>();
		substitutions = new HashMap<String, String>();
		this.libraries = libraries;
		
		load();
	}
	
	private void load() {
		BufferedReader reader = null;
		// Read Core Substitutions
		for (int i = 0; i < files.length; i++) {
			try {
				reader = new BufferedReader(new FileReader(root + files[i]));
				String line = null;
				while ((line = reader.readLine()) != null) {
					// Ignore empty lines: Line is not actually trimmed because some lines intentionally have spaces on the right
					if (!"".equals(line.trim())) {
						Matcher m = sub_pattern.matcher(line);
						if (m.find()) {
							// Add only if it's going to be used
							if (PreprocessData.placeholderFunctions().contains(m.group(1)))
								substitutions.put(m.group(1), m.group(2));
							
						// TODO: This could be an error, but it could also be a comment
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		// Read Different Language Constructs
		try {
			reader = new BufferedReader(new FileReader(root + "constructs"));
			String line = null;
			while ((line = reader.readLine()) != null) {
				// Ignore empty lines: Line is not actually trimmed because some lines intentionally have spaces on the right
				if (!"".equals(line.trim())) {
					Matcher m = con_pattern.matcher(line);
					if (m.find()) {
						constructs.put(
							StatementToRegexParser.keyphrase(m.group(1).trim()).second,
							new Tuple<Pattern, String>(
								StatementToRegexParser.statementRegex(m.group(1).trim(), ""), 
								m.group(2).trim()
							)
						);
						
					// TODO: This could be an error, but it could also be a comment
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Read Loaded Libraries
		if (libraries != null)
			for (Entry<String, String> func : PreprocessData.getLibraryDefinitions().entrySet())
				if (PreprocessData.placeholderFunctions().contains(func.getKey()))
					substitutions.put(func.getKey(), func.getValue());
		
	}
	
	public HashMap<String, Tuple<Pattern, String>> constructs() {
		return constructs;
	}
	
	public HashMap<String, String> substitutions() {
		return substitutions;
	}

}
