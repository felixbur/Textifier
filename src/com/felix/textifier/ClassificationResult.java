package com.felix.textifier;

import java.util.Iterator;
import java.util.Vector;

import org.jdom.Document;
import org.jdom.Element;

public class ClassificationResult {
	Vector<Category> categories;
	String _text;
	public ClassificationResult(Vector<Category> categories, String text) {
		super();
		this.categories = categories;
		_text = text;
	}
	public Document getXMlDocument(){
		Element root = new Element("classification");
		Document rootDoc = new Document(root);
		Element categoriesElem = new Element("categories");
		for (Iterator<Category> iterator = categories.iterator(); iterator.hasNext();) {
			Category cat = (Category) iterator.next();
			Element catElement = new Element("category");
			
			Element nameElem = new Element("name");
			nameElem.addContent(cat.get_id());
			catElement.addContent(nameElem);
			
			Element weightElem = new Element("weight");
			weightElem.addContent(String.valueOf(cat.get_weight()));
			catElement.addContent(weightElem);
			
			Element annotationsElem = new Element("annotations");
			for (Iterator<Annotation> iterator2 = cat.get_annotations().iterator(); iterator2
					.hasNext();) {
				Annotation annotation = (Annotation) iterator2.next();
				Element annotationElem = new Element("annotation");
				Element positionElement = new Element("position");
				positionElement.addContent(String.valueOf(annotation.get_position()));
				annotationElem.addContent(positionElement);
				
				Element tokenElement = new Element("token");
				Token token = annotation.get_token();
				Element tokenNameElem = new Element("name");
				tokenNameElem.addContent(token.get_name());
				tokenElement.addContent(tokenNameElem);
				Element gramsizeElem = new Element("gramsize");
				gramsizeElem.addContent(String.valueOf(token.get_gramSize()));
				tokenElement.addContent(gramsizeElem);
				Element tokenWeightElem = new Element("weight");
				tokenWeightElem.addContent(String.valueOf(token.get_weight()));
				tokenElement.addContent(tokenWeightElem);
				annotationElem.addContent(tokenElement);
				
				Element stringElement = new Element("string");
				stringElement.addContent(annotation.toString());
				annotationElem.addContent(stringElement);
				
				annotationsElem.addContent(annotationElem);
			}
			catElement.addContent(annotationsElem);
			
//			Element summaryElement = new Element("summary");
//			summaryElement.addContent(cat.getSummary());
//			catElement.addContent(summaryElement);
			
			categoriesElem.addContent(catElement);
		}
		root.addContent(categoriesElem);
		
		Element textElem = new Element("text");
		textElem.addContent(_text);
		root.addContent(textElem);
		
		return rootDoc;
	}
	
	
	public Vector<Category> getCategories() {
		return categories;
	}
	public void setCategories(Vector<Category> categories) {
		this.categories = categories;
	}
	public String get_text() {
		return _text;
	}
	public void set_text(String text) {
		_text = text;
	}
	public void addCategory(Category cat) {
		if (categories==null) {
			categories = new Vector<Category>();
		}
		categories.add(cat);
	}
}
