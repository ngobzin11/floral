package com.pkoding.preprocess;

import java.util.ArrayList;
import java.util.regex.*;

import com.pkoding.preprocess.substitute.CoreReplace;
import com.pkoding.preprocess.library.*;
import com.pkoding.preprocess.mode.*;
import com.pkoding.universal.data.*;
import com.pkoding.universal.util.*;

public class Preprocessor {
	
	private final Pattern extra_brackets = Pattern.compile(RegexDefinition.C_SP_BRACKETS);
	private final Pattern alias_import = Pattern.compile(RegexDefinition.C_ALIAS_IMPORT); 
	private final Pattern mode_import = Pattern.compile(RegexDefinition.C_MODE_IMPORT);
	private final Pattern lib_import = Pattern.compile(RegexDefinition.C_LIB_IMPORT);
	private final Pattern alias_tag = Pattern.compile(RegexDefinition.PC_ALIAS_TAG);
	private ArrayList<String> starting_code = null;
	private ArrayList<String> output_code = null;
	private ArrayList<String> used_libraries;
	private boolean uses_libraries = false, 
			uses_aliases = false, 
			has_alias_tags = false;
	private int string_count;
	private Mode mode;
	
	public Preprocessor() {
		PreprocessData.resetPlaceholderFunctions();
		PreprocessData.resetUsedStrings();
		PreprocessData.resetWarnings();
		used_libraries = new ArrayList<String>();
		starting_code = new ArrayList<String>();
		output_code = new ArrayList<String>();
		string_count = 0;
	}
	
	public void extractCode(String[] input_code) {
		ArrayList<Tuple<String, String>> libraries = new ArrayList<Tuple<String, String>>();
		ArrayList<String> alias_files = new ArrayList<String>();
		ArrayList<String> aliases = new ArrayList<String>();
		String mode_file = null;
		ModeLoader mode_parser;
		
		// Separate The Code From Library Imports
		for (int i = 0; i < input_code.length; i++) {
			Matcher alias_matcher = alias_import.matcher(input_code[i]);
			Matcher mode_matcher = mode_import.matcher(input_code[i]);
			Matcher lib_matcher = lib_import.matcher(input_code[i]);
			Matcher atag_matcher = alias_tag.matcher(input_code[i]);
			
			if (lib_matcher.find()) {
				libraries.add(buildPath(lib_matcher.group(1)));
				uses_libraries = true;
			
			} else if (mode_matcher.find()) {
				mode_file = buildPath(mode_matcher.group(1)).first;
			
			} else if (alias_matcher.find()) {
				alias_files.add(buildPath(alias_matcher.group(1)).first);
				uses_aliases = true;
				
			} else if (atag_matcher.find()) {
				aliases.add(atag_matcher.group().trim());
				has_alias_tags = true;
				
			} else {
				// Discard empty lines and comments
				if ((input_code[i].trim().length() > 0) && (!Services.isComment(input_code[i]))) {
					PreprocessData.addLineNumber(i + 1);
					starting_code.add(input_code[i]);
				}
			}
		}
		
		// Load the mode: Doesn't matter whether it's been imported or not
		mode_parser = new ModeLoader(mode_file);
		if (uses_libraries)
			mode_parser.addLibraries(libraries);
		
		if (uses_aliases)
			mode_parser.addAliasFiles(alias_files);
		
		if (has_alias_tags)
			mode_parser.addAliases(aliases);
		
		mode = mode_parser.mode();
	}
	
	public void compile(ArrayList<String> code, int depth) {
		mode.lib_parser.clearCode();
		
		// Translate Code
		for (String line : code) 
			next(line);
		
		// Get used code from libraries
		code = new ArrayList<String>();
		ArrayList<String> dependencies = new ArrayList<String>();
		for (Library lib : mode.lib_parser.usedLibraries()) {
			// Add Dependent Libraries
			for (String dep : lib.dependencies())
				dependencies.add(dep);
			
			ArrayList<String> used_code = lib.usedCode();
			if (used_code != null) {
				int count = PreprocessData.getLastLine();
				for (String line : used_code) {
					PreprocessData.addLineNumber(count + 1);
					code.add(line);
				}
				code.add("\n");
				PreprocessData.addLineNumber(count + 1);
			}
		}
		
		// Load Dependencies
		for (String dep : dependencies) {
			Tuple<String, String> t  = buildPath(dep);
			mode.lib_parser.addLibrary(t.first, t.second);
		}
		mode.lib_parser.loadLibraries();
		
		if (code.size() > 0)
			compile(code, ++depth);
	}
	
	public ArrayList<String> usedLibraries() {
		for (Library t : mode.lib_parser.usedLibraries())
			used_libraries.add(t.name.substring(0,1).toUpperCase() + t.name.substring(1));
		
		return used_libraries;
	}
	
	public ArrayList<String> startingCode() {
		return starting_code;
	}
	
	public ArrayList<String> outputCode() {
		return output_code;
	}
	
	private void next(String input) {
		// Discard Empty Line
		if ((input.length() == 0) || (Regex.WHITE_SPACE.matcher(input).matches())) {
			// output_code.add(input);
			return;
		}
		// Remove outer space
		input = input.trim();
		
		// For now discard comments
		if (!Services.isComment(input)) {
			// Replace Strings With Variables
			input = replaceStrings(input);
			
			// Parse Aliases
			if (mode.alias_collection.hasAliases())
				input = mode.alias_collection.iterativeSubstitute(input);
			
			// Parse Library Functions
			if (mode.lib_parser.usedLibraries().size() > 0)
				input = mode.lib_parser.substitute(input);
			
			// Replace Keywords
			input = CoreReplace.substitute(input);
			
			// Remove extra brackets []
			// TODO: Improve on this because it's a quick fix for now
			if (extra_brackets.matcher(input).find())
				input = extra_brackets.matcher(input).replaceAll("");
			
			output_code.add(input);
		}
	}
	
	private String replaceStrings(String input) {
		Matcher match = Regex.STRING.matcher(input);
		while (match.find()) {
			String rep_var = String.format(Variables.AUTO_STRING, string_count++);
			input = match.replaceFirst(rep_var);
			
			// Log the replaced variable
			PreprocessData.addUsedString(match.group(0));
			
			match = Regex.STRING.matcher(input);
		}
		
		return input;
	}
	
	private Tuple<String, String> buildPath(String line) {
		String[] arr = line.split("\\.");
		return new Tuple<String, String>(Services.join(arr, "/"), arr[arr.length - 1].toLowerCase());
	}
	
}