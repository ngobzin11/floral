package com.pkoding.preprocess.project;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import com.pkoding.preprocess.project.objects.*;
import com.pkoding.recognizer.error.SyntaxError;
import com.pkoding.universal.data.PreprocessData;
import com.pkoding.universal.util.RegexDefinition;


/**
 * Project Structure:
 * 		(++|class) ClassName
 * 			(+|public) methodName(arguments)
 * 			(-|private) methodName(arguments)
 * 
 *		(global|static|public|private) variable_name
 * 
 * @author ngobzin11
 */
public class Structure {
	
	private final Pattern proj_class = Pattern.compile(RegexDefinition.C_PROJ_CLASS);
	private final Pattern proj_func = Pattern.compile(RegexDefinition.C_PROJ_FUNC);
	private final Pattern proj_var = Pattern.compile(RegexDefinition.C_PROJ_VAR);
	private final String root = PreprocessData.CMD + "code/";
	private ArrayList<ProjectClass> classes;
	private String[] code;
	
	public Structure(String filename) {
		classes = new ArrayList<ProjectClass>();
		read(filename);
	}
	
	public Structure(String[] code) {
		classes = new ArrayList<ProjectClass>();
		this.code = code;
	}
	
	public ArrayList<ProjectClass> classes() {
		return classes;
	}
	
	public void build() throws SyntaxError {
		ProjectClass pclass = null;
		for (int i = 0; i < code.length; i++) {
			if (code[i].trim().length() > 0) {
				Matcher cls = proj_class.matcher(code[i]);
				Matcher func = proj_func.matcher(code[i]);
				Matcher var = proj_var.matcher(code[i]);
				if (cls.find()) {
					if (pclass != null)
						classes.add(pclass);
					
					pclass = new ProjectClass(cls.group(2));
				} else if (func.find()) {
					if (pclass != null) {
						pclass.addFunction(new ProjectFunction(
							(func.group(1).equals("+") || func.group(1).equals("public")) ? true : false, 
							func.group(2), 
							func.group(3)
						));
					} else {
						throw new SyntaxError("Function declared outside class in line %s", i + 1, false);
					}
				} else if (var.find()) {
					if (pclass != null)
						pclass.addVariable(new ProjectVariable(var.group(1), var.group(2)));
					else {
						throw new SyntaxError(var.group(1).substring(0, 1) + var.group(1).substring(1) + 
								" variable declared outside class in line %s", i + 1, false);
					}
				} else {
					throw new SyntaxError("Unrecognized statement in line %s", i + 1, false);
				}
			}
		}
		
		if (pclass != null)
			classes.add(pclass);
	}
	
	private void read(String filename) {
		ArrayList<String> lines = new ArrayList<String>();
		BufferedReader br = null;
		 
		try {
			String line;
			br = new BufferedReader(new FileReader(root + "project/" + filename));
 
			while ((line = br.readLine()) != null)
				lines.add(line);
 
		} catch (IOException e) {
			e.printStackTrace();
			
		} finally {
			try {
				if (br != null)
					br.close();
				
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		
		int i = 0;
		this.code = new String[lines.size()];
		for (String line : lines)
			this.code[i++] = line;
	}

}
