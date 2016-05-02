package com.pkoding.universal.util;

import java.util.*;
import java.util.regex.*;

public class StatementToRegexParser {
	
	private static final Pattern non_keyword = Pattern.compile(RegexDefinition.RP_NON_KEYWORD);
	private static final Pattern keyword = Pattern.compile(RegexDefinition.C_RP_KEYWORD);
	private static final Pattern optional = Pattern.compile(RegexDefinition.RP_OPTIONAL);
	private static final Pattern hashtag = Pattern.compile(RegexDefinition.RP_HASHTAG);
	private static final Pattern args = Pattern.compile(RegexDefinition.RP_ARGUMENT);
	private static final Pattern combined = Pattern.compile(
		"(integer|anything|number|decimal|real|string|int|float|comparison_op)"
	);
	
	// TODO: Error Handling ==> key-phrase capture error/exception
	public static Tuple<String, String> keyphrase(String statement) {
		ArrayList<String> phrase = new ArrayList<String>();
		Matcher matcher = keyword.matcher(statement);
		
		while (matcher.find()) {
			phrase.add(matcher.group(2));
			matcher = keyword.matcher(matcher.replaceFirst(matcher.group(1) + " " + matcher.group(3)));
		}
		
		return new Tuple<String, String>(phrase.get(0).trim(), Services.asString(phrase));
	}
	
	public static String phraseRegex(String statement) {
		// Surround keywords with parentheses
		Matcher matcher = keyword.matcher(statement.trim());
		while (matcher.find()) {
			statement = matcher.replaceFirst(matcher.group(1) + "(" + 
					Services.escapeMetaCharacters(matcher.group(2).trim(), true) + ")" + matcher.group(3));
			matcher = keyword.matcher(statement);
		}
		
		// Replace non-key-phrases and with the infamous #tag
		matcher = non_keyword.matcher(statement);
		while (matcher.find()) {
			statement = matcher.replaceFirst("#");
			matcher = non_keyword.matcher(statement);
		}
		
		matcher = hashtag.matcher(statement);
		while (matcher.find()) {
			statement = matcher.replaceFirst(RegexDefinition.NC_ANYTHING);
			matcher = hashtag.matcher(statement);
		}
		
		// Replace all white spaces with \\s+
		statement = Services.strip(statement).replaceAll("\\s+", "\\\\s+");
		
		// Start + Ending Regex to allow use in other functions or statements, then return
		return RegexDefinition.NC_BEGIN + statement + RegexDefinition.NC_END;
	}
	
	// TODO: Error Handling ==> Regex Parse Error
	public static Pattern statementRegex(String statement, String library) {
		// Escape special characters before we add our own
		statement = Services.escapeMetaCharacters(statement, false);
		
		// Replace optional key-phrases
		Matcher matcher = optional.matcher(statement);
		while (matcher.find()) {
			statement = matcher.replaceFirst("(?:" + Services.escapeMetaCharacters(matcher.group(1), false) + ")?");
			matcher = optional.matcher(statement);
		}
		
		// Strip ends, replace white spaces with \\s+
		statement = Services.strip(statement).replaceAll("\\s+", "\\\\s+");

		// Replace Arguments
		matcher = args.matcher(statement);
		ArrayList<Tuple<String, String>> replacements = new ArrayList<Tuple<String, String>>();
			
		while (matcher.find())
			replacements.add(new Tuple<String, String>(matcher.group(0), argReplace(matcher.group(1))));
			
		// TODO: This is really inefficient, find replacement
		for (Tuple<String, String> replacement : replacements) {
			matcher = Pattern.compile(replacement.first, Pattern.LITERAL).matcher(statement);
			if (matcher.find())
				statement = matcher.replaceFirst(replacement.second);
		}
			
		// Add library prefix to statement regex
		statement = ("".equals(library)) ? statement : library + "\\s+" + statement;
	
		// Start + Ending Regex to allow use in other functions or statements, then return
		return Pattern.compile(RegexDefinition.C_ANYTHING_NONALNUM_END + statement + 
			RegexDefinition.C_ANYTHING_NONALNUM_BEGIN);
	}
	
	private static String argReplace(String str) {
		ArrayList<String> matches = new ArrayList<String>();
		matches.add("(" + RegexDefinition.ALO_BRACKETS + "|" + RegexDefinition.VARIABLE + "|" + RegexDefinition.FUNCTION);
		Matcher matcher = combined.matcher(str);

		while (matcher.find()) {
			// Replace <string>
			if (matcher.group().equals("string"))
				matches.add(RegexDefinition.STRING);

			// Replace <anything>
			else if (matcher.group().equals("anything"))
				matches.add(RegexDefinition.ALO_ANYTHING);

			// Replace <number>
			else if (matcher.group().equals("number"))
				matches.add(RegexDefinition.NUMBER);

			// Replace <integer>
			else if (matcher.group().equals("integer"))
				matches.add(RegexDefinition.INT);
				
			else if (matcher.group().equals("int"))
				matches.add(RegexDefinition.INT);

			// Replace <decimal|real>
			else if (matcher.group().equals("decimal") || matcher.group().equals("real"))
				matches.add(RegexDefinition.DECIMAL);
				
			// Replace <float>
			else if (matcher.group().equals("float"))
				matches.add(RegexDefinition.FLOAT);
			
			else if (matcher.group().equals("comparison_op"))
				matches.add(RegexDefinition.COMPARISON_OP);
			
			else if (matcher.group().equals("variable"))
				matches.add("[\\$]?[a-zA-Z_][a-zA-Z0-9_]*");
		}

		return Services.join(matches, "|") + ")";
	}

}
