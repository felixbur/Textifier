package com.felix.textifier.webservice;

import com.felix.textifier.Textifier;


public class Classification {

	private final String description = "Textclassification Service";
	
	private final int majorVersion = 1;
	private final int minorVersion = 1;

	public String Version(){
		String ret = String.format("%d.%03d", majorVersion,minorVersion);
		return ret;
	}
	public String Description(){
		return description;
	}
	
	public String ClassificationXMLForText(String text, KeyValue[] properties){
		// do some work with text and Properties...
		// render result to String
		Textifier textifier = Textifier.getInstance();
		textifier.classifiy(text);
		return textifier.getXMLInterpretation();
	}
	
	public ClassificationResult ClassificationForText(String text, KeyValue[] properties){
		// do some work with text and Properties...
		// render result to String
		ClassificationResult ret = new ClassificationResult();
		
		//return result
		return ret;
	}
}
