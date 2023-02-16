package com.sitepark.translate.translator.entity;

public class Token {
	final TokenType type;
	final String lexeme;

	Token(TokenType type, String lexeme) {
		this.type = type;
		this.lexeme = lexeme;
	}

	@Override
	public String toString() {
		return type + " " + lexeme;
	}
}
