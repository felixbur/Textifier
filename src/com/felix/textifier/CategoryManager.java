package com.felix.textifier;

import java.util.Iterator;

import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.felix.util.FileUtil;

public class CategoryManager {
	private Vector<Category> _categories;
	private Logger _logger;
	
	
	public CategoryManager(String inputFile) {
		_logger = Logger.getLogger("com.tlabs.textifier.CategoryManager");
		loadCategories(inputFile);
	}

	public Vector<Category> getCategories() {
		return _categories;
	}

	public Category getCategoryForString(String input) {
		double maxCount = 0;
		Category winner = null;
		for (Iterator<Category> iterator = _categories.iterator(); iterator.hasNext();) {
			Category cat = (Category) iterator.next();
			double keyWordCount = cat.keyWordCount(input);
			if (keyWordCount>maxCount) {
				maxCount = keyWordCount;
				winner = cat;
			}
		}
		if (winner==null) {
			winner=new Category(null, "noap", "Nicht Zuzuorden");
		}
		return winner;
	}
	public Category getCategoryForId(String catId) {
		for (Iterator<Category> iterator = _categories.iterator(); iterator.hasNext();) {
			Category cat = (Category) iterator.next();
			if (cat.get_id().compareTo(catId)==0) {
				return cat;
			}
		}
		return null;
	}
	public String getCategoryIds() {
		String ret="";
		for (Iterator<Category> iterator = _categories.iterator(); iterator.hasNext();) {
			Category cat = (Category) iterator.next();
			ret+=cat.get_id()+",";
		}
		return ret.substring(0, ret.length()-1);
	}
	public String getCategoryLongname(String catId) {
		for (Iterator<Category> iterator = _categories.iterator(); iterator.hasNext();) {
			Category cat = (Category) iterator.next();
			if (cat.get_id().compareTo(catId)==0) {
				return cat.get_name();
			}
		}
		return "NA";
	}
	private void loadCategories(String filename) {
		_categories = new Vector<Category>();
		try {
			Vector<String> inputLines = FileUtil.getFileLines(filename);
			Category actCat = null;
			String catName = "";
			String catId = "";
			Vector<String> keywords = null;
			for (Iterator<String> iterator = inputLines.iterator(); iterator
					.hasNext();) {
				String line = (String) iterator.next();
				String buffer = line.trim();
				if (buffer.length() > 0) {
					if (buffer.endsWith(":")) {
						if (catId.length() > 0) {
							actCat = new Category(keywords, catId, catName);
							_categories.add(actCat);
							actCat = null;
							catName = "";
							catId = "";
						}
						keywords = new Vector<String>();
						StringTokenizer st = new StringTokenizer(buffer);
						catId = st.nextToken();
						while (st.hasMoreTokens()) {
							catName += st.nextToken() + " ";
						}
						catName = catName.trim().substring(0, catName.trim().length()-1);
					} else {
						keywords.add(buffer);
					}
				}
			}
			actCat = new Category(keywords, catId, catName);
			_categories.add(actCat);
		} catch (Exception e) {
			e.printStackTrace();
		}
		_categories.add(new Category(null, Constants.NON_APPLICABLE_CATEGORY_ID, Constants.NON_APPLICABLE_CATEGORY_NAME));
		_logger.debug("loaded " + _categories.size() + " categories");
	}
}
