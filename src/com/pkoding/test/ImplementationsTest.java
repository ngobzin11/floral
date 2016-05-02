package com.pkoding.test;

import java.io.File;

import com.pkoding.preprocess.Preprocessor;
import com.pkoding.recognizer.Recognizer;
import com.pkoding.recognizer.error.SyntaxError;
import com.pkoding.translator.Translator;
import com.pkoding.universal.data.PreprocessData;
import com.pkoding.universal.run.CodeWriter;

public class ImplementationsTest {
	
	private Translator translator;
	private final String root = "code/raw";
	
	public static void main(String[] args) {
		new ImplementationsTest("php");
	}
	
	public ImplementationsTest(String lang) {
		PreprocessData.setOutputLanguage(lang);
		Preprocessor preprocessor = new Preprocessor();
		File folder = new File(root);
		if (folder.exists()) {
			if (folder.isDirectory()) {
				File[] files = folder.listFiles();
				if ((files != null) && (files.length > 0)) { 
					for (File file : files) {
						if (file.isFile()) {
							System.out.println("Reading " + file.getName() + "...");
							CodeWriter buffer = new CodeWriter(file.getName());
							preprocessor.extractCode(buffer.code());
							preprocessor.compile(preprocessor.startingCode(), 0);
							Recognizer recognizer = new Recognizer(preprocessor.outputCode());
							try {
								recognizer.parser.execute();
							} catch (SyntaxError e) {
								e.printStackTrace();
							}
							translator = new Translator(recognizer.lexer, preprocessor.usedLibraries());
							buffer.updateExtension();
							buffer.write(translator.indentedCode());
							System.out.println("Done Reading " + file.getName() + "...\n\n");
						}
					}
				}
			}
		}
	}

}
