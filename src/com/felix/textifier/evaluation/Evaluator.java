package com.felix.textifier.evaluation;

import java.util.Iterator;
import java.util.Vector;

import com.felix.textifier.Category;
import com.felix.util.Util;

public class Evaluator {
	private Vector<MultiClassInstance> _instances;
	private Vector<Category> _categories;

	public Evaluator(Vector<Category> categories) {
		super();
		_categories = categories;
	}

	public Evaluator(Vector<MultiClassInstance> instances,
			Vector<Category> categories) {
		super();
		_instances = instances;
		_categories = categories;
	}

	public void addInstance(MultiClassInstance i) {
		if (_instances == null)
			_instances = new Vector<MultiClassInstance>();
		_instances.add(i);
	}

	public String toSummaryString() {
		return "partially correct: "
				+ Util.cutDoubleToFour(getPartialAccuracy())
				+ ", total correct: " + Util.cutDoubleToFour(getAccuracy())
				+ ", Classifier 1 correct: "
				+ Util.cutDoubleToFour(getAccuracyForClassifier(0))
				+ ", Classifier 2 correct: " + Util.cutDoubleToFour(getAccuracyForClassifier(1));
	}

	private double getPartialAccuracy() {
		int correct = 0;
		for (Iterator<MultiClassInstance> iterator = _instances.iterator(); iterator
				.hasNext();) {
			MultiClassInstance inst = (MultiClassInstance) iterator.next();
			if (inst.isPartiallyCorrect()) {
				correct++;
			}
		}
		return (double) correct / _instances.size();
	}

	private double getAccuracy() {
		int correct = 0;
		for (Iterator<MultiClassInstance> iterator = _instances.iterator(); iterator
				.hasNext();) {
			MultiClassInstance inst = (MultiClassInstance) iterator.next();
			if (inst.isCorrect()) {
				correct++;
			}
		}
		return (double) correct / _instances.size();
	}

	private double getAccuracyForClassifier(int classifierIndex) {
		int correct = 0;
		for (Iterator<MultiClassInstance> iterator = _instances.iterator(); iterator
				.hasNext();) {
			MultiClassInstance inst = (MultiClassInstance) iterator.next();
			if (inst.isCorrectForClassifier(classifierIndex)) {
				correct++;
			}
		}
		return (double) correct / _instances.size();
	}

	public Vector<MultiClassInstance> getInstances() {
		return _instances;
	}

}
