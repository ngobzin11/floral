package com.pkoding.universal.run;

import java.util.ArrayList;

import com.pkoding.recognizer.*;
import com.pkoding.preprocess.*;
import com.pkoding.preprocess.project.Structure;
import com.pkoding.preprocess.project.objects.ProjectClass;
import com.pkoding.translator.*;
import com.pkoding.recognizer.error.SyntaxError;
import com.pkoding.universal.data.PreprocessData;

public class Main {
	
	private static Translator translator;
	
	public static void main(String[] args) {
		// Jar Run: Un-comment this plus the variable in PreprocessData
		// if (args.length > 1) {
			// filename, language
			String filename = "test", lang = "javascript";
			PreprocessData.setOutputLanguage(lang);
			Preprocessor preprocessor = new Preprocessor();
			CodeWriter buffer = new CodeWriter(filename);
			preprocessor.extractCode(buffer.code());
			preprocessor.compile(preprocessor.startingCode(), 0);
			
			Recognizer recognizer = new Recognizer(preprocessor.outputCode());
			// recognizer.lexer.print();
			try {
				recognizer.parser.execute();
			} catch (SyntaxError e) {
				e.printStackTrace();
			}
			translator = new Translator(recognizer.lexer, preprocessor.usedLibraries());
			translator.print();
			buffer.updateExtension();
			buffer.write(translator.indentedCode());
		/*
		} else {
			System.err.println("Jar file is expecting two arguments: filename and output language. \n" +
				"\tOutput language can either be 'python', 'php' or 'javascript'. \n" +
				"\tStore the file in ./floral_data/code/raw/. There should be algorithms there already");
		}
		*/
		// new Main("floyd", "php");
	}
	
	public static ArrayList<String> indentedCode() {
		return translator.indentedCode();
	}

}
