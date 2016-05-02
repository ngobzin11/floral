package com.pkoding.recognizer;

import java.util.ArrayList;

import com.pkoding.recognizer.lexer.*;
import com.pkoding.recognizer.parser.*;

public class Recognizer {
	
	public final Lexer lexer;
	
	public final Parser parser;
	
	public Recognizer(ArrayList<String> lines) {
		lexer = new Lexer(lines);
		parser = new Parser(lexer);
	}
	
}