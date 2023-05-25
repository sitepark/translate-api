package com.sitepark.translate.translator.entity;

import static com.sitepark.translate.translator.entity.TokenType.EOF;
import static com.sitepark.translate.translator.entity.TokenType.STRING;

import java.util.ArrayList;
import java.util.List;

class Scanner {

	private final String source;
	private final List<Token> tokens = new ArrayList<>();
	private int start;
	private int current;

	Scanner(String source) {
		this.source = source;
	}

	List<Token> scanTokens() {
		while (!isAtEnd()) {
			// We are at the beginning of the next lexeme.
			start = current; // NOPMD by veltrup on 24.05.23, 15:38
			scanToken();
		}

		tokens.add(new Token(EOF, ""));
		return tokens;
	}

	private boolean isAtEnd() {
		return current >= source.length();
	}

	private void scanToken() {
		char c = advance();
		switch (c) {
			case '$':
				if (match('{')) {
					entity('}');
				} else {
					string();
				}
				break;
			case '{':
				entity('}');
				break;
			/*
			case '&':
				entity(';');
				break;
			*/
			default :
				string();
			break;
		}
	}

	private void entity(char expectedEnd) {
		while (!isAtEnd() && isIdentifierChar(peek())) {
			advance();
		}
		if (peek() == expectedEnd) {
			advance();
			addToken(TokenType.ENTITY);
		} else {
			string();
		}
	}

	private void string() {
		while (!isAtEnd() && isStringChar(peek())) {
			advance();
		}
		addToken(STRING);
	}

	private char advance() {
		return source.charAt(current++);
	}

	private void addToken(TokenType type) {
		String text = source.substring(start, current);
		tokens.add(new Token(type, text));
	}

	private boolean match(char expected) {
		if (isAtEnd()) {
			return false;
		}
		if (source.charAt(current) != expected) {
			return false;
		}
		current++;
		return true;
	}

	private char peek() {
		if (isAtEnd()) {
			return '\0';
		}
		return source.charAt(current);
	}

	private boolean isDigit(char c) {
		return c >= '0' && c <= '9';
	}

	private boolean isAlpha(char c) {
		return (c >= 'a' && c <= 'z') ||
			(c >= 'A' && c <= 'Z') ||
			c == '_';
	}

	private boolean isAlphaNumeric(char c) {
		return isAlpha(c) || isDigit(c);
	}

	private boolean isIdentifierChar(char c) {
		return isAlphaNumeric(c) || c == '-' || c == '.';
	}

	private boolean isStringChar(char c) {
		// return c != '$' && c != '{' && c != '&';
		return c != '$' && c != '{';
	}
}
