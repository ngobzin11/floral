package com.pkoding.preprocess.project.objects;

public class ProjectVariable {
	
	public final String name;
	
	public final String type;
	
	/**
	 * http://stackoverflow.com/questions/1535631/static-variables-in-javascript
	 * http://php.net/manual/en/language.oop5.properties.php
	 * https://docs.python.org/2/tutorial/classes.html
	 * 
	 * @param name
	 * @param type
	 */
	public ProjectVariable(String type, String name) {
		this.name = name;
		this.type = type;
	}
	
	public String toString() {
		return "\t" + type + " " + name + "\n";
	}

}
