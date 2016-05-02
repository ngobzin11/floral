package com.pkoding.universal.data;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;

import com.pkoding.universal.util.RegexDefinition;
import com.pkoding.universal.warning.PCWarning;

public class PreprocessData {
	
	// System.getProperty("user.dir") + "/src/java/floral/"
	// "/usr/local/tomcat7/webapps/ROOT/WEB-INF/classes/floral/"
	public static String CMD = "";
	
	private final static Pattern sub_pattern = Pattern.compile(RegexDefinition.C_LANG_SUB_FMT);
	
	private static HashMap<String, String> library_definitions = new HashMap<String, String>();
	
	// Keeps track of the line numbers, skipping comments and blank lines:
	//		- Index		==> The line as seen in the pcode file
	//		- Value		==> The actual line number in the raw file
	private static ArrayList<Integer> line_numbers = new ArrayList<Integer>();
	
	private static ArrayList<String> used_strings = new ArrayList<String>();
	
	// Triple: core/library_name, function_name, arguments
	private static HashSet<String> placeholder_functions = new HashSet<String>();
	
	private static ArrayList<PCWarning> warnings = new ArrayList<PCWarning>();
	
	private static HashSet<String> library_functions = new HashSet<String>();
	
	private static final HashSet<String> keywords = readKeywords();
	
	private static final HashSet<String> builtin = readBuiltIn();
	
	private static String line_end_marker = "";
	
	private static String variable_prefix = "";
	
	private static String output_language;
	
	/**
	 * Adds a new line number in the line marker to be used for error reporting. 
	 * 
	 * @param raw		- The line as it is in the raw code file
	 */
	public static void addLineNumber(int raw) {
		line_numbers.add(raw);
	}
	
	/**
	 * Gets the line's number in the raw file given the line in the pcode file
	 * 
	 * @param index		- The line number in the pcode file
	 * @return Integer	- The line number in the raw file
	 */
	public static int getLineNumber(int index) {
		return (index < line_numbers.size()) ? line_numbers.get(index) : line_numbers.get(line_numbers.size() - 1);
	}
	
	public static int getLastLine() {
		return (line_numbers.size() > 0) ? line_numbers.get(line_numbers.size() - 1) : 0;
	}
	
	public static void addUsedString(String str) {
		used_strings.add(str);
	}
	
	public static String getUsedString(int index) {
		return (index < used_strings.size()) ? used_strings.get(index) : null;
	}
	
	public static void addPlaceholderFunction(String function) {
		placeholder_functions.add(function);
	}
	
	public static HashSet<String> placeholderFunctions() {
		return placeholder_functions;
	}
	
	public static void resetPlaceholderFunctions() {
		placeholder_functions = new HashSet<String>();
	}
	
	public static void resetUsedStrings() {
		used_strings = new ArrayList<String>();
	}
	
	public static void addWarning(PCWarning warning) {
		warnings.add(warning);
	}
	
	public static ArrayList<PCWarning> warnings() {
		return warnings;
	}
	
	public static void resetWarnings() {
		warnings = new ArrayList<PCWarning>();
	}
	
	public static void addLibraryFunction(String name) {
		library_functions.add(name);
	}
	
	public static boolean containsLibraryFunction(String name) {
		return library_functions.contains(name);
	}
	
	public static boolean isNative(String func_name) {
		return library_definitions.containsKey(func_name);
	}
	
	public static boolean isBuiltIn(String func_name) {
		return builtin.contains(func_name);
	}
	
	public static boolean isKeyword(String keyword) {
		return keywords.contains(keyword);
	}
	
	public static HashSet<String> libraryFunctions() {
		return library_functions;
	}
	
	public static void resetLibraryFunctions() {
		library_functions = new HashSet<String>();
	}
	
	public static HashMap<String, String> getLibraryDefinitions() {
		return library_definitions;
	}
	
	public static void setOutputLanguage(String language) {
		output_language = language;
		if (output_language.equals("javascript") || output_language.equals("console")) {
			line_end_marker = ";";
			variable_prefix = "";
		
		} else if (output_language.equals("php")) {
			line_end_marker = ";";
			variable_prefix = "$";
			
		} else {
			line_end_marker = "";
			variable_prefix = "";
		}
		readLibDefinitions();
	}
	
	public static String getOutputLanguage() {
		return output_language;
	}
	
	public static String variablePrefix() {
		return variable_prefix;
	}
	
	public static String lineEndMarker() {
		return line_end_marker;
	}
	
	private static void readLibDefinitions() {
		// TODO: Throw an exception here in case the lib files cannot be read
		File[] files = new File(CMD + "substitutions/" + output_language + "/library/").listFiles();
		BufferedReader reader = null;
		for (int i = 0; i < files.length; i++) {
			try {
				reader = new BufferedReader(new FileReader(files[i]));
				String line = null;
				while ((line = reader.readLine()) != null) {
					// Ignore empty lines: Line is not actually trimmed because some lines intentionally have spaces on the right
					if (!"".equals(line.trim())) {
						Matcher m = sub_pattern.matcher(line);
						if (m.find()) {
							library_definitions.put(m.group(1), m.group(2));
							
						// TODO: This could be an error, but it could also be a comment
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static HashSet<String> readKeywords() {
		HashSet<String> code = new HashSet<String>();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(CMD + "data/keywords"));
			String line;
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				if (!"".equals(line))
					code.add(line);
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return code;
	}
	
	private static HashSet<String> readBuiltIn() {
		HashSet<String> code = new HashSet<String>();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(CMD + "data/builtin"));
			String line;
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				if (!"".equals(line))
					code.add(line);
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();	
		}
		
		return code;
	}

}
