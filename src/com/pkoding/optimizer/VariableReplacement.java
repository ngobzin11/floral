package com.pkoding.optimizer;

import java.util.*;
import java.util.Map.*;

import com.pkoding.recognizer.lexer.*;
import com.pkoding.universal.util.Tuple;

public class VariableReplacement {
	
	public static ArrayList<ArrayList<Token>> execute(HashMap<String, Tuple<Integer, ArrayList<Token>>> token_map, ArrayList<ArrayList<Token>> output_code) {
		HashSet<String> replace_queue = replaceQueue(token_map);
		if (replace_queue.size() == 0)
			return output_code;
		
		while (true) {
			ArrayList<ArrayList<Token>> new_code = new ArrayList<ArrayList<Token>>();
			HashSet<String> replaced = new HashSet<String>();
			for (int i = output_code.size() - 1; i >= 0; i--) {
				ArrayList<Token> line = new ArrayList<Token>();
				boolean ignore = false, tag_declare = false;
				for (Token token : output_code.get(i)) {
					// Tag Declare
					if (token.type == TokenType.VAR_DECL) {
						line.add(token);
						tag_declare = true;
					
					// Close t
					} else if ((tag_declare) && (token.type == TokenType.CL_PAREN)) {
						line.add(token);
						tag_declare = false;
					
					} else if (token.type == TokenType.VARIABLE) {
						String key = token.scope().name + "_" + token.name;
						boolean in_queue = replace_queue.contains(key);
						
						// The declaration of a variable that will be replaced: continue to the next line
						if (tag_declare && in_queue) {
							ignore = true;
							continue;
						
						// Using the variable that will be replaced
						} else if (in_queue) {
							replaced.add(key);
							
							// Replace the variable with the tokens from the token map: Enclose with parentheses
							//		TODO: This assumes there is a value being replaced: Catch the error earlier so there won't be a need to here
							line.add(new Token(TokenType.OP_PAREN, "("));
							for (Token t : token_map.get(key).second)
								line.add(t);
							
							line.add(new Token(TokenType.CL_PAREN, ")"));
							
						// This is a useful variable, keep it
						} else {
							line.add(token);
						}
						
					} else {
						line.add(token);
					}
					
				}
				if (!ignore)
					new_code.add(line);
				
			}
			output_code = reverse(new_code);
			
			if (replaced.size() == 0)
				return output_code;
		}
	}
	
	private static ArrayList<ArrayList<Token>> reverse(ArrayList<ArrayList<Token>> code) {
		ArrayList<ArrayList<Token>> reversed = new ArrayList<ArrayList<Token>>();
		for (int i = code.size() - 1; i >= 0; i--)
			reversed.add(code.get(i));
		
		return reversed;
	}
	
	/**
	 * Gets the variables that have been declared and then used once. 
	 * 	It ignores variables that are declared as arguments. These are 
	 * 	variables that have null as their value
	 * 
	 * @return queue - A HashSet of the scope's variables that need to be removed
	 */
	private static HashSet<String> replaceQueue(HashMap<String, Tuple<Integer, ArrayList<Token>>> token_map) {
		HashSet<String> log = new HashSet<String>();
		for (Entry<String, Tuple<Integer, ArrayList<Token>>> entry : token_map.entrySet())
			if ((entry.getValue().first == 1) && (entry.getValue().second != null))
				log.add(entry.getKey());
		
		return log;
	}

}
