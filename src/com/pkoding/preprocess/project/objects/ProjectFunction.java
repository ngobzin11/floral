package com.pkoding.preprocess.project.objects;

public class ProjectFunction {
	
	public final String arguments;
	
	public final boolean visible;
	
	public final String name;
	
	public ProjectFunction(boolean visible, String name, String arguments) {
		this.arguments = arguments;
		this.visible = visible;
		this.name = name;
	}
	
	public String toString() {
		return "\n\t" + (visible ? "public " : "private ") + name + "(" + arguments + ")" + 
					"\n\t\t// Function Body Goes Here...\n" + 
				"\tend";
	}

}
