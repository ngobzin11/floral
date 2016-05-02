package com.pkoding.universal.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

/**
 * A class that has utility functions that will be used by the rest of the compiler classes
 * 
 * @author ngobzin11
 * @version 1
 */
public class Services {
	
	private static final HashSet<Character> metachars = metachars();
	
	/**
	 * Indents a given line using depth tabs
	 * 
	 * @param line		The line being indented
	 * @param depth		The number of tabs to be used to indent the line
	 * @return	The line prepended with depth tabs
	 */
	public static String indent(String line, int depth) {
		String result = "";
		for (int i = 0; i < depth; i++)
			result += "\t";
		
		return result + line;
	}
	
	/**
	 * Checks if a given string is a phrase or not. A string is deemed a
	 * phrase if it has white space separating words.
	 * 
	 * @param str	The string being tested
	 * @return	<code>True</code> if the string is a phrase, <code>false</code> otherwise
	 */
	public static boolean isPhrase(String str) {
		return strip(str).split(RegexDefinition.WHITE_SPACE).length > 1;
	}
	
	/**
	 * Strips outer white space from a given string
	 * 
	 * @param sentence		The string being stripped of white space
	 * @return	A string without white space before and after its non-space characters
	 */
	public static String strip(String sentence) {
		return ((sentence != null) && (!sentence.equals(""))) ? 
				Regex.OUTER_WHITE_SPACE.matcher(sentence).replaceAll("") : sentence;
	}
	
	/**
	 * Replaces all extra white space from a string with just one space
	 * 
	 * @param sentence		The string being cleaned of extra white spaces
	 * @return	A string with no extra white space 
	 */
	public static String tidy(String sentence) {
		return Regex.WHITE_SPACE.matcher(sentence).replaceAll(" ");
	}
	
	/**
	 * Cleans up a string by removing special characters, extra white space 
	 * and finally stripping the string of outer white space.
	 *  
	 * @param str	The string being cleaned
	 * @return	A cleaned string
	 */
	public static String clean(String str) {
		// Remove special characters
		String w_spaces = Regex.SPECIAL_CHARS.matcher(str).replaceAll("");
		
		// Remove extra spaces + tabs and return
		w_spaces = Regex.WHITE_SPACE.matcher(w_spaces).replaceAll(" ");
		
		// Remove spaces at the beginning and end of sentence
		return Regex.OUTER_WHITE_SPACE.matcher(w_spaces).replaceAll("");
	}
	
	/**
	 * Returns the contents of a given collection as a space separated string
	 * 
	 * @param collection	The collection whose items are to be made into a string
	 * @return	A string with the collection's contents separated by a space 
	 */
	public static <T> String asString(Collection<T> collection) {
		return join(collection, " ");
	}
	
	/**
	 * Returns the contents of a given array as a space separated string
	 * 
	 * @param array		The array whose items are to be made into a string
	 * @return	A string with the array's contents separated by a space
	 */
	public static <T> String asString(T[] array) {
		return join(array, " ");
	}
	
	/**
	 * Returns the contents of a given collection as a comma separated string
	 * 
	 * @param collection	The collection whose items are to be made in to a string
	 * @return	A string with the collection's contents separated by a comma
	 */
	public static <T> String commaSeparatedString(Collection<T> collection) {
		return join(collection, ",");
	}
	
	/**
	 * Returns the contents of a given array as a comma separated string
	 * 
	 * @param array		The array whose items are to be made in to a string
	 * @return	A string with the array's contents separated by a comma
	 */
	public static <T> String commaSeparatedString(T[] array) {
		return join(array, ",");
	}
	
	/**
	 * Joins the contents of a given collection by a separator
	 * 
	 * @param collection	The collection whose items are to be joined
	 * @param separator		The separator used to join the contents
	 * @return	<code>Null</code> if the <code>collection/separator</code> is <code>null</code>, the separated string otherwise 
	 */
	public static <T> String join(Collection<T> collection, String separator) {
		if (collection == null || separator == null)
			return null;
		
		int sz = collection.size();
		if (sz == 0)
			return "";
		
		String str = "";
		for (T item : collection) 
			str += (--sz > 0) ? strip((String) item) + separator : strip((String) item);
		
		return str;
	}
	
	/**
	 * Joins the contents of a given collection by a separator
	 * 
	 * @param array		The array whose items are to be joined
	 * @param separator		The separator used to join the contents
	 * @return	<code>Null</code> if the <code>array/separator</code> is <code>null</code>, the separated string otherwise 
	 */
	public static <T> String join(T[] array, String separator) {
		if (array == null || separator == null) 
			return null;
		
		int sz = array.length;
		if (sz == 0)
			return "";
		
		String str = "";
		for (T item : array)
			str += (--sz > 0) ? strip((String) item) + separator : strip((String) item);
		
		return str;
	}
	
	/**
	 * An enhanced splitting function that splits a function by a given regular expression 
	 * 	and checks that opening and 
	 * 
	 * @param line
	 * @param regex
	 * @return
	 */
	public static ArrayList<String> enhancedSplit(String line, String regex, String glue) {
		line = line.trim();
		
		// Ignore empty lines
		if ("".equals(line))
			return new ArrayList<String>();
		
		ArrayList<String> res = new ArrayList<String>();
		// Split the line by spaces
		String[] arr = line.split(regex);
		
		// One function lines, e.g. __END__
		if (arr.length == 1) {
			res.add(arr[0]);
			
		// The more complicated part of life
		} else {
			String running = "";
			int paren = 0, brac = 0;
			for (int i = 0; i < arr.length; i++) {
				String temp = arr[i];
				Tuple<Integer, Integer> t = isBalanced(temp.toCharArray());
				paren += t.first;
				brac += t.second;
				
				// If it is balanced then don't do anything else
				if ((paren == 0) && (brac == 0)) {
					res.add(running + temp);
					running = "";
					
				// Otherwise add it to the running string until it balances
				} else {
					running += temp + glue;
				}
			}
		}
		
		return res;
	}
	
	/**
	 * Adds the contents of an array to an ArrayList
	 * 
	 * @param array		The array whose contents are to be added to an ArrayList
	 * @return	The ArrayList containing the contents of the array  
	 */
	public static <T> ArrayList<T> toArrayList(T[] array) {
		ArrayList<T> lst = new ArrayList<T>();
		for (T item : array)
			lst.add(item);
		
		return lst;
	}
	
	public static boolean isOperation(String val) {
		return Regex.OPERATIONS.matcher(val).matches();
	}
	
	public static boolean isComment(String line) {
		return Regex.LINE_COMMENT.matcher(line).matches();
	}
	
	public static String stringMultiply(String str, int multiple) {
		String res = "";
		for (int i = 0; i < multiple; i++)
			res += str;
		
		return res;
	}
	
	public static String regexEscape(String str) {
		if (str == null)
			return null;
		
		String out = "";
		for (char c : str.toCharArray()) {
			if (c == '+' || c == '*')
				out += "\\" + c;
			else 
				out += c;
		}
		
		return out;
	}
	
	/**
	 * Checks if a stream of characters has a balanced number of parentheses/brackets
	 * 
	 * @param stream - The stream of characters being checked
	 * @return true - If the statement is balanced, false otherwise
	 */
	private static Tuple<Integer, Integer> isBalanced(char[] stream) {
		int paren = 0, brac = 0;
		for (int i = 0; i < stream.length; i++) {
			if (stream[i] == '(')
				paren++;
			
			else if (stream[i] == ')')
				paren--;
			
			else if (stream[i] == '[')
				brac++;
			
			else if (stream[i] == ']')
				brac--;
		}
		
		return new Tuple<Integer, Integer>(paren, brac);
	}
	
	public static String escapeMetaCharacters(String keyword, boolean desc) {
		String result = "";
		for (char c : keyword.toCharArray())
			result += metachars.contains(c) ? (desc ? "\\\\" : "\\") + c + "" : c ;
		
		return result;
	}
	
	private static HashSet<Character> metachars() {
		// Regex meta-characters: Ignores <>[]
		char[] meta = {'(', '{', '\\', '^', '-', '=', '$', '!', '|', '}', ')', '?', '*', '+', '.'};
		HashSet<Character> set = new HashSet<Character>();
		for (int i = 0; i < meta.length; i++)
			set.add(meta[i]);
		
		return set;
	}

}
