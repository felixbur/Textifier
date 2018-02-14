package com.felix.textifier;

import java.util.Iterator;

import java.util.Vector;

import com.felix.util.StringUtil;

public class Category {
	private Vector<String> _keywords;
	private String _id;
	private String _name;
	private double _lastCount = 0;
	private double _weight = 0;
	private Vector<Annotation> _annotations;

	public Category(Vector<String> keywords, String id, String name) {
		set_keywords(keywords);
		_id = id;
		_name = name;
	}

	public void set_keywords(Vector<String> keywords) {
		if (keywords == null) {
			_keywords = new Vector<String>();
			_annotations = new Vector<Annotation>();
			return;
		}
		_keywords = keywords;
		_annotations = new Vector<Annotation>();
		for (Iterator<String> iterator = keywords.iterator(); iterator
				.hasNext();) {
			String string = (String) iterator.next();
			Annotation annotation = new Annotation(0, new Token(string, 1, 1));
			_annotations.add(annotation);
		}
	}

	public boolean isSame(Category otherCategoriy) {
		if (_id.compareTo(otherCategoriy.get_id())==0) {
			return true;
		}
		return false;
	}
	
	public boolean isClassifiable() {
		if (_id.compareTo(Constants.NON_APPLICABLE_CATEGORY_ID) == 0) {
			return false;
		}
		return true;
	}

	public Vector<String> get_keywords() {
		return _keywords;
	}

	public Vector<Annotation> get_annotations() {
		return _annotations;
	}

	public String get_id() {
		return _id;
	}

	public double get_weight() {
		return _weight;
	}

	public void set_weight(double weight) {
		_weight = weight;
	}

	public String get_name() {
		return _name;
	}

	public String toString() {
		return _id + " (" + _name + "): " + _lastCount;
	}

	/**
	 * A summary of the given re-quest�s text. The summary is category specific.
	 * 
	 * @return (may be included in future ver-sions)
	 */
	public String getSummary() {
		return "may be included in future versions";
	}

	/**
	 * Calculate a number based on the occurrence of the categoerie's keywords
	 * in a String.
	 * 
	 * @param test
	 *            The input String.
	 * @return A number counting each uniq keywords plus 0.5 for each
	 *         repetition.
	 */
	public double keyWordCount(String test) {
		if (_keywords == null) {
			return 0;
		}
		double ret = 0;
		for (Iterator<String> iterator = _keywords.iterator(); iterator
				.hasNext();) {
			String keyword = (String) iterator.next();
			if (keyword.trim().length() > 0) {
				int occNum = StringUtil.countTokensIgnoreCase(test, keyword);
				if (occNum > 0) {
					ret++;
					if (occNum > 1) {
						ret += occNum / 2.0;
					}
				}
			}
		}
		_lastCount = ret;
		_weight = ret;
		return ret;
	}
}
