package com.pkoding.universal.run;

import java.io.*;
import java.util.ArrayList;

import com.pkoding.universal.data.PreprocessData;
import com.pkoding.universal.util.Tuple;

public class CodeWriter {
	
	private Tuple<String, String> enclosures = null;
	
	private String extension = ".pcode";
	
	private final String filename;
	
	private final String root = PreprocessData.CMD + "code/";
	
	private String[] code = null;
	
	public CodeWriter(String filename) {
		this.filename = filename;
		read();
	}
	
	public void write(ArrayList<String> code) {
		try {
			File file = new File(makeFileName(filename));
 
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
			BufferedWriter bw = new BufferedWriter(new FileWriter(file.getAbsoluteFile()));
			
			if (enclosures != null)
				bw.write(enclosures.first + "\n\n");
			
			for (String line : code) 
				bw.write(line + "\n");
			
			if (enclosures != null)
				bw.write("\n" + enclosures.second);
			
			bw.close();
 
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String[] code() {
		return code;
	}
	
	public void updateExtension() {
		String lang = PreprocessData.getOutputLanguage();
		if (lang.equals("python"))
			extension = ".py";
		
		else if (lang.equals("javascript") || lang.equals("console"))
			extension = ".js";
		
		else {
			enclosures = new Tuple<String, String>("<?php", "?>");
			extension = ".php";
		}
	}
	
	private void read() {
		ArrayList<String> code = new ArrayList<String>();
		BufferedReader br = null;
		 
		try {
			String line;
			br = new BufferedReader(new FileReader(root + "raw/" + filename));
 
			while ((line = br.readLine()) != null)
				code.add(line);
 
		} catch (IOException e) {
			e.printStackTrace();
			
		} finally {
			try {
				if (br != null)
					br.close();
				
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		
		// Add To Array
		int i = 0;
		this.code = new String[code.size()];
		for (String line : code)
			this.code[i++] = line;
	}
	
	private String makeFileName(String name) {
		return root + (extension.equals(".pcode") ? "pcode/" : PreprocessData.getOutputLanguage() + "/") + 
			filename + extension;
	}

}
