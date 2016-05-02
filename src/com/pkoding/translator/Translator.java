package com.pkoding.translator;

import java.util.regex.*;
import java.util.*;

import com.pkoding.recognizer.lexer.*;
import com.pkoding.universal.util.*;
import com.pkoding.universal.data.*;

public class Translator {
	
	private final Pattern pk_func_fmt = Pattern.compile(RegexDefinition.C_PK_FUNC_FMT);
	private final Pattern nm_func_fmt = Pattern.compile(RegexDefinition.C_NM_FUNC_FMT);
	private final Pattern str_ptn = Pattern.compile(RegexDefinition.C_AUTO_STRING);
	
	private final HashMap<String, Tuple<Pattern, String>> constructs;
	private ArrayList<Tuple<Integer, String>> output_code;
	private final HashMap<String, String> substitutions;
	private final String line_end_marker;
	private final String var_prefix;
	private final Lexer lexer;
	
	public Translator(Lexer lexer, ArrayList<String> libraries) {
		LanguageLoader lparser = new LanguageLoader(libraries);
		output_code = new ArrayList<Tuple<Integer, String>>();
		line_end_marker = PreprocessData.lineEndMarker();
		var_prefix = PreprocessData.variablePrefix();
		substitutions = lparser.substitutions();
		constructs = lparser.constructs();
		this.lexer = lexer;
		int depth = 0;
		
		boolean statement = true, mid = false, end = false;
		String running = "", construct = "";
		lexer.reset();
		Token token = lexer.nextToken();
		while ((token != null) && (token.type != TokenType.EOF)) {
			switch (token.type) {
				case LOOP: case FUNCTION_NAME:/* case CLASS_NAME:*/ case ARRAY_ELEMENT: case OP_PAREN: case VAR_DECL: case VAR_UPDATE:
					TokenTree root = nextFunction(null, (token.type == TokenType.OP_PAREN) ? null : token);
					System.out.println("Root:\t\t" + root);
					markDepth(root, 0);
					running += substitute(root) + " ";
					System.out.println("Running:\t\t" + running);
					if (token.type == TokenType.LOOP)
						statement = false;
					else if (token.name.equals("__CAST_TO__")) 
						construct += token.name + " ";
					break;
				case VARIABLE:
					running += var_prefix + token.name + " ";
					break;
				case ELSE_IF: case ELSE: case CATCH: case FINALLY:
					construct += token.name + " ";
					running += token.name + " ";
					mid = true; statement = false;
					break;
				case FOR: case IF: case TRY: case WHILE: case SWITCH: case FUNCTION_DECL: case CLASS_DECL:
					construct += token.name + " ";
					running += token.name + " ";
					statement = false;
					break;
				case END:
					running += token.name + " ";
					end = true;
					break;
				case DO: case IN: case THEN:
					running += (token.type == TokenType.IN) ? " " + token.name + " " : token.name;
					construct += token.name + " ";
					break;
				case STRING:
					running += replaceStrings(token.name) + " ";
					break;
				case NEWLINE:
					System.out.println("Entering Newline");
					if (end) output_code.add(new Tuple<Integer, String>(--depth, replace("__END__", "__END__")));
					else if (statement) output_code.add(new Tuple<Integer, String>(depth, running.trim() + line_end_marker));
					else output_code.add(new Tuple<Integer, String>(mid ? depth - 1 : depth++, replace(construct.trim(), running.trim())));
					
					System.out.println(output_code);
					statement = true; mid = end = false;
					running = construct = "";
					break;
				default:
					running += ((substitutions.containsKey(token.name)) ? 
						String.format(substitutions.get(token.name), new Object[0]) : token.name) + " ";
					if (lexer.BinaryOperator(token.type))
						running = " " + running;
					
					break;
			}
			token = lexer.nextToken();
		}
	}
	
	public void print() {
		for (Tuple<Integer, String> t : output_code)
			System.out.println(Services.indent(t.second, t.first));
	}
	
	public ArrayList<Tuple<Integer, String>> outputCode() {
		return output_code;
	}
	
	public ArrayList<String> indentedCode() {
		ArrayList<String> indented = new ArrayList<String>();
		for (Tuple<Integer, String> t : output_code)
			indented.add(Services.stringMultiply("\t", t.first) + t.second);
		
		return indented;
	}
	
	private TokenTree nextFunction(TokenTree parent, Token start) {
		TokenTree node = new TokenTree(parent, (start != null) ? start.name : "");
		// Throw away the opening brace if this is an actual function
		if (start != null) lexer.nextToken();
		
		int braces = 1;
		String running = "";
		while (braces != 0) {
			Token tk = lexer.nextToken();
			TokenTree child = null;
			switch (tk.type) {
				case CL_PAREN:
					braces--;
					child = new TokenTree(node, running.trim());
					node.num_children++;
					if (node.first_child == null) {
						node.first_child = child;
					} else {
						TokenTree temp = node.first_child;
						while (temp.next_sibling != null)
							temp = temp.next_sibling;
						
						temp.next_sibling = child;
					}
					running = "";		// This is unnecessary, but... 
					break;
				case FUNCTION_NAME:/* case CLASS_NAME:*/ case ARRAY_ELEMENT: case VAR_DECL: case VAR_UPDATE: case OP_PAREN:
					child = nextFunction(node, (tk.type == TokenType.OP_PAREN) ? null : tk);
					node.num_children++;
					if (node.first_child == null) {
						node.first_child = child;
					} else {
						TokenTree temp = node.first_child;
						while (temp.next_sibling != null)
							temp = temp.next_sibling;
						
						temp.next_sibling = child;
					}
					break;
				case COMMA:
					child = new TokenTree(node, running.trim());
					node.num_children++;
					if (node.first_child == null) {
						node.first_child = child;
					} else {
						TokenTree temp = node.first_child;
						while (temp.next_sibling != null)
							temp = temp.next_sibling;
						
						temp.next_sibling = child;
					}
					running = "";
					break;
				case VARIABLE:
					running += var_prefix + tk.name;
					break;
				case STRING:
					running += replaceStrings(tk.name) + " ";
					break;
				default:
					// substitute if necessary, otherwise just add it like it is 
					running += ((substitutions.containsKey(tk.name))? 
						String.format(substitutions.get(tk.name), new Object[0]) : tk.name);
					if (lexer.BinaryOperator(tk.type))
						running = " " + running + " ";
					
					break;
			}
		}
		
		return node;
	}
	
	private void markDepth(TokenTree node, int depth) {
		// If the parent is null just return
		if (node == null)
			return;
		
		// Go all the way to the left-most leaf node
		//		Every node has already been visited, so just invert the visited check
		if ((node.first_child != null) && (node.first_child.is_visited))
			markDepth(node.first_child, depth);
		
		// Only write once and not as the recursion exits
		if (node.is_visited)
			node.subtree_depth = depth;
		
		// Everything is already visited from creating the tree, so reverse this here
		node.is_visited = false;
		
		// Go to the next sibling and find its left most child
		//		Second check is also useless, but let it be for now...
		if ((node.next_sibling != null) && (node.next_sibling.is_visited))
			markDepth(node.next_sibling, 0);
		
		// Go to the parent and mark its depth
		else
			markDepth(node.parent, ++depth);
		
		return;
	}
	
	private String substitute(TokenTree node) {
		TokenTree temp = node;
		// If the node has siblings, find the sibling with the biggest subtree depth
		while (temp.next_sibling != null) {
			if (temp.next_sibling.subtree_depth > node.subtree_depth)
				node = temp.next_sibling;
			
			temp = temp.next_sibling;
		}
		
		
		// Go down to the next level
		if (node.first_child != null)
			return substitute(node.first_child);
			
		// On the deepest leaf node: subtree size = 0 
		else {
			// System.out.println(node + "\t|\t" + node.parent + "\t|\t" + (node.parent != null ? node.parent.num_children : 0));
			// If this is the root node: add it like it is
			if (node.parent == null) {
				if (substitutions.containsKey(node.value))
					node.value = String.format(substitutions.get(node.value), new Object[0]);
				
				return node.value;
				
			} else {
				// Add all the values to a string array and use this for string formating
				String[] args = new String[node.parent.num_children];
				int count = 0;
				
				// Point back to the first child: This is not necessary coz it's always going to be the first child here???
				node = node.parent.first_child;
				args[count++] = node.value;
				while (node.next_sibling != null) {
					args[count++] = node.next_sibling.value;
					node = node.next_sibling;
				}
				// Point back to the parent: unlink the children from the parent and update the parent's depth
				node.parent.num_children = 0;
				node.parent.subtree_depth = 0;
				node.parent.first_child = null;
				
				// Multi-dimensional Array
				if (node.parent.value.equals("__ELEMENT_AT_FROM__") && (args.length > 2))
					node.parent.value = Constructs.mdArray(args);
				
				else if ((node.parent.value.equals("__LOOP_DOWN_FROM_TO_BY__") || (node.parent.value.equals("__LOOP_UP_FROM_TO_BY__")) 
						&& (args.length == 4)))
					
					node.parent.value = Constructs.loop(args[0], args[1], args[2], args[3], 
							node.parent.value.equals("__LOOP_UP_FROM_TO_BY__"));
					
				// Language Substitution Here:
				//		If the function name is known: replace it with the language substitution
				else if (substitutions.containsKey(node.parent.value)) {
					// If it's an array: just combine the arguments into one
					node.parent.value = (node.parent.value.equals("__ARRAY__")) ?
						String.format(substitutions.get(node.parent.value), Services.join(args, ", ")) :
							String.format(substitutions.get(node.parent.value), (Object[]) args);
				
				// Library functions that are not implemented in the languages
				} else {
					Matcher pkm = pk_func_fmt.matcher(node.parent.value);
					Matcher nmm = nm_func_fmt.matcher(node.parent.value);
					// Casting
					if (node.parent.value.equals("__CAST_TO__"))
						node.parent.value = Constructs.cast(args[0], args[1]);
					
					else if (pkm.find())
						node.parent.value = pkm.group(1).toLowerCase() + "(" + Services.join(args, ", ") + ")";
					
					// Parenthesized expressions:
					//		TODO: Do some more testing on this, it's more of a hack than anything
					else if ("".equals(node.parent.value))
						node.parent.value = "(" + Services.join(args, " ") + ")";
					
					// Match normal functions
					else if (nmm.find())
						node.parent.value = nmm.group() + "(" + Services.join(args, ", ") + ")";
					
				}
				return substitute(node.parent);
			}
		}
	}
	
	private String replace(String keyphrase, String code) {
		// Python has a different version of for loop
		if (constructs.containsKey(keyphrase)) {
			Matcher m = constructs.get(keyphrase).first.matcher(code);
			if (m.find()) {
				ArrayList<String> lst = new ArrayList<String>();
				for (int i = 1; i <= m.groupCount(); i++)
					if ((m.group(i) != null) && (m.group(i).trim().length() > 0))
						lst.add(m.group(i));
					
				code = String.format(constructs.get(keyphrase).second, lst.toArray());
			}
		}
		return code;
	}
	
	private String replaceStrings(String code) {
		Matcher m = str_ptn.matcher(code);
		if (m.find()) {
			ArrayList<String> args = new ArrayList<String>();
			for (int i = 1; i <= m.groupCount(); i++)
					args.add(m.group(i));
			
			args.set(1, PreprocessData.getUsedString(Integer.parseInt(args.get(1))));
			code = Services.join(args, " ");
		}
		
		return code;
	}

}
