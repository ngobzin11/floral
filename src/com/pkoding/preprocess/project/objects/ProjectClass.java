package com.pkoding.preprocess.project.objects;

import java.util.ArrayList;

public class ProjectClass {
	
	private ArrayList<ProjectFunction> functions;
	private ArrayList<ProjectVariable> variables;
	private final String name;
	
	public ProjectClass(String name) {
		functions = new ArrayList<ProjectFunction>();
		variables = new ArrayList<ProjectVariable>();
		this.name = name;
	}
	
	public void addFunction(ProjectFunction function) {
		functions.add(function);
	}
	
	public void addVariable(ProjectVariable variable) {
		variables.add(variable);
	}
	
	public String toString() {
		String str = "\nclass " + name + "\n\n";
		for (ProjectVariable variable : variables) 
			str += variable.toString();
		
		for (ProjectFunction function : functions) 
			str += function.toString() + "\n";
		
		return str + "\nend\n";
	}

}
