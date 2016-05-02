package com.pkoding.universal.util;

public class RegexDefinition {
	
	/************************************************************************************
	 * 									NAMED REGEX PATTERNS							*
	 * 	Key:																			*
	 * 		- NC = Non Capturing														*
	 * 		- PC = Partial Capture														*
	 * 		- C = Capturing																*
	 * 		- ALO = At Least One														*
	 ************************************************************************************/
	
	// Plain Empty Space
	public static final String WHITE_SPACE = "\\s+|\\t+";
	
	// Plain Outer White Space
	public static final String OUTER_WHITE_SPACE = "^\\s+|\\s+$";
	
	// Plain Special Characters
	public static final String SPECIAL_CHARS = "[\".!?',*;:\\^%+=()\\[\\]~`\\{\\}></%-]";
	
	// (Non Capturing) Space/Not Variable or Number
	public static final String NC_VAL_SURROUND = "(?:\\s+|[^a-zA-Z0-9_])";
	
	// (Non Capturing) Space/Not Variable or Number/Begin
	public static final String NC_BEGIN = "(?:\\s+|[^a-zA-Z0-9_(\\.]|^)";

	// (Non Capturing) Control Structure (if|for|while) Begin
	public static final String NC_CS_BEGIN = "(?:[(]|\\s+|[^a-zA-Z0-9_])";

	// (Non Capturing) Control Structure (if|for|while) End
	public static final String NC_CS_END = "(?:[)]|\\s+|[^a-zA-Z0-9_])";
	
	// (Non Capturing) Space/Not Variable or Number/End
	public static final String NC_END = "(?:\\s+|[^a-zA-Z0-9_(\\.]|$)";
	
	// (Non Capturing) Anything
	public static final String NC_ANYTHING = "(?:.*)";
	
	// (Capturing) Anything
	public static final String C_ANYTHING = "(.*)";
	
	// Anything
	public static final String ANYTHING = ".*";
	
	// (Capturing) Anything Ending In Non-Alphanumeric Character
	public static final String C_ANYTHING_NONALNUM_END = "(.*[^a-zA-Z0-9_]|.*\\s+|^)";
	
	// (Capturing) Anything Beginning With Non-Alphanumeric Character
	public static final String C_ANYTHING_NONALNUM_BEGIN = "([^a-zA-Z0-9_].*|\\s+.*|$)";
	
	// (Capturing) At Least One Of Anything
	public static final String C_ALO_ANYTHING = "(.+)";
	
	// At Least One Of Anything
	public static final String ALO_ANYTHING = ".+";
	
	// (Capturing) At Least One Thing Ending In Non-Alphanumeric Character
	public static final String C_ALO_ANYTHING_NONALNUM_END = "(.+[^a-zA-Z0-9_]|.+\\s+|.+$)";
		
	// (Capturing) At Least One Thing Beginning With Non-Alphanumeric Character
	public static final String C_ALO_ANYTHING_NONALNUM_BEGIN = "([^a-zA-Z0-9_].+|\\s+.+|$)";
	
	// (Non Capturing) At Least One Of Anything
	public static final String NC_ALO_ANYTHING = "(?:.+)";
	
	// Plain Variable
	public static final String VARIABLE = "[\\$]?[a-zA-Z_][a-zA-Z0-9_]*";
	
	// Plain String
	public static final String STRING = "\"[^\"\r\n]*\"|\'[^\'\r\n]*\'";
	
	// Plain Operations
	public static final String OPERATIONS = "[%!=><*/-]=|[*]{1,2}|[-]{1,2}|[+]{1,2}|\\+=|[/%&^<>~|,=]";

	// Plain Ordinal
	public static final String ORDINAL = "th|nd|rd|st";

	// (Non Capturing) Ordinal
	public static final String NC_ORDINAL = "(?:th|nd|rd|st)";

	// Pain Integer
	public static final String INT = "[0-9]+";
	
	// Plain Floating Point Number
	public static final String FLOAT = "[-+]?[0-9]*[.][0-9]+";
	
	// Plain Hexadecimal
	public static final String HEX = "0[xX][a-fA-F0-9]+";
	
	// TODO: Plain Octal (Include Python) 
	public static final String OCTAL = "";
	
	// Plain Decimal
	public static final String DECIMAL = FLOAT + "|" + INT;
	
	// Plain Number
	public static final String NUMBER = DECIMAL + "|" + HEX; // + "|" + OCTAL;
	
	// Strict Number
	public static final String STRICT_NUMBER = "^" + FLOAT + "$|^" + INT + "$|^" + HEX + "$"; // + "|" + OCTAL;
	
	// Plain Alphabetic
	public static final String ALPHA = "[a-zA-Z]+";
	
	// Plain AlphaNumeric
	public static final String ALNUM = "[a-zA-Z0-9]+";
	
	// Character used to form a variable
	public static final String VARIABLE_CHAR = "[a-zA-Z0-9_]";
	
	// Plain Function
	public static final String FUNCTION = "[a-zA-Z_][a-zA-Z0-9_.]*\\s*[(].*[)]";
	
	// Multi-Capture Function
	public static final String MC_FUNCTION = "([a-zA-Z_][a-zA-Z0-9_.]*)\\s*[(]" + C_ANYTHING + "[)]";
	
	// At Least One Thing In Brackets (Not Capturing The Brackets Themselves)
	// TODO: Nested Brackets Exception
	public static final String ALO_BRACKETS = "(?:\\[)[^\\]]+(?:\\])|(?:[(])[^)](?:[)])";
	
	// Bracket followed by space
	public static final String C_SP_BRACKETS = "([\\[]\\s+|\\s+[\\]])";
	
	// (Capturing) All Types
	public static final String C_ALL_TYPES = "(string|int|float|double|long|char|short|boolean|array)";
	
	// (Non Capturing) All DataTypes
	public static final String NC_ALL_TYPES = "(?:string|int|float|double|long|char|short|boolean|array)";
	
	// Comparison Operations
	public static final String COMPARISON_OP = "[!=]==|[><!=]=|[><]";
	
	// */+-
	public static final String NORMAL_OP = "[-+*/]";
	
	// Three Adress Code: Variable Capture
	public static final String TAC_STEP_CAPTURE = VARIABLE + "\\s*=\\s*" + VARIABLE + "\\s*" + NORMAL_OP + 
		"\\s*(" + NUMBER + "|" + VARIABLE + ")|" + VARIABLE + "\\s*=\\s*(" + FUNCTION + ")";
	
	// Starting an ignore block
	public static final String C_IGNORE_START = "\\s*[@]ignore_(javascript|python|php)_start\\s*";
	
	// Ending an ignore block
	public static final String C_IGNORE_END = "\\s*[@]ignore_(javascript|python|php)_end\\s*";
	
	// Array Contents
	public static final String C_ARRAY_CONTENTS = "\\[\\s*" + C_ANYTHING + "\\s*\\]";
	
	// Plain Control End <end>
	public static final String CONTROL_END = NC_BEGIN + "end" + NC_END;
	
	// Plain Control Begin <then|do>
	public static final String CONTROL_BEGIN = NC_BEGIN + "then" + NC_END + "|" + NC_BEGIN + "do" + NC_END;
	
	/********************************************************************************************
	 * 									COMMENTS AND TAGS										*
	 ********************************************************************************************/
	// Language Specific Library Name
	public static final String C_LIB_NAME = "^\\s*[@]libname\\s+(php|python|javascript)\\s+" +
		C_ALO_ANYTHING + "\\s*$";
	
	// Language Specific Library Import: @import language statement
	public static final String C_IMPORT_STATEMENT = "^\\s*[@]import\\s+(php|python|javascript)\\s+" +
		C_ALO_ANYTHING + "\\s*$";
	
	// Library Import
	public static final String C_LIB_IMPORT = "^\\s*[#][Ll]ibraries\\.([a-zA-Z0-9_\\.]+)";
	
	// Inside Comment
	public static final String PC_JAVA_DOC = "^\\s*(?:[/][*]{2}|[*])([^/].*|$)";
	
	// @regex Tag
	public static final String PC_REGEX_TAG = "^\\s*[@]regex\\s+(.+)";
	
	// @tags Tag: @tags "tag one", "tag two"
	public static final String PC_TAGS_TAG = "^\\s*[@]tags\\s+" + C_ALO_ANYTHING + "\\s*";
	
	// @native Tag: @native language function_name call_type {arg-order}
	public static final String PC_NATIVE_TAG = "^\\s*[@]native\\s+(" + ALPHA + ")\\s+(" + VARIABLE + 
		")\\s+(" + INT + ")\\s+\\{" + C_ANYTHING + "\\}\\s*";
	
	// @dependency tag: @dependency library.function
	public static final String PC_DEPENDENCY_TAG = "^\\s*[@]dependencies\\s+([^,]+)\\s*((?:,\\s*)[^,]+)*\\s*";
	
	// @param tag: @param [number] <type> The number you are ...
	public static final String PC_PARAM_TAG = "^\\s*[@]param\\s+(\\[" + ALO_ANYTHING + "\\]\\s*[<]" +
		"\\s*" + ALPHA + "[>]\\s*[:-]?\\s+[^\\s].+)";
	
	// @argorder tag: @argorder {1, 2, ...}
	public static final String PC_ARG_TAG = "^\\s*[@]argorder\\s+\\{" + C_ANYTHING + "\\}";
	
	// @alias tag: @alias input <==> output 
	public static final String PC_ALIAS_TAG = "^\\s*[@]alias\\s+(" + ALO_ANYTHING + ")\\s+<==>\\s+(" + ALO_ANYTHING + ")\\s*";
	
	// Mode import
	public static final String C_MODE_IMPORT = "^\\s*[#][Mm]odes\\.([a-zA-Z0-9_\\.]+)";
	
	// Alias file import
	public static final String C_ALIAS_IMPORT = "^\\s*[#][Aa]liases\\.([a-zA-Z0-9_\\.]+)";
	
	/********************************************************************************************
	 * 										REGEX PARSER										*
	 ********************************************************************************************/
	// Plain Keyword:		"(?:^|\\s+|[?>])([a-zA-Z0-9_\\-+*]+)(?:\\s+|[<]|$)"
	public static final String C_RP_KEYWORD = "(^|\\s+|[?>\\]])([^\\s\\(\\)\\[\\]\\>\\<]+)(\\s+|[\\[<]|$)";
	
	// (Capturing) Non-Keyword
	public static final String RP_NON_KEYWORD = "(\\[[^\\]]+\\]|[<].+[>])";
	
	// (Partial Capturing) HashTag
	public static final String RP_HASHTAG = "#(\\s+#)*";
	
	// (Partial Capturing) Argument
	public static final String RP_ARGUMENT = "\\[([^\\]]+)\\]";
	
	// (Partial Capturing) Argument
	public static final String RP_OPTIONAL = "[<]([^>]+)[>]";
	
	// Plain Opening Brace
	public static final String OP_BRACE = ANYTHING + "\\{" + ANYTHING;
	
	// Plain Closing Brace
	public static final String CL_BRACE = ANYTHING + "\\}" + ANYTHING;
	
	// Strict One Line Comment
	public static final String STRICT_LINE_COMMENT = "^\\s*[/]{2}" + ANYTHING + "|^\\s*/\\*" + ANYTHING + "\\*/\\s*$";
	
	// (Capturing) One Line Comment
	public static final String C_STRICT_LINE_COMMENT = "^\\s*[/]{2}" + C_ANYTHING + "|^\\s*/\\*" + C_ANYTHING + "\\*/\\s*$";
	
	// Comment Beginning
	public static final String MULTILINE_COMMENT = "";
	
	// (Partial Capture) One Line Comment
	public static final String PC_LINE_COMMENT = C_ANYTHING + "[/]{2}" + ANYTHING;
	
	// (Partial Capture) Native
	public static final String PC_NATIVE = "^\\s*native\\s+function\\s+(" + VARIABLE + ")\\s*\\(" + C_ANYTHING + "\\)\\s*";
	
	// Plain Begin Function
	public static final String BEG_FUNCT = "^\\s*(?:native\\s+)?function\\s+" + ALO_ANYTHING;
	
	// Function Definition Capture
	public static final String C_FUNC_DEF = "\\s*function\\s+(" + VARIABLE + ")\\s*\\(" + C_ANYTHING + "\\)\\s*";
	
	/********************************************************************************************
	 * 										LANGUAGE PARSER										*
	 ********************************************************************************************/
	
	// Pseudocode Function Name Format
	public static final String C_PK_FUNC_FMT = "__([A-Z_]+)__";
	
	// Normal Function Name Format
	public static final String C_NM_FUNC_FMT = "[a-zA-Z][a-zA-Z_]+";
	
	// Outermost Defined Function, spans the entire line
	public static final String C_OUTER_FUNC = "^(__[A-Z][A-Z0-9_]*__)[(]" + C_ANYTHING + "[)]$";
	
	// Outermost Translated Language Function, spans the entire line: It has to have some untranslated components to match
	public static final String C_TRANS_FUNC = "^([a-zA-Z_][a-zA-Z0-9_]*)[(](.*__[A-Z][A-Z0-9_]*__.*)[)]$";
	
	// Control Flow Statement In: Needs Indent After
	public static final String C_CTRL_FLOW_IN = "^\\s*__(FOR|ELSE|ELSE_IF|WHILE|SWITCH|CASE|IF|FUNCTION|TRY|CATCH|FINALLY)__\\s*";
	
	// Control Flow Statement Out: Needs Dedent After
	public static final String C_CTRL_FLOW_OUT = "^\\s*(__END__)\\s*$";
	
	// Language Substitution File Format
	public static final String C_LANG_SUB_FMT = "^\\s*(__[A-Z_]+__)\\s+(.*)\\s*$";
	
	// Language Construct File Format
	public static final String C_LANG_CONS_FMT = "^\\s*(.+)\\s+[|]\\s+(.*)\\s*$";
	
	// Standard For Statement
	public static final String C_CTRL_FOR = "(__FOR__)\\s+" + ALO_ANYTHING + "\\s+(__DO__)";
	
	// Enhanced For Statement
	public static final String C_CTRL_FOR_IN = "(__FOR__)\\s+" + ALO_ANYTHING + "\\s+(__IN__)\\s+" + ALO_ANYTHING + "\\s+(__DO__)";
	
	// If Statement
	public static final String C_CTRL_IF = "(__IF__)\\s+" + ALO_ANYTHING + "\\s+(__THEN__)";
	
	// Else If Statement
	public static final String C_CTRL_ELIF = "(__ELSE_IF__)\\s+" + ALO_ANYTHING + "\\s+(__THEN__)";
	
	// Else Statement
	public static final String C_CTRL_ELSE = "(__ELSE__)";
	
	// Try Statement
	public static final String C_CTRL_TRY = "(__TRY__)";
	
	// Catch Statement
	public static final String C_CTRL_CATCH = "(__CATCH__)";
	
	// Finally Statement
	public static final String C_CTRL_FINAL = "(__FINALLY__)";
	
	// While Statement
	public static final String C_CTRL_WHILE = "(__WHILE__)\\s+" + ALO_ANYTHING + "\\s+(__DO__)";
	
	// Switch Statement
	public static final String C_CTRL_SWITCH = "(__SWITCH__)\\s+" + ALO_ANYTHING + "\\s+(__THEN__)";
	
	// Case Statement
	public static final String C_CTRL_CASE = "(__CASE__)";
	
	// Default Statement
	public static final String C_CTRL_DEFAULT = "(__DEFAULT__)";
	
	// Function 
	public static final String C_CTRL_FUNC = "(__FUNCTION__)";
	
	// Parenthesized expression
	public static final String C_CTRL_PAREN = "^[(]" + C_ALO_ANYTHING + "[)]$";
	
	// Auto String Format
	public static final String C_AUTO_STRING = C_ANYTHING_NONALNUM_END + "__AUTO_STRING_([0-9]+)__" + C_ANYTHING_NONALNUM_BEGIN;
	
	// Increment or decrement TAC
	public static final String C_CTRL_TAC = VARIABLE + "\\s*=\\s*" + VARIABLE + "\\s*(" + NORMAL_OP + 
			")\\s*(" + FUNCTION + "|" + VARIABLE + "|" + NUMBER + ")";
	
	// All The Control Flow Statements
	public static final String C_CTRL_ALL = C_CTRL_FOR_IN +  "|" + C_CTRL_FOR + "|" + C_CTRL_IF + "|" + C_CTRL_ELIF + "|" + 
				C_CTRL_ELSE + "|" + C_CTRL_TRY + "|" + C_CTRL_CATCH + "|" + C_CTRL_FINAL + "|" + C_CTRL_WHILE + "|" + 
				C_CTRL_SWITCH + "|" + C_CTRL_CASE + "|" + C_CTRL_DEFAULT + "|" + C_CTRL_FUNC;
	
	/********************************************************************************************
	 * 										CLASS STRUCTURE										*
	 ********************************************************************************************/
	// Project Class Name
	public static final String C_PROJ_CLASS = "^\\s*(\\+\\s*\\+|class\\s+)\\s*([a-zA-Z_][a-zA-Z0-9_]*)\\s*$";
	
	// Project Function
	public static final String C_PROJ_FUNC = "^\\s*(\\+|\\-|public|private)\\s*([a-zA-Z_][a-zA-Z0-9_]*)\\s*[(]\\s*(.*)\\s*[)]\\s*$";
	
	// Project Global Variable
	public static final String C_PROJ_VAR = "^\\s*(private|public|static|global)\\s+([a-zA-Z_][a-zA-Z0-9_]*)\\s*$";

}
