package com.sitepark.translate.translator.entity;

import java.util.Arrays;

public final class Encoder {

  private Encoder() {}

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
}
