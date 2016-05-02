package com.pkoding.preprocess.library;

import java.util.*;
import java.util.regex.Pattern;

import com.pkoding.universal.data.PreprocessData;
import com.pkoding.universal.util.*;

public class LibraryFunction {
	
	private ArrayList<String> code = new ArrayList<String>();
	
	public final String[] dependencies;
	
	public final HashSet<String> tags;
	
	public final Pattern regex;
	
	public final String name;
	
	public LibraryFunction(String name, Pattern regex, HashSet<String> tags, String[] dependencies) {
		this.dependencies = dependencies;
		this.regex = regex;
		this.tags = tags;
		this.name = name;
	}
	
	public void addCode(String line) {
		code.add(line);
	}
	
	// Here we make the assumption that all functions are not native
	public Tuple<String, LibraryFunction> call(String library, String[] args) {
		String funct_name = "__" + name.toUpperCase() + "__";
		String[] used_args = usedArgs(args);
		String funct_call = ((args[0] == null) ? "" : Services.strip(args[0]) + " ") + 
			funct_name + "(" + Services.join(used_args, ", ") + ")" + 
			((args[args.length - 1] == null) ? "" : " " + args[args.length - 1]);
		
		PreprocessData.addPlaceholderFunction(funct_name);
		
		return new Tuple<String, LibraryFunction>(funct_call, this);
	}
	
	public ArrayList<String> code() {
		return code;
	}
	
	public boolean hasDependencies() {
		return dependencies != null;
	}
	
	private String[] usedArgs(String[] args) {
		String[] res = new String[args.length - 2];
		for (int i = 0; i < res.length; i++)
			res[i] = args[i + 1];
		
		return res;
	}
	
	public String toString() {
		String str = "name:\t\t\t" + name + "\n";
		str += "dependencies:\t" + Services.join(dependencies, ", ") + "\n";
		str += "tags:\t\t\t" + Services.join(tags, ", ") + "\n";
		
		// Code
		for (String line : code)
			str += line + "\n";
		
		return str;
	}
	
}
