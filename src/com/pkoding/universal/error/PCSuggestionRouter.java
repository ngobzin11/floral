package com.pkoding.universal.error;

import java.util.*;
import com.pkoding.universal.util.*;
import com.pkoding.universal.warning.*;

/*
 * TODO: Integrate some of this into a Grails domain class and use BootStrap to build database + 
 * 		a Service to handle the rest of the logic
 */
public class PCSuggestionRouter {
	
	// <prefix> <keyphrase|keyphrase|...> <pattern|pattern|...> <breakdown-line|breakdown-line|...>
	private static String[][] prefix_data = {
		{"get", "get char at from|get char from", "get char at [position] from [string]|get [index]" +
			"[-][th|nd|rd|st] char from [string]", "[position|index] <Integer> - Character position|" +
			"[-] <optional> - Used for code readability|[string] <String> - The string you want the" +
			" character from"},
		{"char", "char from|char at from", "[index][-][th|nd|rd|st] char from [string]|char at " +
			"[position] from [string]", "[position|index] <Integer> - Character position|[-] <optional>" +
			" - Used for code readability|[string] <String> - The string you want the character from"},
		{"charat", "charat from", "charat [position] from [string]", "[position] <Integer> - Character " +
			"position|[string] <String> - The string you want the character from"},
		{"element", "element at from|element from", "element at [position] from [array]|[index][-]" +
			"[th|nd|rd|st] element from [array]", "[position|index] <Integer> - Element position|" +
			"[-] <optional> - Used for code readability|[array] <Array> - The array you want the" +
			" element from"},
		{"element at", "element at from", "element at [position] from [array]|", "[position] <Integer>" +
			" - Element position|[array] <Array> - The array you want the element from"},
		{"item", "item at from|item from", "item at [position] from [array]|[index][-][th|nd|rd|st] " +
			"item from [array]", "[position|index] <Integer> - Item position|[-] <optional> - Used " +
			"for code readability|[array] <Array> - The array you want the item from"},
		{"substring", "substring of from to|substring from to|substring of from|substring from", 
			"substring of [string] from [start] to [end]|substring [string] from [start] to [end]|" +
			"substring of [string] from [start]|substring [string] from [start]", "[string] <String>" +
			" - String you want the substring from|[start] <Integer> - Substring start position|" +
			"[end] <Integer> - Substring end position (not inclusive)"},
		{"find", "find in", "find [object] in [array]", "[object] <Object> (String, Integer, ...) - " +
			"The object you are trying to find from the array|[array] <Array> - The array you are " +
			"trying to locate the object from"},
		{"index", "index of in", "index of [substring] in [string]", "[substring] <String> - The " +
			"substring whose index you are trying to find in the string|[string] <String> - The " +
			"string you are trying to find the index from"},
		{"index of", "index of in", "index of [substring] in [string]", "[substring] <String> - The " +
			"substring whose index you are trying to find in the string|[string] <String> - The " +
			"string you are trying to find the index from"},
		{"indexof", "indexof in", "indexof [substring] in [string]", "[substring] <String> - The " +
			"substring whose index you are trying to find in the string|[string] <String> - The " +
			"string you are trying to find the index from"},
		{"length", "length of", "length of [string]", "[string] <String> - String whose length you " +
			"are trying to find"},
		{"size", "size of", "size of [array]", "[array] <Array> - Array whose length/size you " +
			"are trying to find"},
		{"absolute", "absolute value|absolute value of", "absolute value [number]|absolute value of" +
			" [number]", "[number] <Number> (Integer, Float, Double) - The number whose absolute " +
			"value you are trying to get"},
		{"insert", "insert in|insert in at", "insert [object] in [array]|insert [object] in [array] " +
			"at [index]", "[object] <Object> (String, Integer, ...) - The object you are trying to " +
			"insert into the array|[index] <Integer> - The position you want to add the object at|" +
			"[array] <Array> - The array you want to add the object to"},
		{"empty", "empty array", "arr = empty array", "Used to initialize an empty array"},
		{"NOT", "NOT identical|NOT identical to|NOT equal to|NOT equal", "[obj_1] not identical [obj_2]|" +
			"[obj_1] NOT identical to [obj_2]|[obj_1] NOT equal to [obj_2]", "[obj_1 & obj_2] <Object>" +
			" - The objects you are trying to compare|[NOT] - For the boolean not use NOT " +
			"(case-sensitive)"},
		{"NOT equal", "not equal to", "[obj_1] not equal to [obj_2]", "[obj_1 & obj_2] <Object> - " +
			"The objects you are trying to compare"},
		{"NOT equal to", "not equal to", "[obj_1] not equal to [obj_2]", "[obj_1 & obj_2] <Object> - " +
			"The objects you are trying to compare"},
		{"add", "add to|add and", "add [number_1] to [number_2]|add [number_1] and [number_2]", 
			"[number_1 & number_2] <Number> (Integer, Float, Double) - The numbers you are trying to " +
			"add together"},
		{"added", "added with|added to", "[number_1] added to [number_2]|[number_1] added with [number_2]",
			"[number_1 & number_2] <Number> (Integer, Float, Double) - The numbers you are trying to " +
			"add together"},
		{"multiplied", "multiplied by", "[number_1] multiplied by [number_2]", "[number_1 & number_2] " +
			"<Number> (Integer, Double, Float) - The numbers you are trying to multiply together"},
		{"divided", "divided by", "[number_1] divided by [number_2]", "[number_1] <Number> (Integer, " +
			"Double, Float) - The number you are trying to divide|[number_2] <Number> (Integer, " +
			"Double, Float) - The number you are trying to divide by"},
		{"assign", "assign to", "assign [variable] to [value]", "[variable] <Object> (Integer, String, " +
			"...) - The variable you are assigning a value to|[value] <Object> (Integer, String, ...) - " +
			"The value you are assigning to the variable"},
		{"set", "set to", "set [variable] to [value]", "[variable] <Object> (Integer, String, " +
			"...) - The variable you are assigning a value to|[value] <Object> (Integer, String, ...) - " +
			"The value you are assigning to the variable"},
		{"greater", "greater than|greater than or equal to", "[number_1] greater than [number_2]|" +
			"[number_1] greater than or equal to [number_2]", "[number_1 & number_2] <Number> (Integer, " +
			"Float, Double) - The numbers being compared"},
		{"less", "less than|less than or equal to", "[number_1] less than [number_2]|[number_1] less " +
			"than or equal to [number_2]", "[number_1 & number_2] <Number> (Integer, Float, Double) - " +
			"The numbers being compared"},
		{"bitwise", "bitwise or|bitwise and|bitwise xor|bitwise not", "[value_1] bitwise or [value_2]|" +
			"[value_1] bitwise and [value_2]|[value_1] bitwise xor [value_2]|[value_1] bitwise not " +
			"[value_2]|", "[value_1 & value_2] <Number> - The numbers you are performing bitwise " +
			"operations on"},
		{"shift", "shift left by|shift right by", "shift [number_1] left by [number_2]|shift [number_1] " +
			"right by [number_2]", "[number_1] <Number> - The number whose bits you want to shift|" +
			"[number_2] <Integer> - The number of bits you want to shift by"},
		{"cast", "cast to", "cast [variable] to [object]", "[variable] - The variable whose type you " +
			"are changing|[object] <Object> (string, int, float, double, long, char, short, boolean, " +
			"array) - The type you are casting the variable to"}
	};

	// <keyphrase> <pattern> <breakdown-line|breakdown-line|...>
	private static String[][] keyphrase_data = {
		{"NEW array", "NEW array {object, object, object, ...}", "[object] <Object> - The objects" +
			" you are filling the array with"},
		{"get char at from", "get char at [position] from [string]", "[position] <Integer> - " +
			"Character position|[string] <String> - The string you want the character from"},
		{"get char from", "get [index][-][th|nd|rd|st] char from [string]", "[index] <Integer> - " +
			"Character position|[string] <String> - The string you want the character from"},
		{"char from", "[index][-][th|nd|rd|st] char from [string]", "[index] <Integer> - Character " +
			"position|[-] <optional> - Used for code readability|[string] <String> - The string you " +
			"want the character from"},
		{"char at from", "char at [position] from [string]", "[position] <Integer> - Character " +
			"position|[string] <String> - The string you want the character from"},
		{"charat from", "charat [position] from [string]", "[position] <Integer> - Character " +
			"position|[string] <String> - The string you want the character from"},
		{"element at from", "element at [position] from [array]", "[position] <Integer> - " +
			"Element position|[array] <Array> - The array you want the element from"},
		{"element from", "[index][-][th|nd|rd|st] element from [array]", "[index] <Integer> - " +
			"Element position|[-] <optional> - Used for code readability|[array] <Array> - The array" +
			" you want the element from"},
		{"item at from", "item at [position] from [array]", "[position] <Integer> - Item position|" +
			"[array] <Array> - The array you want the item from"},
		{"item from", "[index][-][th|nd|rd|st] item from [array]", "[index] <Integer> - Item position|" +
			"[-] <optional> - Used for code readability|[array] <Array> - The array you want the item" +
			" from"},
		{"substring of from to", "substring of [string] from [start] to [end]", "[string] <String> - " +
			"String you want the substring from|[start] <Integer> - Substring start position|" +
			"[end] <Integer> - Substring end position (not inclusive)"},
		{"substring from to", "substring [string] from [start] to [end]", "[string] <String> - String " +
			"you want the substring from|[start] <Integer> - Substring start position|[end] <Integer> " +
			"- Substring end position (not inclusive)"},
		{"substring of from", "substring of [string] from [start]", "[string] <String> - String you " +
			"want the substring from|[start] <Integer> - Substring start position"},
		{"substring from", "substring [string] from [start]", "[string] <String> - String you want the " +
			"substring from|[start] <Integer> - Substring start position"},
		{"lowercase", "lowercase [string]", "[string] <String> - The string you want to convert to " +
			"lowercase"},
		{"uppercase", "uppercase [string]", "[string] <String> - The string you want to convert to " +
			"uppercase"},
		{"split", "split [string]", "[string] <String> - The string you want to split"},
		{"split by", "split [string] by [regex]", "[string] <String> - The string you want to split|" +
			"[regex] <String> - The regular expression or substring you are splitting the string with"},
		{"find in", "find [object] in [array]", "[object] <Object> (Number, String, ...) - The object " +
			"whose position you are trying to find in the array|[array] <Array> - The array you are " +
			"trying to find the object from"},
		{"index of in", "index of [substring] in [string]", "[substring] <String> - The substring whose" +
			" start position you are trying to find|[string] <String> - The string in which you are " +
			"trying to find the substring"},
		{"indexof in", "indexof [substring] in [string]", "[substring] <String> - The substring whose" +
			" start position you are trying to find|[string] <String> - The string in which you are " +
			"trying to find the substring"},
		{"NOT", "NOT [boolean]", "[boolean] <Boolean> - The boolean value being complemented"},
		{"OR", "[boolean_1] OR [boolean_2]", "[boolean_1 & boolean_2] <Boolean> - The boolean values at " +
			"least one of which has to evaluate to true"},
		{"AND", "[boolean_1] AND [boolean_2]", "[boolean_1 & boolean_2] <Boolean> - The boolean values " +
			"both of which have to evaluate to true"},
		{"TRUE", "variable = TRUE", ""},
		{"FALSE", "variable = FALSE", ""},
		{"length of", "length of [string]", "[string] <String> - The string whose length you are trying" +
			" to calculate"},
		{"size of", "size of [array]", "[array] <Array> - The array whose size you are trying" +
			" to find"},
		{"round", "round [number]", "[number] <Number> (Float, Double) - The number you are trying to " +
			"round to the nearest integer"},
		{"absolute value", "absolute value [number]", "[number] <Number> (Float, Double, ...) - The " +
			"number whose absolute value you are trying to calculate"},
		{"absolute value of", "absolute value of [number]", "[number] <Number> (Float, Double, ...) - " +
			"The number whose absolute value you are trying to calculate"},
		{"insert in", "insert [object] in [array]", "[object] <Object> (Integer, String, ...) - The " +
			"object you are trying to insert to the array|[array] <Array> - The array you are trying to" +
			" add an object to"},
		{"insert in at", "insert [object] in [array] at [position]", "[object] <Object> (Integer, String" +
			", ...) - The object you are trying to insert to the array|[array] <Array> - The array you " +
			"are trying to add an object to|[position] <Integer> - The position you are adding the " +
			"object in"},
		{"sort", "sort [array]", "[array] <Array> - The array you are sorting"},
		{"system_out", "system_out [object]", "[object] <Object> (Integer, String, ...) - The object " +
			"you are printing"},
		{"conc", "[object_1] conc [object_2]", "[object_1 & object_2] <Object> - The objects you are " +
			"concatenating into a string"},
		{"empty array", "arr = empty array", ""},
		{"func", "func [function_name]([arguments])", "[function_name] - The name of the function you" +
			" are initializing|[arguments] - Comma-separated function arguments"},
		{"identical", "[object_1] identical [object_2]", "[object_1 & object_2] <Object> - The objects " +
			"you are comparing"},
		{"identical to", "[object_1] identical to [object_2]", "[object_1 & object_2] <Object> - The " +
			"objects you are comparing"},
		{"NOT identical", "[object_1] NOT identical [object_2]", "[object_1 & object_2] <Object> - The " +
			"objects you are comparing"},
		{"NOT identical to", "[object_1] NOT identical to [object_2]", "[object_1 & object_2] <Object> " +
			"- The objects you are comparing"},
		{"NOT equal to", "[object_1] NOT equal to [object_2]", "[object_1 & object_2] <Object> - The " +
			"objects you are comparing"},
		{"NOT equal", "[object_1] NOT equal [object_2]", "[object_1 & object_2] <Object> - The " +
			"objects you are comparing"},
		{"NULL", "variable = NULL", ""},
		{"power", "[number_1] power [number_2]", "[number_1] <Number> - The number you are " +
			"exponentiating|[number_2] <Number> - The exponent"},
		{"to the power", "[number_1] to the power [number_2]", "[number_1] <Number> - The number you " +
			"are exponentiating|[number_2] <Number> - The exponent"},
		{"negate", "negate [number]", "[number] <Number> - The number you are negating"},
		{"negative", "negative [number]", "[number] <Number> - The number you are negating"},
		{"equal", "[variable] equal [value]", "[variable] - The variable whose value you are going to " +
			"set|[value] <Object> - The value you are assigning to the variable"},
		{"equals", "[variable] equals [value]", "[variable] - The variable whose value you are going to " +
			"set|[value] <Object> - The value you are assigning to the variable"},
		{"equal to", "[variable] equal to [value]", "[variable] - The variable whose value you are going" +
			" to set|[value] <Object> - The value you are assigning to the variable"},
		{"add to", "add [number_1] to [number_2]", "[number_1 & number_2] <Number> - The numbers you " +
			"are adding together"},
		{"add and", "add [number_1] and [number_2]", "[number_1 & number_2] <Number> - The numbers you " +
			"are adding together"},
		{"added with", "[number_1] added with [number_2]", "[number_1 & number_2] <Number> - The " +
			"numbers you are adding together"},
		{"added to", "[number_1] added to [number_2]", "[number_1 & number_2] <Number> - The numbers " +
			"you are adding together"},
		{"plus", "[number_1] plus [number_2]", "[number_1 & number_2] <Number> - The numbers you " +
			"are adding together"},
		{"subtract", "[number_1] subtract [number_2]", "[number_1] <Number> - The number you are " +
			"subtracting the second number from|[number_2] <Number> - The number you are " +
			"subtracting from the first number"},
		{"subtract from", "subtract [number_1] from [number_2]", "[number_1] <Number> - The number you" +
			" are subtracting from the second number from|[number_2] <Number> - The number you are " +
			"subtracting the first number from"},
		{"minus", "[number_1] minus [number_2]", "[number_1] <Number> - The number you are subtracting" +
			" the second number from|[number_2] <Number> - The number you are subtracting from the " +
			"first number"},
		{"multiplied by", "[number_1] multiplied by [number_2]", "[number_1 & number_2] <Number> - The" +
			" numbers you are multiplying together"},
		{"multiply", "[number_1] multiply [number_2]", "[number_1 & number_2] <Number> - The numbers " +
			"you are multiplying together"},
		{"times", "[number_1] times [number_2]", "[number_1 & number_2] <Number> - The numbers you are " +
			"multiplying together"},
		{"divided by", "[number_1] divided by [number_2]", "[number_1] <Number> - The number you are " +
			"dividing from|[number_2] <Number> - The number you are dividing by"},
		{"divide by", "divide [number_1] by [number_2]", "[number_1] <Number> - The number you are " +
			"dividing from|[number_2] <Number> - The number you are dividing by"},
		{"over", "[number_1] over [number_2]", "[number_1] <Number> - The number you are " +
			"dividing from|[number_2] <Number> - The number you are dividing by"},
		{"mod", "[number_1] mod [number_2]", "[number_1] <Number> - The number whose remainder you " +
			"are trying to calculate|[number_2] <Number> - The number you are dividing by"},
		{"modulus", "[number_1] modulus [number_2]", "[number_1] <Number> - The number whose remainder " +
			"you are trying to calculate|[number_2] <Number> - The number you are dividing by"},
		{"assign to", "assign [variable] to [value]", "[variable] - The variable whose value you are" +
			" going to set|[value] <Object> - The value you are assigning to the variable"},
		{"set to", "set [variable] to [value]", "[variable] - The variable whose value you are going to" +
			" set|[value] <Object> - The value you are assigning to the variable"},
		{"increment", "increment [variable]", "[variable] - The variable whose value you are " +
				"incrementing by 1"},
		{"increment by", "increment [variable] by [number]", "[variable] - The variable whose value " +
			"you are incrementing by number|[number] <Number> - The number you are incrementing by"},
		{"decrement", "decrement [variable]", "[variable] - The variable whose value you are " +
			"decrementing by 1"},
		{"decrement by", "decrement [variable] by [number]", "[variable] - The variable whose value " +
			"you are decrementing by 1|[number] <Number> - The number you are decrementing by"},
		{"greater than", "[number_1] greater than [number_2]", "[number_1 & number_2] <Number> - The " +
			"numbers you are comparing"},
		{"greater than or equal to", "[number_1] greater than or equal to [number_2]", "[number_1" +
			" & number_2] <Number> - The numbers you are comparing"},
		{"less than", "[number_1] less than [number_2]", "[number_1 & number_2] <Number> - The " +
			"numbers you are comparing"},
		{"less than or equal to", "[number_1] less than or equal to [number_2]", "[number_1 &" +
			" number_2] <Number> - The numbers you are comparing"},
		{"bitwise or", "[number_1] bitwise or [number_2]", "[value_1 & value_2] <Number> - " +
			"The numbers you are performing bitwise operations on"},
		{"bitwise and", "[number_1] bitwise and [number_2]", "[value_1 & value_2] <Number> - " +
			"The numbers you are performing bitwise operations on"},
		{"bitwise xor", "[number_1] bitwise xor [number_2]", "[value_1 & value_2] <Number> - " +
			"The numbers you are performing bitwise operations on"},
		{"bitwise not", "[number_1] bitwise not [number_2]", "[value_1 & value_2] <Number> - " +
			"The numbers you are performing bitwise operations on"},
		{"shift left by", "shift [number_1] left by [number_2]", "[number_1] <Number> - The number " +
			"whose bits you want to shift left|[number_2] <Integer> - The number of bits you want to" +
			" shift left by"},
		{"shift right by", "shift [number_1] right by [number_2]", "[number_1] <Number> - The number " +
			"whose bits you want to arithmetically shift right|[number_2] <Integer> - The number of " +
			"bits you want to arithmetically shift right by"},
		{"for", "for ([init]; [condition]; [step]) {", "[init] - Initialization statement, e.g. i = 0|" +
			"[condition] - Running condition, e.g. i <= 5|[step] - Increment or decrement, e.g. i++"},
		{"for in", "for ([variable] in [collection]) {", "[variable] - Item in the collection you " +
			"are iterating over|[collection] (Array, List, ...) - The collection you are iterating over"},
		{"foreach in", "foreach ([variable] in [collection]) {", "[variable] - Item in the collection " +
			"you are iterating over|[collection] (Array, List, ...) - The collection you are iterating" +
			" over"},
		{"for each in", "for each ([variable] in [collection]) {", "[variable] - Item in the collection" +
			" you are iterating over|[collection] (Array, List, ...) - The collection you are iterating" +
			" over"},
		{"for :", "for ([variable] : [collection]) {", "[variable] - Item in the collection you are " +
			"iterating over|[collection] (Array, List, ...) - The collection you are iterating over"},
		{"for in range", "for ([variable] in range([start][end][step])) {", "[variable] <Integer> - " +
			"A number between [start] and [end]|[start] <Integer> - The starting position, initialized" +
			" as 0 if not set|[end] <Integer> - The ending position (not inclusive)|[step] <Integer> -" +
			" The increment/decrement step, initialized as 1 if not set"},
		{"foreach as", "foreach ([collection] as [variable]) {", "[variable] - Item in the collection" +
			" you are iterating over|[collection] (Array, List, ...) - The collection you are iterating" +
			" over"},
		{"for each as", "for each ([collection] as [variable]) {", "[variable] - Item in the collection" +
			" you are iterating over|[collection] (Array, List, ...) - The collection you are iterating" +
			" over"},
		{"while", "while ([condition]) {", "[condition] <Boolean> - The loop running condition"},
		{"if", "if ([condition]) {", "[condition] <Boolean> - The if clause contents are executed if " +
			"this condition is met"},
		{"else if", "else if ([condition]) {", "[condition] <Boolean> - The else if clause contents " +
			"are executed if this condition is met"},
		{"elif", "elif ([condition]) {", "[condition] <Boolean> - The elif clause contents are executed " +
			"if this condition is met"},
		{"elseif", "elseif ([condition]) {", "[condition] <Boolean> - The elseif clause contents are " +
			"executed if this condition is met"},
		{"? :", "[variable] = [condition] ? [value_true] : [value_false]", "[variable] - The variable " +
			"you are assinging a value to|[condition] <Boolean> - Condition being checked before " +
			"a value is assigned|[value_true] <Object> - Value assigned to variable if condition is met|" +
			"[value_false] <Object> - Value assigned to variable if condition is not met"},
		{"cast to", "cast [variable] to [object]", "[variable] - The variable whose type you are " +
			"changing|[object] <Object> (string, int, float, double, long, char, short, boolean, array)" +
			" - The type you are casting the variable to"}
	};

	private static HashMap<String, String[]> prefix_table = null;
	
	private static HashMap<String, String[]> keyphrase_table = null;
	
	/**
	 * Warning Requires:
	 * 	- prefix & keyphrase should contain the library name to avoid collision
	 * 	<prefix> <keyphrase|keyphrase|...> <pattern|pattern|...> <breakdown-line|breakdown-line|...>
	 */
	public static void addWarning(String prefix, String keyphrase, String pattern, String breakdown) {
		if ((prefix_table == null) || (keyphrase_table == null)) {
			prefix_table = prefixBuild();
			keyphrase_table = keyphraseBuild();
		}
		String[] arr = {keyphrase, pattern, breakdown};
		prefix_table.put(prefix, arr);
		
		String[] arr1 = {pattern, breakdown};
		keyphrase_table.put(keyphrase, arr1);
	}
	
	public static Tuple<ArrayList<PCSuggestion>, PCClarification> suggest(WarningType type, String phrase) {
		if ((prefix_table == null) || (keyphrase_table == null)) {
			prefix_table = prefixBuild();
			keyphrase_table = keyphraseBuild();
		}
		
		Tuple<ArrayList<PCSuggestion>, PCClarification> tup = null;
		switch (type) {
			case MISSING_SUFFIX:
				tup = prefixSuggest(phrase);
				break;
			case MISSING_PARAMS:
				tup = phraseSuggest(phrase);
				break;
		}
		return tup; 
	}
	
	private static Tuple<ArrayList<PCSuggestion>, PCClarification> phraseSuggest(String keyphrase) {
		// <keyphrase|keyphrase|...> <pattern|pattern|...> <breakdown-line|breakdown-line|...>
		String[] raw_pref_data = keyphrase_table.get(keyphrase);
		
		// Add Suggestions
		ArrayList<PCSuggestion> suggestions = new ArrayList<PCSuggestion>();
		suggestions.add(new PCSuggestion(keyphrase, raw_pref_data[0]));
				
		// Line by line suggestion breakdown
		String[] breakdown = raw_pref_data[1].split("[|]");
				
		// Add Clarifications
		PCClarification clarification = new PCClarification();
		for (String detail : breakdown) {
			String[] arr = detail.split(" - ");
			clarification.addDetail(new PCClarificationDetail(arr[0], arr[1]));
		}
		
		return new Tuple<ArrayList<PCSuggestion>, PCClarification>(suggestions, clarification);
	}
	
	private static Tuple<ArrayList<PCSuggestion>, PCClarification> prefixSuggest(String prefix) {		
		// <keyphrase|keyphrase|...> <pattern|pattern|...> <breakdown-line|breakdown-line|...>
		String[] raw_pref_data = prefix_table.get(prefix);
		
		// Key-phrases related to this prefix
		String[] keyphrases = raw_pref_data[0].split("[|]");
		
		// Patterns associated with the keyphrases
		String[] patterns = raw_pref_data[1].split("[|]");
		
		// Add Suggestions
		ArrayList<PCSuggestion> suggestions = new ArrayList<PCSuggestion>();
		for (int i = 0; i < patterns.length; i++)
			suggestions.add(new PCSuggestion(keyphrases[i], patterns[i]));
		
		// Line by line suggestion breakdown
		String[] breakdown = raw_pref_data[2].split("[|]");
		
		// Add Clarifications
		PCClarification clarification = new PCClarification();
		for (String detail : breakdown) {
			String[] arr = detail.split(" - ");
			clarification.addDetail(new PCClarificationDetail(arr[0], arr[1]));
		}
		
		return new Tuple<ArrayList<PCSuggestion>, PCClarification>(suggestions, clarification);
	}

	private static HashMap<String, String[]> prefixBuild() {
		HashMap<String, String[]> prefs = new HashMap<String, String[]>();
		
		for (String[] components : prefix_data) {
			// First Component Is The Key
			String key = components[0];
			
			// <keyphrase|keyphrase|...> <pattern|pattern|...> <breakdown-line|breakdown-line|...>
			String[] relation = {components[1], components[2], components[3]};
			
			// Add Everything To Prefix Table
			prefs.put(key, relation);
		}
		
		return prefs;
	}

	private static HashMap<String, String[]> keyphraseBuild() {
		HashMap<String, String[]> keys = new HashMap<String, String[]>();
		
		for (String[] components : keyphrase_data) {
			// First Component Is The Key
			String key = components[0];
			
			// <pattern> <breakdown-line|breakdown-line|...>
			String[] relation = {components[1], components[2]};
			
			// Add Everything To KeyPhrase Table
			keys.put(key, relation);
		}
		
		return keys;
	}
}
