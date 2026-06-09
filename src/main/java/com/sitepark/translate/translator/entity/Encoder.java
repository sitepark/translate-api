package com.sitepark.translate.translator.entity;

import java.util.Arrays;
import java.util.List;

public final class Encoder {

  private Encoder() {}

  public static boolean hasPlaceholder(String text) {
    return new Scanner(text).scanTokens().stream().anyMatch(t -> t.type == TokenType.ENTITY);
  }

  public static String[] encode(String... text) {
    return Arrays.stream(text).map(Encoder::encode).toArray(String[]::new);
  }

  public static String encode(String text) {

    StringBuilder encoded = new StringBuilder(70);
    Scanner scanner = new Scanner(text);

    for (Token token : scanner.scanTokens()) {
      if (token.type == TokenType.STRING) {
        encoded.append(token.lexeme);
      } else if (token.type == TokenType.ENTITY) {
        encoded
            .append("<span data-encoded-entity=\"true\" translate=\"no\">")
            .append(token.lexeme)
            .append("</span>");
      }
    }

    return encoded.toString();
  }

  public static String[] encodeXml(String... text) {
    return Arrays.stream(text).map(Encoder::encodeXml).toArray(String[]::new);
  }

  public static String encodeXml(String text) {
    List<Token> tokens = new Scanner(text).scanTokens();
    if (tokens.stream().noneMatch(t -> t.type == TokenType.ENTITY)) {
      return text;
    }
    StringBuilder sb = new StringBuilder();
    for (Token token : tokens) {
      if (token.type == TokenType.STRING) {
        sb.append(token.lexeme.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;"));
      } else if (token.type == TokenType.ENTITY) {
        sb.append("<x>").append(token.lexeme).append("</x>");
      }
    }
    return sb.toString();
  }
}
