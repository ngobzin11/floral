package com.pkoding.preprocess.substitute;

import java.util.*;
import java.util.regex.*;
import com.pkoding.universal.util.*;

public class CoreReplace {
	
	public static final Pattern keywords = Pattern.compile(RegexDefinition.NC_BEGIN + 
		"(plus|minus|times|over|modulus|power|equal|increment|decrement|element|character|end|in|" +
		"declare|set|substring|lowercase|uppercase|split|size|length|round|insert|absolute|while|" +
		"sort|index|greater|less|bitwise|shift|print|concatenate|identical|empty|null|not|or|do|" +
		"and|true|false|cast|new|remove|raw|find|function|return|break|continue|for|if|then|else|" +
		"try|catch|finally|assert|class|negative|positive|\\{|loop|public|private|static|global)" + 
	RegexDefinition.NC_END);
	
	private static final String[][] keyphrase_data = {
		{"plus", null}, {"minus", null}, {"times", null}, {"over", null}, {"modulus", null}, {"do", null},
		{"power", null}, {"lowercase", null}, {"uppercase", null}, {"round", null}, {"return", null},
		{"sort", null}, {"print", null}, {"concatenate", null}, {"null", null}, {"function", null},
		{"or", null}, {"and", null}, {"true", null}, {"false", null}, {"continue", null}, {"negative", null},
		{"break", null}, {"for", null}, {"if", null}, {"then", null}, {"while", null}, {"end", null},
		{"in", null}, {"try", null}, {"catch", null}, {"finally", null}, {"class", null}, {"assert", null},
		{"positive", null}, {"public", null}, {"private", null}, {"global", null}, {"static", null},
		{"loop",
			// loop [variable] up/down from [start] to [stop] by [step]
			RegexDefinition.NC_BEGIN + "(down|up)\\s+(from)\\s+" + RegexDefinition.NC_ANYTHING + 
			"\\s+(to)\\s+" + RegexDefinition.NC_ANYTHING + "\\s+(by)" + RegexDefinition.NC_END + "|" +
			// loop [variable] up/down from [start] to [stop]
			RegexDefinition.NC_BEGIN + "(down|up)\\s+(from)\\s+" + RegexDefinition.NC_ANYTHING + 
			"\\s+(to)" + RegexDefinition.NC_END + "|" +
			// loop [variable] to [stop]
			RegexDefinition.NC_BEGIN + "(to)" + RegexDefinition.NC_END},
		{"not", 
			RegexDefinition.NC_BEGIN + "(equal|identical)\\s+(to)" + RegexDefinition.NC_END},
		{"else", 
			RegexDefinition.NC_BEGIN + "(if)" + RegexDefinition.NC_END}, 
		{"raw", 
			RegexDefinition.NC_BEGIN + "(input)" + RegexDefinition.NC_END},
		{"identical", 
			RegexDefinition.NC_BEGIN + "(to)" + RegexDefinition.NC_END},
		{"equal", 
			RegexDefinition.NC_BEGIN + "(to)" + RegexDefinition.NC_END}, 
		{"substring", 
			RegexDefinition.NC_BEGIN + "(of)\\s+" + RegexDefinition.NC_ANYTHING + "\\s+(from)\\s+" + 
			RegexDefinition.NC_ANYTHING + "\\s+(to)" + RegexDefinition.NC_END + "|" +
			RegexDefinition.NC_BEGIN + "(of)\\s+" + RegexDefinition.NC_ANYTHING + "\\s+(from)" + 
			RegexDefinition.NC_END}, 
		{"cast", 
			RegexDefinition.NC_BEGIN + "(to)" + RegexDefinition.NC_END}, 
		{"new", 
			RegexDefinition.NC_BEGIN + "(array)" + RegexDefinition.NC_END},
		{"remove", 
			RegexDefinition.NC_BEGIN + "(last)\\s+(from)" + RegexDefinition.NC_ANYTHING},
		{"index",
			RegexDefinition.NC_BEGIN + "(of)\\s+" + RegexDefinition.NC_ANYTHING + "\\s+(in)" + 
			RegexDefinition.NC_END}, 
		{"find", 
			RegexDefinition.NC_BEGIN + "(in)" + RegexDefinition.NC_END}, 
		{"greater", 
			RegexDefinition.NC_BEGIN + "(than)\\s+(or)\\s+(equal)\\s+(to)" + RegexDefinition.NC_END + 
			"|" + RegexDefinition.NC_BEGIN + "(than)" + RegexDefinition.NC_END},
		{"less", 
			RegexDefinition.NC_BEGIN + "(than)\\s+(or)\\s+(equal)\\s+(to)" + RegexDefinition.NC_END + 
			"|" + RegexDefinition.NC_BEGIN + "(than)" + RegexDefinition.NC_END}, 
		{"bitwise", 
			RegexDefinition.NC_BEGIN + "(or|and|xor|not)" + RegexDefinition.NC_END}, 
		{"shift", 
			RegexDefinition.NC_BEGIN + "(left|right)\\s+(by)" + RegexDefinition.NC_END}, 
		{"empty", 
			RegexDefinition.NC_BEGIN + "(array|string)" + RegexDefinition.NC_END}, 
		{"split", 
			RegexDefinition.NC_BEGIN + "(by)" + RegexDefinition.NC_END}, 
		{"size", 
			RegexDefinition.NC_BEGIN + "(of)" + RegexDefinition.NC_END}, 
		{"length", 
			RegexDefinition.NC_BEGIN + "(of)" + RegexDefinition.NC_END}, 
		{"insert", 
			RegexDefinition.NC_BEGIN + "(in)\\s+" + RegexDefinition.NC_ANYTHING + "\\s+(at)" + 
			RegexDefinition.NC_END + "|" + RegexDefinition.NC_BEGIN + "(in)" + RegexDefinition.NC_END}, 
		{"absolute", 
			RegexDefinition.NC_BEGIN + "(value)\\s+(of)" + RegexDefinition.NC_END}, 
		{"increment", 
			RegexDefinition.NC_BEGIN + "(by)" + RegexDefinition.NC_END}, 
		{"decrement", 
			RegexDefinition.NC_BEGIN + "(by)" + RegexDefinition.NC_END}, 
		{"element", 
			RegexDefinition.NC_BEGIN + "(at)\\s+" + RegexDefinition.NC_ANYTHING + "\\s+(from)" + 
			RegexDefinition.NC_END}, 
		{"character", 
			RegexDefinition.NC_BEGIN + "(at)\\s+" + RegexDefinition.NC_ANYTHING + "\\s+(from)" + 
			RegexDefinition.NC_END}, 
		{"declare", 
			RegexDefinition.NC_BEGIN + "(as)" + RegexDefinition.NC_END}, 
		{"set", 
			RegexDefinition.NC_BEGIN + "(to)" + RegexDefinition.NC_END},
		{"{",
			RegexDefinition.NC_BEGIN + "(\\})" + RegexDefinition.NC_END}
	};
	
	private static final String[][] argument_data = {
		{"plus", 
			RegexDefinition.C_ALO_ANYTHING_NONALNUM_END + "plus" + 
			RegexDefinition.C_ALO_ANYTHING_NONALNUM_BEGIN},
		{"public", 
			RegexDefinition.C_ANYTHING_NONALNUM_END + "public" + 
			RegexDefinition.C_ALO_ANYTHING_NONALNUM_BEGIN},
		{"private", 
			RegexDefinition.C_ANYTHING_NONALNUM_END + "private" + 
			RegexDefinition.C_ALO_ANYTHING_NONALNUM_BEGIN},
		{"static", 
			RegexDefinition.C_ALO_ANYTHING_NONALNUM_END + "static" + 
			RegexDefinition.C_ALO_ANYTHING_NONALNUM_BEGIN},
		{"global", 
			RegexDefinition.C_ALO_ANYTHING_NONALNUM_END + "global" + 
			RegexDefinition.C_ALO_ANYTHING_NONALNUM_BEGIN},
		{"minus", 
			RegexDefinition.C_ALO_ANYTHING_NONALNUM_END + "minus" + 
			RegexDefinition.C_ALO_ANYTHING_NONALNUM_BEGIN}, 
		{"times", 
			RegexDefinition.C_ALO_ANYTHING_NONALNUM_END + "times" + 
			RegexDefinition.C_ALO_ANYTHING_NONALNUM_BEGIN}, 
		{"over", 
			RegexDefinition.C_ALO_ANYTHING_NONALNUM_END + "over" + 
			RegexDefinition.C_ALO_ANYTHING_NONALNUM_BEGIN}, 
		{"modulus", 
			RegexDefinition.C_ALO_ANYTHING_NONALNUM_END + "modulus" + 
			RegexDefinition.C_ALO_ANYTHING_NONALNUM_BEGIN},
		{"equal to", 
			RegexDefinition.C_ALO_ANYTHING_NONALNUM_END + "equal\\s+to" + 
			RegexDefinition.C_ALO_ANYTHING_NONALNUM_BEGIN}, 
		{"power", 
			RegexDefinition.C_ALO_ANYTHING_NONALNUM_END + "(" + RegexDefinition.ALO_BRACKETS + "|" + 
			RegexDefinition.VARIABLE + "|" + RegexDefinition.DECIMAL + "|" + RegexDefinition.FUNCTION + 
			")\\s+power\\s+(" + RegexDefinition.ALO_BRACKETS + "|" + RegexDefinition.VARIABLE + "|" + 
			RegexDefinition.DECIMAL + "|" + RegexDefinition.FUNCTION + ")" + 
			RegexDefinition.C_ANYTHING_NONALNUM_BEGIN}, 
		{"lowercase", 
			RegexDefinition.C_ALO_ANYTHING_NONALNUM_END + "lowercase\\s+(" + RegexDefinition.ALO_BRACKETS
			+ "|" + RegexDefinition.VARIABLE + "|" + RegexDefinition.STRING + "|" + RegexDefinition.FUNCTION + 
			")" + RegexDefinition.C_ANYTHING_NONALNUM_BEGIN}, 
		{"uppercase", 
			RegexDefinition.C_ALO_ANYTHING_NONALNUM_END + "uppercase\\s+(" + RegexDefinition.ALO_BRACKETS
			+ "|" + RegexDefinition.VARIABLE + "|" + RegexDefinition.STRING + "|" + RegexDefinition.FUNCTION + 
			")" + RegexDefinition.C_ANYTHING_NONALNUM_BEGIN}, 
		{"round", 
			RegexDefinition.C_ALO_ANYTHING_NONALNUM_END + "round\\s+(" + RegexDefinition.ALO_BRACKETS + 
			"|" + RegexDefinition.VARIABLE + "|" + RegexDefinition.DECIMAL + "|" + RegexDefinition.FUNCTION + 
			")" + RegexDefinition.C_ANYTHING_NONALNUM_BEGIN},
			
		{"return", 
			RegexDefinition.C_ANYTHING_NONALNUM_END + "return" + RegexDefinition.C_ANYTHING_NONALNUM_BEGIN},
		{"sort", 
			RegexDefinition.C_ALO_ANYTHING_NONALNUM_END + "sort\\s+(" + RegexDefinition.ALO_BRACKETS + 
			"|" + RegexDefinition.VARIABLE + "|" + RegexDefinition.FUNCTION + ")" + 
			RegexDefinition.C_ANYTHING_NONALNUM_BEGIN}, 
		{"print", 
			RegexDefinition.C_ANYTHING_NONALNUM_END + "print\\s+(" + RegexDefinition.ALO_ANYTHING + 
			")" + RegexDefinition.C_ANYTHING_NONALNUM_BEGIN}, 
		{"concatenate", 
			RegexDefinition.C_ALO_ANYTHING_NONALNUM_END + "(" + RegexDefinition.ALO_BRACKETS + "|" + 
			RegexDefinition.DECIMAL + "|" + RegexDefinition.STRING + "|" + RegexDefinition.VARIABLE + 
			"|" + RegexDefinition.FUNCTION + ")\\s+concatenate\\s+(" + RegexDefinition.ALO_BRACKETS + "|" + 
			RegexDefinition.DECIMAL + "|" + RegexDefinition.STRING + "|" + RegexDefinition.FUNCTION +  "|" + 
			RegexDefinition.VARIABLE + ")" + RegexDefinition.C_ANYTHING_NONALNUM_BEGIN}, 
		{"identical to", 
			RegexDefinition.C_ALO_ANYTHING_NONALNUM_END + "identical\\s+to" +
			RegexDefinition.C_ALO_ANYTHING_NONALNUM_BEGIN}, 
		{"null", 
			RegexDefinition.C_ALO_ANYTHING_NONALNUM_END + "null" + 
			RegexDefinition.C_ANYTHING_NONALNUM_BEGIN},
		{"function", 
			RegexDefinition.C_ANYTHING_NONALNUM_END + "function" + 
			RegexDefinition.C_ANYTHING_NONALNUM_BEGIN},
		{"not identical to", 
			RegexDefinition.C_ALO_ANYTHING_NONALNUM_END + "not\\s+identical\\s+to" +
			RegexDefinition.C_ALO_ANYTHING_NONALNUM_BEGIN},
		{"not equal to", 
			RegexDefinition.C_ALO_ANYTHING_NONALNUM_END + "not\\s+equal\\s+to" +
			RegexDefinition.C_ALO_ANYTHING_NONALNUM_BEGIN},
		{"not", 
			RegexDefinition.C_ALO_ANYTHING_NONALNUM_END + "not" +
			RegexDefinition.C_ALO_ANYTHING_NONALNUM_BEGIN}, 
		{"or", 
			RegexDefinition.C_ALO_ANYTHING_NONALNUM_END + "or" +
			RegexDefinition.C_ALO_ANYTHING_NONALNUM_BEGIN}, 
		{"and", 
			RegexDefinition.C_ALO_ANYTHING_NONALNUM_END + "and" +
			RegexDefinition.C_ALO_ANYTHING_NONALNUM_BEGIN}, 
		{"true", 
			RegexDefinition.C_ALO_ANYTHING_NONALNUM_END + "true" +
			RegexDefinition.C_ANYTHING_NONALNUM_BEGIN}, 
		{"false", 
			RegexDefinition.C_ALO_ANYTHING_NONALNUM_END + "false" +
			RegexDefinition.C_ANYTHING_NONALNUM_BEGIN}, 
		{"raw input", 
			RegexDefinition.C_ANYTHING_NONALNUM_END + "raw\\s+input\\s+(" + RegexDefinition.ALO_BRACKETS 
			+ "|" + RegexDefinition.STRING + "|" + RegexDefinition.VARIABLE + "|" + RegexDefinition.FUNCTION + 
			")" + RegexDefinition.C_ANYTHING_NONALNUM_BEGIN},
		{"continue", 
			RegexDefinition.C_ANYTHING_NONALNUM_END + "continue" +
			RegexDefinition.C_ANYTHING_NONALNUM_BEGIN},
		{"break", 
			RegexDefinition.C_ANYTHING_NONALNUM_END + "break" +
			RegexDefinition.C_ALO_ANYTHING_NONALNUM_BEGIN},
		{"for", 
			RegexDefinition.C_ANYTHING_NONALNUM_END + "for" +
			RegexDefinition.C_ALO_ANYTHING_NONALNUM_BEGIN}, 
		{"if", 
			RegexDefinition.C_ANYTHING_NONALNUM_END + "if" +
			RegexDefinition.C_ALO_ANYTHING_NONALNUM_BEGIN}, 
		{"then", 
			RegexDefinition.C_ALO_ANYTHING_NONALNUM_END + "then" +
			RegexDefinition.C_ANYTHING_NONALNUM_BEGIN}, 
		{"do", 
			RegexDefinition.C_ALO_ANYTHING_NONALNUM_END + "do" +
			RegexDefinition.C_ANYTHING_NONALNUM_BEGIN},
		{"while", 
			RegexDefinition.C_ANYTHING_NONALNUM_END + "while" +
			RegexDefinition.C_ALO_ANYTHING_NONALNUM_BEGIN},
		{"end", 
			RegexDefinition.C_ANYTHING_NONALNUM_END + "end" +
			RegexDefinition.C_ANYTHING_NONALNUM_BEGIN},
		{"try", 
			RegexDefinition.C_ANYTHING_NONALNUM_END + "try" +
			RegexDefinition.C_ANYTHING_NONALNUM_BEGIN},
		{"catch", 
			RegexDefinition.C_ANYTHING_NONALNUM_END + "catch" +
			RegexDefinition.C_ANYTHING_NONALNUM_BEGIN},
		{"finally", 
			RegexDefinition.C_ANYTHING_NONALNUM_END + "finally" +
			RegexDefinition.C_ANYTHING_NONALNUM_BEGIN},
		{"class", 
			RegexDefinition.C_ANYTHING_NONALNUM_END + "class" +
			RegexDefinition.C_ANYTHING_NONALNUM_BEGIN},
		{"assert", 
			RegexDefinition.C_ANYTHING_NONALNUM_END + "assert" +
			RegexDefinition.C_ANYTHING_NONALNUM_BEGIN},
		{"negative", 
			RegexDefinition.C_ALO_ANYTHING_NONALNUM_END + "negative" + 
			RegexDefinition.C_ALO_ANYTHING_NONALNUM_BEGIN},
		{"positive", 
			RegexDefinition.C_ALO_ANYTHING_NONALNUM_END + "positive" + 
			RegexDefinition.C_ALO_ANYTHING_NONALNUM_BEGIN},
		// This is for use inside a for loop
		{"in", 
			RegexDefinition.C_ANYTHING_NONALNUM_END + "in" +
			RegexDefinition.C_ANYTHING_NONALNUM_BEGIN},
		{"else if", 
			RegexDefinition.C_ANYTHING_NONALNUM_END + "else\\s+if" +
			RegexDefinition.C_ALO_ANYTHING_NONALNUM_BEGIN},
		{"else", 
			RegexDefinition.C_ANYTHING_NONALNUM_END + "else" +
			RegexDefinition.C_ALO_ANYTHING_NONALNUM_BEGIN},
		{"loop up from to by",
			RegexDefinition.C_ANYTHING_NONALNUM_END + "loop\\s+(" + RegexDefinition.VARIABLE + ")\\s+up\\s+from\\s+(" + 
			RegexDefinition.ALO_ANYTHING + ")\\s+to\\s+(" + RegexDefinition.ALO_ANYTHING + ")\\s+by\\s+(" +
			RegexDefinition.ALO_ANYTHING + ")" + RegexDefinition.C_ANYTHING_NONALNUM_BEGIN},
		{"loop down from to by",
			RegexDefinition.C_ANYTHING_NONALNUM_END + "loop\\s+(" + RegexDefinition.VARIABLE + ")\\s+down\\s+from\\s+(" + 
			RegexDefinition.ALO_ANYTHING + ")\\s+to\\s+(" + RegexDefinition.ALO_ANYTHING + ")\\s+by\\s+(" +
			RegexDefinition.ALO_ANYTHING + ")" + RegexDefinition.C_ANYTHING_NONALNUM_BEGIN
		},
		{"loop up from to",
			RegexDefinition.C_ANYTHING_NONALNUM_END + "loop\\s+(" + RegexDefinition.VARIABLE + ")\\s+up\\s+from\\s+(" + 
			RegexDefinition.ALO_ANYTHING + ")\\s+to\\s+(" + RegexDefinition.ALO_ANYTHING + ")" + 
			RegexDefinition.C_ANYTHING_NONALNUM_BEGIN},
		{"loop down from to",
			RegexDefinition.C_ANYTHING_NONALNUM_END + "loop\\s+(" + RegexDefinition.VARIABLE + ")\\s+down\\s+from\\s+(" + 
			RegexDefinition.ALO_ANYTHING + ")\\s+to\\s+(" + RegexDefinition.ALO_ANYTHING + ")" + 
			RegexDefinition.C_ANYTHING_NONALNUM_BEGIN},
		{"loop to",
			RegexDefinition.C_ANYTHING_NONALNUM_END + "loop\\s+(" + RegexDefinition.VARIABLE + ")\\s+to\\s+(" + 
			RegexDefinition.ALO_ANYTHING + ")" + RegexDefinition.C_ANYTHING_NONALNUM_BEGIN},
		{"substring of from", 
			RegexDefinition.C_ALO_ANYTHING_NONALNUM_END + "substring\\s+of\\s+(" + 
			RegexDefinition.ALO_BRACKETS + "|" + RegexDefinition.VARIABLE + "|" + RegexDefinition.STRING + 
			"|" + RegexDefinition.FUNCTION + ")\\s+from\\s+(" + RegexDefinition.ALO_BRACKETS + "|" + 
			RegexDefinition.VARIABLE + "|" + RegexDefinition.INT + "|" + RegexDefinition.FUNCTION + ")" + 
			RegexDefinition.C_ANYTHING_NONALNUM_BEGIN},
		{"substring of from to", 
			RegexDefinition.C_ALO_ANYTHING_NONALNUM_END + "substring\\s+of\\s+(" + 
			RegexDefinition.ALO_BRACKETS + "|" + RegexDefinition.VARIABLE + "|" + RegexDefinition.STRING + 
			"|" + RegexDefinition.FUNCTION + ")\\s+from\\s+(" + RegexDefinition.ALO_BRACKETS + "|" + 
			RegexDefinition.VARIABLE + "|" + RegexDefinition.INT + "|" + RegexDefinition.FUNCTION + 
			")\\s+to\\s+(" + RegexDefinition.ALO_BRACKETS + "|" + RegexDefinition.VARIABLE + "|" + 
			RegexDefinition.INT + "|" + RegexDefinition.FUNCTION + ")" + 
			RegexDefinition.C_ANYTHING_NONALNUM_BEGIN},
		{"cast to", 
			RegexDefinition.C_ALO_ANYTHING_NONALNUM_END + "cast\\s+(" + RegexDefinition.ALO_BRACKETS + 
			"|" + RegexDefinition.VARIABLE + "|" + RegexDefinition.FUNCTION + ")\\s+to\\s+" + 
			RegexDefinition.C_ALL_TYPES + RegexDefinition.C_ANYTHING_NONALNUM_BEGIN}, 
		{"new array", 
			RegexDefinition.C_ALO_ANYTHING_NONALNUM_END + "new\\s+array\\s*" + 
			RegexDefinition.C_ARRAY_CONTENTS + RegexDefinition.C_ANYTHING_NONALNUM_BEGIN},
		{"remove last from", 
			RegexDefinition.C_ANYTHING_NONALNUM_END + "remove\\s+last\\s+from\\s+(" +
			RegexDefinition.ALO_BRACKETS + "|" + RegexDefinition.FUNCTION + "|" + RegexDefinition.VARIABLE + 
			")" + RegexDefinition.C_ANYTHING_NONALNUM_BEGIN},
		{"index of in",
			RegexDefinition.C_ALO_ANYTHING_NONALNUM_END + "index\\s+of\\s+(" +
			RegexDefinition.ALO_BRACKETS + "|" + RegexDefinition.VARIABLE + "|" + RegexDefinition.STRING + 
			"|" + RegexDefinition.FUNCTION + ")\\s+in\\s+(" + RegexDefinition.ALO_BRACKETS + "|" + 
			RegexDefinition.VARIABLE + "|" + RegexDefinition.STRING + "|" + RegexDefinition.FUNCTION + ")" +
			RegexDefinition.C_ANYTHING_NONALNUM_BEGIN},
		{"find in", 
			RegexDefinition.C_ALO_ANYTHING_NONALNUM_END + "find\\s+(" + RegexDefinition.ALO_BRACKETS + 
			"|" + RegexDefinition.VARIABLE + "|" + RegexDefinition.STRING + "|" +
			RegexDefinition.DECIMAL + "|" + RegexDefinition.FUNCTION + ")\\s+in\\s+(" + 
			RegexDefinition.ALO_BRACKETS + "|" + RegexDefinition.VARIABLE + "|" + RegexDefinition.FUNCTION + 
			")" + RegexDefinition.C_ANYTHING_NONALNUM_BEGIN},
		{"greater than", 
			RegexDefinition.C_ALO_ANYTHING_NONALNUM_END + "greater\\s+than" +
			RegexDefinition.C_ANYTHING_NONALNUM_BEGIN},
		{"greater than or equal to", 
			RegexDefinition.C_ALO_ANYTHING_NONALNUM_END + "greater\\s+than\\s+or\\s+equal\\s+to" +
			RegexDefinition.C_ANYTHING_NONALNUM_BEGIN},
		{"less than", 
			RegexDefinition.C_ALO_ANYTHING_NONALNUM_END + "less\\s+than" +
			RegexDefinition.C_ANYTHING_NONALNUM_BEGIN},
		{"less than or equal to", 
			RegexDefinition.C_ALO_ANYTHING_NONALNUM_END + "less\\s+than\\s+or\\s+equal\\s+to" +
			RegexDefinition.C_ANYTHING_NONALNUM_BEGIN}, 
		{"bitwise or", 
			RegexDefinition.C_ALO_ANYTHING_NONALNUM_END + "bitwise\\s+or" +
			RegexDefinition.C_ANYTHING_NONALNUM_BEGIN},
		{"bitwise and", 
			RegexDefinition.C_ALO_ANYTHING_NONALNUM_END + "bitwise\\s+and" +
			RegexDefinition.C_ANYTHING_NONALNUM_BEGIN},
		{"bitwise xor", 
			RegexDefinition.C_ALO_ANYTHING_NONALNUM_END + "bitwise\\s+xor" +
			RegexDefinition.C_ANYTHING_NONALNUM_BEGIN},
		{"bitwise not", 
			RegexDefinition.C_ANYTHING_NONALNUM_END + "bitwise\\s+not" +
			RegexDefinition.C_ANYTHING_NONALNUM_BEGIN}, 
		{"shift left by", 
			RegexDefinition.C_ANYTHING_NONALNUM_END + "shift\\s+(" + RegexDefinition.ALO_BRACKETS + "|" 
			+ RegexDefinition.ALO_ANYTHING + "|" + ")\\s+left\\s+by\\s+(" + RegexDefinition.ALO_BRACKETS + 
			"|" + RegexDefinition.VARIABLE + "|" + RegexDefinition.INT + "|" + RegexDefinition.FUNCTION + 
			")" + RegexDefinition.C_ANYTHING_NONALNUM_BEGIN},
		{"shift right by", 
			RegexDefinition.C_ANYTHING_NONALNUM_END + "shift\\s+(" + RegexDefinition.ALO_BRACKETS + "|" 
			+ RegexDefinition.ALO_ANYTHING + ")\\s+right\\s+by\\s+(" + RegexDefinition.ALO_BRACKETS + "|" +
			RegexDefinition.VARIABLE + "|" + RegexDefinition.INT + "|" + RegexDefinition.FUNCTION + ")" + 
			RegexDefinition.C_ANYTHING_NONALNUM_BEGIN}, 
		{"empty array", 
			RegexDefinition.C_ALO_ANYTHING_NONALNUM_END + "empty\\s+array" +
			RegexDefinition.C_ANYTHING_NONALNUM_BEGIN},
		{"empty string", 
			RegexDefinition.C_ALO_ANYTHING_NONALNUM_END + "empty\\s+string" +
			RegexDefinition.C_ANYTHING_NONALNUM_BEGIN}, 
		{"split",
			RegexDefinition.C_ALO_ANYTHING_NONALNUM_END + "split\\s+(" + RegexDefinition.ALO_BRACKETS + 
			"|" + RegexDefinition.VARIABLE + "|" + RegexDefinition.STRING + "|" + RegexDefinition.FUNCTION + 
			")" + RegexDefinition.C_ANYTHING_NONALNUM_BEGIN},
		{"split by",
			RegexDefinition.C_ALO_ANYTHING_NONALNUM_END + "split\\s+(" + RegexDefinition.ALO_BRACKETS + 
			"|" + RegexDefinition.VARIABLE + "|" + RegexDefinition.STRING + "|" + RegexDefinition.FUNCTION + 
			")\\s+by\\s+(" + RegexDefinition.ALO_BRACKETS + "|" + RegexDefinition.VARIABLE + "|" + 
			RegexDefinition.STRING + "|" + RegexDefinition.FUNCTION + ")" + 
			RegexDefinition.C_ANYTHING_NONALNUM_BEGIN},
		{"size of", 
			RegexDefinition.C_ALO_ANYTHING_NONALNUM_END + "size\\s+of\\s+(" + RegexDefinition.ALO_BRACKETS + 
			"|" + RegexDefinition.VARIABLE + "|" + RegexDefinition.STRING + "|" + RegexDefinition.FUNCTION + 
			")" + RegexDefinition.C_ANYTHING_NONALNUM_BEGIN}, 
		{"length of", 
			RegexDefinition.C_ALO_ANYTHING_NONALNUM_END + "length\\s+of\\s+(" + RegexDefinition.ALO_BRACKETS 
			+ "|" + RegexDefinition.VARIABLE + "|" + RegexDefinition.FUNCTION + ")" + 
			RegexDefinition.C_ANYTHING_NONALNUM_BEGIN}, 
		{"insert in", 
			RegexDefinition.C_ANYTHING_NONALNUM_END + "insert\\s+(" + RegexDefinition.ALO_BRACKETS + "|" + 
			RegexDefinition.ALO_ANYTHING + ")\\s+in\\s+(" + RegexDefinition.ALO_BRACKETS + "|" + 
			RegexDefinition.VARIABLE + "|" + RegexDefinition.STRING + "|" + RegexDefinition.FUNCTION + ")" + 
			RegexDefinition.C_ANYTHING_NONALNUM_BEGIN},
		{"insert in at", 
			RegexDefinition.C_ANYTHING_NONALNUM_END + "insert\\s+(" + RegexDefinition.ALO_BRACKETS + "|" 
			+ RegexDefinition.ALO_ANYTHING + ")\\s+in\\s+(" + RegexDefinition.ALO_BRACKETS + "|" + 
			RegexDefinition.VARIABLE + "|" + RegexDefinition.STRING + "|" + RegexDefinition.FUNCTION + 
			")\\s+at\\s+(" + RegexDefinition.ALO_BRACKETS + "|" + RegexDefinition.VARIABLE + "|" + 
			RegexDefinition.INT + "|" + RegexDefinition.FUNCTION + ")" + 
			RegexDefinition.C_ANYTHING_NONALNUM_BEGIN}, 
		{"absolute value of",
			RegexDefinition.C_ALO_ANYTHING_NONALNUM_END + "absolute\\s+value\\s+of\\s+(" +
			RegexDefinition.ALO_BRACKETS + "|" + RegexDefinition.VARIABLE + "|" + RegexDefinition.DECIMAL + 
			"|" + RegexDefinition.FUNCTION + ")" + RegexDefinition.C_ANYTHING_NONALNUM_BEGIN}, 
		{"increment by", 
			RegexDefinition.C_ANYTHING_NONALNUM_END + "increment\\s+(" + RegexDefinition.VARIABLE + 
			")\\s+by\\s+(" + RegexDefinition.ALO_BRACKETS + "|" + RegexDefinition.VARIABLE + "|" + 
			RegexDefinition.DECIMAL + "|" + RegexDefinition.FUNCTION + ")" + 
			RegexDefinition.C_ANYTHING_NONALNUM_BEGIN},
		{"increment", 
			RegexDefinition.C_ANYTHING_NONALNUM_END + "increment\\s+(" + RegexDefinition.VARIABLE + 
			")" + RegexDefinition.C_ANYTHING_NONALNUM_BEGIN},
		{"decrement by", 
			RegexDefinition.C_ANYTHING_NONALNUM_END + "decrement\\s+(" + RegexDefinition.VARIABLE + 
			")\\s+by\\s+(" + RegexDefinition.ALO_BRACKETS + "|" + RegexDefinition.VARIABLE + "|" + 
			RegexDefinition.DECIMAL + "|" + RegexDefinition.FUNCTION + ")" + 
			RegexDefinition.C_ANYTHING_NONALNUM_BEGIN},
		{"decrement", 
			RegexDefinition.C_ANYTHING_NONALNUM_END + "decrement\\s+(" + RegexDefinition.VARIABLE + 
			")" + RegexDefinition.C_ANYTHING_NONALNUM_BEGIN}, 
		{"element at from",
			RegexDefinition.C_ALO_ANYTHING_NONALNUM_END + "element\\s+at\\s+(" + RegexDefinition.ALO_BRACKETS 
			+ "|" + RegexDefinition.ALO_ANYTHING + ")\\s+from\\s+(" + RegexDefinition.ALO_BRACKETS + "|" + 
			RegexDefinition.VARIABLE + "|" + RegexDefinition.FUNCTION + ")" + 
			RegexDefinition.C_ANYTHING_NONALNUM_BEGIN}, 
		{"character at from",
			RegexDefinition.C_ALO_ANYTHING_NONALNUM_END + "character\\s+at\\s+(" + 
			RegexDefinition.ALO_BRACKETS + "|" + RegexDefinition.ALO_ANYTHING + ")\\s+from\\s+(" + 
			RegexDefinition.ALO_BRACKETS + "|" + RegexDefinition.VARIABLE + "|" + RegexDefinition.STRING + 
			"|" + RegexDefinition.FUNCTION + ")" + RegexDefinition.C_ANYTHING_NONALNUM_BEGIN},
		{"declare as", 
			RegexDefinition.C_ANYTHING_NONALNUM_END + "declare\\s+(" + RegexDefinition.ALO_BRACKETS + 
			"|" + RegexDefinition.VARIABLE + ")\\s+as" + RegexDefinition.C_ALO_ANYTHING_NONALNUM_BEGIN}, 
		{"set to", 
			RegexDefinition.C_ANYTHING_NONALNUM_END + "set\\s+(" + RegexDefinition.ALO_BRACKETS + 
			"|" + RegexDefinition.VARIABLE + ")\\s+to" + RegexDefinition.C_ALO_ANYTHING_NONALNUM_BEGIN},
		
		// Force a strict usage of array literals: Should be declared in it's own line
		{"{ }",
			RegexDefinition.C_ANYTHING_NONALNUM_END + "[{]" + RegexDefinition.C_ANYTHING + "[}]" + 
			RegexDefinition.C_ANYTHING_NONALNUM_BEGIN}
	};
	
	public static final Map<String, Pattern> suffixes = suffixes();
	
	public static final Map<String, Pattern> substitutions = substitutions();
	
	public static String substitute(String input) {
		String untouched = input, output = null;
		while (Substitute.capture(keywords, suffixes, input) != null) {
			Tuple<String, String[]> tup = Substitute.extract(keywords, suffixes, substitutions, input);
			// TODO: Throw Error Here: Keyword used as variable
			String temp = (tup != null) ? Services.strip(Substitute.replace("core", tup.first, tup.second)) : null;
			if (temp != null) 
				output = temp;
			
			input = temp;
		}
		
		return (output == null) ? untouched : output;
	}
	
	private static Map<String, Pattern> substitutions() {
		Map<String, Pattern> subs = new HashMap<String, Pattern>();
		for (String[] components : argument_data) {
			// First Component is the key
			String key = components[0];
			
			// Extraction Pattern
			final Pattern extract = Pattern.compile(components[1]);

			// Add Everything to the substitution hashMap
			subs.put(key, extract);
		}
		return subs;
	}
	
	private static Map<String, Pattern> suffixes() {
		Map<String, Pattern> sufs = new HashMap<String, Pattern>();
		for (String[] components : keyphrase_data) {
			// First Component Is The Key
			String key = components[0];
			
			// Extraction Pattern
			final Pattern extract = (components[1] != null) ? 
					Pattern.compile(components[1]) : null;
			
			// Add Everything To Suffix Table
			sufs.put(key, extract);
		}
		return sufs;
	}

}
