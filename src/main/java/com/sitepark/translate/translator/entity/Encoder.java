package com.sitepark.translate.translator.entity;

import java.util.Arrays;

public final class Encoder {

	private Encoder() {}

	public static String[] encode(String... text) {
		return Arrays.stream(text)
			.map(Encoder::encode)
			.toArray(String[]::new);
	}

	public static String encode(String text) {

		StringBuilder encoded = new StringBuilder();
		Scanner scanner = new Scanner(text);

		for (Token token : scanner.scanTokens()) {
			if (token.type == TokenType.STRING) {
				encoded.append(token.lexeme);
			} else if (token.type == TokenType.ENTITY) {
				encoded.append("<span data-encoded-entity=\"true\" translate=\"no\">");
				encoded.append(token.lexeme);
				encoded.append("</span>");
			}
		}

		return encoded.toString();
	}
}
