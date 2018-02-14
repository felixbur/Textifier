package com.felix.textifier;

public class Token {
	private String _name;
	private int _gramSize;
	private float _weight;

	public Token(String name, int gramSize, float weight) {
		super();
		_name = name;
		_gramSize = gramSize;
		_weight = weight;
	}

	public String get_name() {
		return _name;
	}


	public int get_gramSize() {
		return _gramSize;
	}


	public float get_weight() {
		return _weight;
	}

}
