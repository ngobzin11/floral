package com.pkoding.preprocess.mode;

import java.io.*;
import java.util.regex.*;
import java.util.ArrayList;

import com.pkoding.universal.util.*;


/**
 * The ModeParser class loads the libraries and aliases for each mode. There can only be one mode
 * 	per document so if more than one is declared the last mode declared is going to be put into 
 * 	effect. Unlike the AliasParser and LibraryParser classes, the mode parser is essentially a 
 * 	specialized file reader that decides which class handles certain content.
 * 
 * @author ngobzin11
 *
 */
public class ModeLoader {
	
	private ArrayList<Tuple<String, String>> lib_names = new ArrayList<Tuple<String, String>>();
	private final Pattern alias_ptn = Pattern.compile(RegexDefinition.C_ALIAS_IMPORT);
	private final Pattern lib_ptn = Pattern.compile(RegexDefinition.C_LIB_IMPORT);
	private ArrayList<String> alias_files = new ArrayList<String>();
	private ArrayList<String> in_file = new ArrayList<String>();					// In file aliases
	private boolean files_added = false;
	private String root = "modes/";
	private Mode mode = null;
	
	public ModeLoader(String mode_file) {
		if ((mode_file != null) && (!"".equals(mode_file))) {
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new FileReader(root + mode_file));
				String line = null;
				while ((line = reader.readLine()) != null) {
					line = line.trim();
					// Skip blank lines
					if (!"".equals(line)) {
						Matcher alias_matcher = alias_ptn.matcher(line);
						Matcher lib_matcher = lib_ptn.matcher(line);
						// If it's a library
						if (lib_matcher.find()) {
							lib_names.add(buildPath(lib_matcher.group(1)));
							
						// If it's an alias
						} else if (alias_matcher.find()) {
							alias_files.add(buildPath(alias_matcher.group(1)).first);
							
						// Not recognized
						} else {
							// TODO: Show Incorrect Import Warning
							//		Consider the possibility that this might be a comment
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void addAliasFiles(ArrayList<String> aliases) {
		alias_files.addAll(aliases);
		files_added = true;
	}
	
	public void addAliasFile(String alias) {
		alias_files.add(alias);
		files_added = true;
	}
	
	public void addAliases(ArrayList<String> aliases) {
		in_file.addAll(aliases);
		files_added = true;
	}
	
	public void addAlias(String alias) {
		in_file.add(alias);
		files_added = true;
	}
	
	public void addLibraries(ArrayList<Tuple<String, String>> libs) {
		lib_names.addAll(libs);
		files_added = true;
	}
	
	public void addLibrary(Tuple<String, String> lib) {
		lib_names.add(lib);
		files_added = true;
	}
	
	public Mode mode() {
		if (mode == null || files_added)
			mode = new Mode("default", lib_names, alias_files, in_file);
		
		files_added = false;
		return mode;
	}
	
	private Tuple<String, String> buildPath(String line) {
		String[] arr = line.split("\\.");
		return new Tuple<String, String>(Services.join(arr, "/"), arr[arr.length - 1].toLowerCase());
	}

}
