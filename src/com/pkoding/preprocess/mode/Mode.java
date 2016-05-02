package com.pkoding.preprocess.mode;

import java.util.*;

import com.pkoding.preprocess.alias.*;
import com.pkoding.preprocess.library.*;
import com.pkoding.universal.util.Tuple;

/**
 * A Mode is a collection of aliases that bend the pseudocode for a specific  use case. 
 * 	For example, in math mode a programmer can define a group of math aliases and put 
 * 	them in the same mode. In the future the user will be able to just include the math 
 * 	mode and all math related libraries like random and math would be included. This would 
 * 	also include the math aliases that the user has defined. 
 * 
 * 	For now a mode is just going to be a bunch of aliases with no formal support for modes.  
 * 
 * @author ngobzin11
 *
 */
public class Mode {
	
	public AliasCollection alias_collection;
	
	public final ArrayList<Tuple<String, String>> lib_names;
	
	// Both files and folders
	public final ArrayList<String> alias_files;
	
	public final LibraryLoader lib_parser;
	
	public final String name;
	
	public Mode(String name, ArrayList<Tuple<String, String>> lib_names, ArrayList<String> alias_files, ArrayList<String> in_file) {
		this.alias_files = alias_files;
		this.lib_names = lib_names;
		this.name = name;
		
		// Add aliases and load them
		AliasLoader ap = new AliasLoader();
		for (String afile : alias_files)
			ap.addFile(afile);
		
		ap.addToCollection(in_file);
		ap.generate();
		alias_collection = ap.getAliasCollection();
		
		// Add libraries and load them
		lib_parser = new LibraryLoader();
		if (lib_names.size() > 0) {
			for (Tuple<String, String> lib : lib_names)
				lib_parser.addLibrary(lib.first, lib.second);
			
			// Load library dependencies
			lib_parser.loadLibraries();
		}
	}
	
	public void setAliasCollection(AliasCollection alias_collection) {
		this.alias_collection = alias_collection;
	}

}
