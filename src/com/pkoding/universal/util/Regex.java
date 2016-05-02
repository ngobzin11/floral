package com.pkoding.universal.util;

import java.util.regex.*;

public class Regex {
	
	public static final Pattern OPERATIONS = Pattern.compile(RegexDefinition.OPERATIONS);
	
	public static final Pattern VARIABLE = Pattern.compile(RegexDefinition.VARIABLE);
	
	public static final Pattern NUMBER = Pattern.compile("^" + RegexDefinition.NUMBER + "$");
	
	public static final Pattern STRING = Pattern.compile(RegexDefinition.STRING);
	
	public static final Pattern WHITE_SPACE = Pattern.compile(RegexDefinition.WHITE_SPACE);
	
	public static final Pattern OUTER_WHITE_SPACE = Pattern.compile(RegexDefinition.OUTER_WHITE_SPACE);
	
	public static final Pattern SPECIAL_CHARS = Pattern.compile(RegexDefinition.SPECIAL_CHARS);
	
	public static final Pattern LINE_COMMENT = Pattern.compile(RegexDefinition.C_STRICT_LINE_COMMENT);
	
	public static final Pattern AUTO_STRING_REG = Pattern.compile("__AUTO_STRING_[0-9]+__");

}
