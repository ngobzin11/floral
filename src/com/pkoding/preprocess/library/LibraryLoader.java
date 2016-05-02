package com.pkoding.preprocess.library;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.*;
import com.pkoding.universal.util.*;
import com.pkoding.universal.data.PreprocessData;
import com.pkoding.universal.error.*;

public class LibraryLoader {
	
	/*
	 * Compile All Patterns That Will Be Used
	 */
	private final Pattern comment = Pattern.compile(RegexDefinition.PC_JAVA_DOC);
	private final Pattern ctrl_beg = Pattern.compile(RegexDefinition.CONTROL_BEGIN);
	private final Pattern ctrl_end = Pattern.compile(RegexDefinition.CONTROL_END);
	private final Pattern beg_funct = Pattern.compile(RegexDefinition.BEG_FUNCT);
	private final Pattern native_funct = Pattern.compile(RegexDefinition.PC_NATIVE);
	private final Pattern func_def = Pattern.compile(RegexDefinition.C_FUNC_DEF);
	
	private final Pattern regex_tag = Pattern.compile(RegexDefinition.PC_REGEX_TAG);
	private final Pattern tags_tag = Pattern.compile(RegexDefinition.PC_TAGS_TAG);
	private final Pattern depend_tag = Pattern.compile(RegexDefinition.PC_DEPENDENCY_TAG);
	private final Pattern param_tag = Pattern.compile(RegexDefinition.PC_PARAM_TAG);
	
	// Library Entry ==> prefix : library_name
	private HashMap<String, String> libraries = new HashMap<String, String>();
	private HashMap<String, Library> lib_map = new HashMap<String, Library>();
	private String root = PreprocessData.CMD + "libraries/";
	// Use this <root> when generating jar file
	// private final String root = System.getProperty("user.dir") + "/pkoding/libraries/pkoding/";
	private Pattern lib_pattern;
	
	/* Library information */
	private String func_name = null, phrase_regex = null, pcode_pattern = null;
	private ArrayList<String> params = new ArrayList<String>();
	private Tuple<String, String> keyphrase = null;
	private Pattern statement_regex = null;
	private LibraryFunction lib_fun = null;
	private String[] dependencies = null;
	private HashSet<String> tags = null;
	private int braces = 0;
	
	public void loadLibraries() {
		for (Entry<String, String> entry : libraries.entrySet())
			if (!lib_map.containsKey(entry.getKey()))
				load(entry.getValue(), entry.getKey());
		
		lib_pattern = Pattern.compile(RegexDefinition.NC_BEGIN + 
			"(" + Services.join(libraries.keySet(), "|") + ")" + 
			RegexDefinition.NC_END);
	}
	
	public void addLibrary(String path, String prefix) {
		if (!libraries.containsKey(prefix))
			libraries.put(prefix, path);
	}
	
	public void clearCode() {
		for (Entry<String, Library> entry : copy(lib_map.entrySet())) {
			Library library = entry.getValue();
			library.clearCode();
			lib_map.put(entry.getKey(), library);
		}
	}
	
	public String substitute(String input) {
		String untouched = input, output = null, key = capture(input), prev = "";
		while ((key != null) && (!prev.equals(input))) {
			prev = input;
			// Get Library
			Library lib = lib_map.get(key);
			
			// Call Library Substitute function
			String temp = lib.iterativeSubstitute(input);
			if (temp != null)
				output = temp;
			
			input = temp;
			key = capture(input);
		}
		
		return (output == null) ? untouched : output;
	}
	
	public Collection<Library> usedLibraries() {
		return lib_map.values();
	}
	
	private String capture(String input) {
		Matcher matcher = lib_pattern.matcher(input);
		String pref_match = null; 
		if (matcher.find())
			for (int i = 1; i <= matcher.groupCount(); i++)
				if (matcher.group(i) != null)
					pref_match = matcher.group(i);
		
		return pref_match;
	}
	
	public void load(String library, String prefix) {
		BufferedReader reader = null;
		Library lib = new Library(prefix, root + library);
		try {
			reader = new BufferedReader(new FileReader(root + library));
			String line = null;
			
			while ((line = reader.readLine()) != null) {
				// Java Doc Matcher
				Matcher jd_matcher = comment.matcher(line);
				
				// Inside/Starting A Comment
				if (jd_matcher.matches() && (jd_matcher.group(1) != null)) {
					// Contains: @regex/@tags/@dependency/@param
					Matcher dep_matcher = depend_tag.matcher(jd_matcher.group(1));
					Matcher prm_matcher = param_tag.matcher(jd_matcher.group(1));
					Matcher rgx_matcher = regex_tag.matcher(jd_matcher.group(1));
					Matcher tag_matcher = tags_tag.matcher(jd_matcher.group(1));
					
					// Extract Regex Information
					if (rgx_matcher.find()) {
						pcode_pattern = rgx_matcher.group(1);
						keyphrase = StatementToRegexParser.keyphrase(pcode_pattern);
						phrase_regex = StatementToRegexParser.phraseRegex(pcode_pattern);
						statement_regex = StatementToRegexParser.statementRegex(pcode_pattern, prefix);
					}
					
					// Extract Tags Information
					else if (tag_matcher.find())
						tags = extractTags(tag_matcher);
					
					// Extract Dependency Information
					else if (dep_matcher.find())
						dependencies = extractDependencies(dep_matcher);
					
					// Extract Parameter Information --> Used for error handling
					else if (prm_matcher.find())
						params.add(extractParam(prm_matcher));
					
				}
				// Inside Function
				else if (braces > 0) {
					// Opening Brace Matcher
					Matcher ctrl_beg_matcher = ctrl_beg.matcher(line);
					
					// Closing Brace Matcher
					Matcher ctrl_end_matcher = ctrl_end.matcher(line);
					
					// Add line of code 
					lib_fun.addCode(Services.strip(line));
					
					// Add opening brace to stack
					if (ctrl_beg_matcher.find()) {
						braces++;
					}
					// Remove opening brace from stack
					// TODO: Error Handling --> More closing braces than opening braces
					//						--> Function defined without extra information (Exception)
					else if (ctrl_end_matcher.find()) {
						if (--braces == 0) {
							// Add function to library
							lib.addFunction(keyphrase.first, keyphrase.second, phrase_regex, lib_fun);
							PCSuggestionRouter.addWarning(prefix + " " + keyphrase.first, prefix + " " + 
									keyphrase.second, pcode_pattern, Services.join(params, "|"));
							reset();
						}
					}
				}
				// Beginning Of Function
				else if (beg_funct.matcher(line).find()) {
					// Native Function
					Matcher ntv_matcher = native_funct.matcher(line);
					
					// Defined Function Matcher
					Matcher fn_matcher = func_def.matcher(line);
					
					// If native then generate library-function
					if (ntv_matcher.find()) {
						// TODO: Error when <native> is followed by code/implementation
						lib_fun = new LibraryFunction(ntv_matcher.group(1), statement_regex, tags, 
								dependencies);
						
						// Add function to library
						lib.addFunction(keyphrase.first, keyphrase.second, phrase_regex, lib_fun);
						PCSuggestionRouter.addWarning(prefix + " " + keyphrase.first, prefix + " " + 
								keyphrase.second, pcode_pattern, Services.join(params, "|"));
						
						reset();
					}
					// If not native then get function name and add code to library-function
					else if (fn_matcher.find()) {
						func_name = fn_matcher.group(1);
						lib_fun = new LibraryFunction(func_name, statement_regex, tags, dependencies);
						
						// Add function components
						lib_fun.addCode("function __" + func_name.toUpperCase() + "__(" + ((fn_matcher.group(2) == null) ? "" : 
							fn_matcher.group(2)) + ")");
						braces++;
					}
				}
			}
			
			lib.generate();
			lib_map.put(prefix, lib);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void reset() {
		params = new ArrayList<String>();
		statement_regex = null; 
		keyphrase = null;
		dependencies = null;
		tags = null;
		func_name = null;
		phrase_regex = null;
		braces = 0;
	}
	
	private String extractParam(Matcher prm_matcher) {
		return prm_matcher.group(1);
	}
	
	private String[] extractDependencies(Matcher dep_matcher) {
		String[] depend = new String[dep_matcher.groupCount()];
		for (int i = 1; i <= dep_matcher.groupCount(); i++)
			if (dep_matcher.group(i) != null)
				depend[i - 1] = dep_matcher.group(i);
		
		return depend;
	}
	
	private ArrayList<Entry<String, Library>> copy(Set<Entry<String, Library>> src) {
		ArrayList<Entry<String, Library>> dest = new ArrayList<Entry<String, Library>>();
		for (Entry<String, Library> entry : src)
			dest.add(entry);
		
		return dest;
	}
	
	private HashSet<String> extractTags(Matcher tag_matcher) {
		HashSet<String> tags = new HashSet<String>();
		for (String tag : tag_matcher.group(1).split(","))
			tags.add(tag);
		
		return tags;
	}
	
}
