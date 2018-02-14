package com.felix.textifier;

import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import com.felix.util.StringUtil;

public class DataItem {
	private String _string, _origString;
	private Vector<Category> _categories;

	public DataItem(String string, Vector<Category> categories) {
		super();
		_string = string;
		_origString = string;
		_categories = categories;
	}

	public DataItem(String string, Category category) {
		super();
		_string = string;
		_origString = string;
		_categories = new Vector<Category>();
		_categories.add(category);
	}

	public void addCategory(Category cat) {
		if (_categories == null) {
			_categories = new Vector<Category>();
		}
		_categories.add(cat);
	}

	public String getString() {
		return _string;
	}

	public Vector<String> getWords() {
		return StringUtil.stringToVector(_string);
	}

	public String getOrigString() {
		return _origString;
	}

	public void setString(String string) {
		this._string = string;
	}

	public Vector<Category> getCategories() {
		return _categories;
	}

	public void setCategories(Vector<Category> categories) {
		this._categories = categories;
	}

	public String getARFFLines() {
		String ret = "";
		for (Iterator<Category> iterator = _categories.iterator(); iterator
				.hasNext();) {
			Category cat = (Category) iterator.next();
			ret += "\"" + _string + "\"," + cat.get_id() + "\n";
		}
		return ret;
	}

	public String toString() {
		String catS = "";
		for (Iterator iterator = _categories.iterator(); iterator.hasNext();) {
			Category cat = (Category) iterator.next();
			catS += cat.toString() + ", ";
		}
		return "orig: " + _origString + ", string: " + _string + ", cat: "
				+ catS;
	}
}
