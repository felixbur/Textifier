package com.felix.textifier;

import java.util.Vector;

public class Annotation {
private int _position;
private Token _token;
	
	public Annotation(int position, Token token) {
		super();
		_position = position;
		_token = token;
	}


	public int get_position() {
		return _position;
	}

	public void set_position(int position) {
		_position = position;
	}

	public Token get_token() {
		return _token;
	}

	public void set_token(Token token) {
		_token = token;
	}

	public String toString() {
		return _token.get_name();
	}

}
