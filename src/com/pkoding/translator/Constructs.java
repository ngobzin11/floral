package com.pkoding.translator;

import java.util.*;
import java.util.regex.*;

import com.pkoding.universal.data.PreprocessData;
import com.pkoding.universal.util.*;


public class Constructs {
	
	private static final Pattern number = Pattern.compile("^(" + RegexDefinition.NUMBER + ")$");
	private static final Pattern tac_ptn = Pattern.compile(RegexDefinition.C_CTRL_TAC);
	private static final String[][] object_data = {
		// Python
		{"python", "array,", "boolean,bool(%s)", "int,int(%s)", "integer,int(%s)", "float,float(%s)",
			"string,str(%s)", "functioncall,", "all_cast,(boolean|int|string|float)"},
		// JavaScript
		{"javascript", "array,", "boolean,Boolean(%s)", "int,parseInt(%s)", "integer,parseInt(%s)",
			"float,parseFloat(%s)", "string,String(%s)", "functioncall,", 
			"all_cast,(boolean|int|string|float)"},
		// PHP
		{"php", "array,(array) %s", "boolean,(bool) %s", "int,(int) %s", "integer,(int) %s", "float,(float) %s", 
			"string,(string) %s", "functioncall,", "all_cast,(array|boolean|int|string|float)"}
	};
	
	private static final Map<String, String> objects = objects();
	
	// Multi-Dimensional Array:
	//		This is language independent so far, because all three supported languages have the 
	//		same array access structure
	public static String mdArray(String[] args) {		
		String out = "";
		for (int i = 0; i < args.length - 1; i++)
			out += "[" + args[i] + "]";
		
		// The last argument is always the array
		return args[args.length - 1] + out;
	}
	
	// Casting
	public static String cast(String str, String type) {
		String cst = objects.get(PreprocessData.getOutputLanguage() + "_" + type);
		if (cst != null)
			cst = String.format(cst, str);
		
		// TODO: Throw Error If cst is null: Refer to previous installation
		return cst;
	}
	
	// [variable, start, stop, step]	|	up / down
	public static String loop(String variable, String start, String stop, String step, boolean up) {
		if (PreprocessData.getOutputLanguage().equals("python")) {
			return "for " + variable + " in range(" + start + ", " + stop + ", " + step + "):";
			
		} else {
			String prefix = PreprocessData.getOutputLanguage().equals("javascript") ? "var " : "";
			return "for (" + prefix + variable + " = " + start + "; " + variable + (up ? " < " : " > ") + stop + 
				"; " + variable + " += " + step + ") {";
		}
	}
	
	// For loop, converting [<= to <], [>= to >]
	public static String pythonFor(Matcher m, String code1) {
		ArrayList<String> lst = new ArrayList<String>();
		for (int i = 1; i <= m.groupCount(); i++) 
			if ((m.group(i) != null) && (m.group(i).trim().length() > 0)) 
				lst.add(m.group(i).trim());
		
		String op = lst.get(3);
		String stop = lst.get(4);
		Matcher num = number.matcher(stop);
		
		// Less than or equal to: Add 1 to the stopping position
		if (op.equals("<="))
			// If the stopping position is a number, change it, otherwise just add one to it
			stop = (num.find()) ? "" + (Integer.parseInt(stop) + 1) : stop + " + 1";
			
		// Greater than or equal to: Subtract 1 from the stopping position
		else if (op.equals(">="))
			// If the stopping position is a number, change it, otherwise just add one to it
			stop = (num.find()) ? "" + (Integer.parseInt(stop) - 1) : stop + " - 1";
		
		// Step
		Matcher sm = tac_ptn.matcher(lst.get(5));
		String step = "";
		if (sm.find()) {
			// If the operator is a plus
			if (m.group(1).equals("-"))
				step = "-";
			
			// TODO: Error here - Unsupported Step Operation 
			else if (!m.group(1).equals("-"))
				step = "";
			
			step += sm.group(2);
			
		// TODO: Error here - Unrecognized Step Statement
		}
		
		return "for " + lst.get(0) + " in range(" + lst.get(1) + ", " + stop + ", " + step + "):";
	}
	
	private static Map<String, String> objects() {
		Map<String, String> map = new HashMap<String, String>();
		for (String[] lang : object_data)
			for (int i = 1; i < lang.length; i++) {
				String[] arr = lang[i].split(",");
				map.put(lang[0] + "_" + arr[0], (arr.length == 1 ? null : arr[1]));
			}
		
		return map;
	}

}
