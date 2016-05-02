package com.pkoding.preprocess.alias;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import com.pkoding.universal.data.PreprocessData;
import com.pkoding.universal.util.*;

/**
 * This program assumes a user has the alias functions defined in a separate file to 
 * 	allow for global aliases. The files are stored in the path 'modes/pkoding/alias/general' 
 * 	 and the specific name is defined by the user and is passed the same way libraries are.
 * 
 * The format of an alias statement is:
 * 
 * 				@alias alias	<==>	defined_statement
 * 
 * 	where alias is the alias statement to be replaced with defined_statement. The specific 
 * 	format of the captured statement is the same as that of libraries. To use an example of 
 * 	defining an alias for the 'plus' operation:
 * 
 * 				@alias add [number] to [number]		<==>	[1] plus [2]
 * 
 * 	Whenever the alias parser finds the key-phrase 'add to' in the given format, it will 
 * 	replace it with 'plus' with the captured arguments added in the given order. Counting 
 * 	in all file parsing will start at 1 instead of zero. 
 * 	An additional example that has the arguments returned in reverse order is given below:
 * 
 * 				@alias [number] divide [number]		<==>	[2] over [1]
 *  
 * 	In the above example, the second number is being divided by the first, which can be 
 * 	translated to the second number over the first. 
 * 
 * @author ngobzin11
 */
public class AliasLoader {
	
	private final Pattern alias_ptn = Pattern.compile(RegexDefinition.PC_ALIAS_TAG);
	private final Pattern arg_order_ptn = Pattern.compile("\\s*\\[\\s*([0-9]+)\\s*\\]\\s*");
	private final String root = PreprocessData.CMD + "aliases/";
	
	private ArrayList<String> file_paths = new ArrayList<String>();		// Folders with defined aliases
	private AliasCollection collection = new AliasCollection();			// All defined aliases go here
	
	// Loads every file in the general directory
	public void generate() {
		for (String path : file_paths) {
			File f = new File(root + path);
			if (f.exists()) {
				// Folder
				if (f.isDirectory()) {
					File[] files = f.listFiles();
					if ((files != null) && (files.length > 0)) 
						for (File file : files)
							// Don't load folders
							if (file.isFile())
								loadFile(file.getPath());
					
				// File
				} else {
					loadFile(f.getPath());
				}
			} else {
				// TODO: Add an error here
			}
		}
		
		if (collection.hasAliases())
			collection.generate();
	}
	
	public void addToCollection(ArrayList<String> aliases) {
		for (String alias : aliases)
			makeAndAdd(alias);
	}
	
	public AliasCollection getAliasCollection() {
		return collection;
	}
	
	public void addFile(String file) {
		file_paths.add(file);
	}
	
	// Load a specific file in the general folder
	public void loadFile(String fpath) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(fpath));
			String line = null;
			while ((line = reader.readLine()) != null)
				makeAndAdd(line.trim());
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void makeAndAdd(String line) {
		if (!"".equals(line)) {
			Matcher a_matcher = alias_ptn.matcher(line);
			if (a_matcher.find()) {
				// Generate The Alias' Regex Statements
				String alias = a_matcher.group(1).trim();
				Tuple<String, String> keyphrase = StatementToRegexParser.keyphrase(alias);
				
				// Add Alias To Collection
				collection.addAlias(
						// Keyword and key-phrase
						keyphrase.first, keyphrase.second,
						// The phrase's regex
						StatementToRegexParser.phraseRegex(alias), 
						new Alias(
								// The statement's regex
								StatementToRegexParser.statementRegex(alias, ""),
								// The formated replacement string
								formatReplacement(a_matcher.group(2).trim())
						)
				);
			// TODO: Show some error if an alias statement does not match the allowed definition 
			}
		}
	}
	
	private String formatReplacement(String replacement) {
		if ((replacement != null) && (!"".equals(replacement))) {
			String[] arr = replacement.split("\\s+");
			String result = "";
			for (int i = 0; i < arr.length; i++) {
				Matcher m = arg_order_ptn.matcher(arr[i]);
				// If it's a number replace it with %(number)$s, otherwise just keep it as it is
				if (m.find()) {
					int num = Integer.parseInt(m.group(1)) + 1;
					result += (i == 0) ? "%" + num + "$s" : " %" + num + "$s";
				} else
					result += (i == 0) ? arr[i] : " " + arr[i];
			}
			return result;
		}
		// TODO: Throw an error/warning if the output is not given
		return null;
	}

}
