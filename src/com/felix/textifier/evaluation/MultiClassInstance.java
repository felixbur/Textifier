package com.felix.textifier.evaluation;

import java.util.Iterator;
import java.util.Vector;

import com.felix.textifier.Category;

public class MultiClassInstance {
	private Vector<Category> _categories;
	private Category _groundTruth;
	private String _text;

	public MultiClassInstance(Vector<Category> categories, Category groundTruth) {
		super();
		this._categories = categories;
		this._groundTruth = groundTruth;
	}

	public MultiClassInstance(Category groundTruth) {
		super();
		_groundTruth = groundTruth;
	}

	public String getText() {
		return _text;
	}

	public void setText(String text) {
		_text = text;
	}

	public void addCategory(Category i) {
		if (_categories == null)
			_categories = new Vector<Category>();
		_categories.add(i);
	}

	/**
	 * Test if one of the categories equals ground truth.
	 * 
	 * @return True if one of the categories equals ground truth.
	 */
	public boolean isPartiallyCorrect() {
		for (Iterator<Category> iterator = _categories.iterator(); iterator
				.hasNext();) {
			Category cat = (Category) iterator.next();
			if (cat.isSame(_groundTruth))
				return true;
		}
		return false;
	}

	/**
	 * Test if category i equals ground truth.
	 * 
	 * @param classifierIndex
	 *            The index i.
	 * @return True if category i equals ground truth.
	 */
	public boolean isCorrectForClassifier(int classifierIndex) {
		if (_categories.elementAt(classifierIndex).isSame(_groundTruth))
			return true;
		return false;
	}

	public String toString() {
		String ret = "text: " + _text + " cat: ";
		if (_groundTruth != null) {
			ret += _groundTruth.get_id();
		} else {
			ret += "null";
		}
		if (_categories != null) {
			for (Iterator<Category> iterator = _categories.iterator(); iterator
					.hasNext();) {
				Category cat = (Category) iterator.next();
				ret += ", assigned: " + cat.get_id();
			}
		}
		return ret;
	}

	/**
	 * Test if all categories equal ground truth.
	 * 
	 * @return True if all categories equal ground truth.
	 */
	public boolean isCorrect() {
		boolean res = false;
		for (Iterator<Category> iterator = _categories.iterator(); iterator
				.hasNext();) {
			Category cat = (Category) iterator.next();
			if (cat.isSame(_groundTruth))
				res = true;
			else
				return false;
		}
		return res;
	}
}
