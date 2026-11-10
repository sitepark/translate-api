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
      start = current;
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
        if (match('{')) {
          mustache();
        } else {
          entity('}');
        }
        break;
      default:
        string();
        break;
    }
  }

  /**
   * A Mustache tag such as {@code {{data.name}}}, {@code {{{html}}}} or {@code {{#section}}}, which
   * must stay as one entity: protecting only its inner {@code {data.name}} lets the translation
   * change the outer braces. Anything that does not close as a tag is a string.
   */
  private void mustache() {
    boolean triple = match('{');
    match('#');
    match('^');
    match('/');
    match('&');
    while (!isAtEnd() && (isIdentifierChar(peek()) || peek() == ' ')) {
      advance();
    }
    if (match('}') && match('}') && (!triple || match('}'))) {
      addToken(TokenType.ENTITY);
    } else {
      string();
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
    current++;
    return source.charAt(current - 1);
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
    return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
  }

  private boolean isAlphaNumeric(char c) {
    return isAlpha(c) || isDigit(c);
  }

  private boolean isIdentifierChar(char c) {
    return isAlphaNumeric(c) || c == '-' || c == '.';
  }

  private boolean isStringChar(char c) {
    return c != '$' && c != '{';
  }
}
