package com.sitepark.translate.translator.placeholder;

import java.util.Arrays;
import java.util.List;

import com.sitepark.translate.translator.placeholder.PlaceholderParser.Node;
import com.sitepark.translate.translator.placeholder.PlaceholderParser.PlaceholderNode;
import com.sitepark.translate.translator.placeholder.PlaceholderParser.TextNode;

public final class PlaceholderEncoder {

	private PlaceholderEncoder() {}

	public static String[] encode(String... text) {
		return Arrays.stream(text)
			.map(PlaceholderEncoder::encode)
			.toArray(String[]::new);
	}

	public static String encode(String text) {
		PlaceholderParser parser = new PlaceholderParser();
		List<Node> nodes = parser.parse(text);
		StringBuilder encoded = new StringBuilder();
		for (Node n : nodes) {
			if (n instanceof TextNode) {
				encoded.append(((TextNode)n).text);
			} else if (n instanceof PlaceholderNode) {
				PlaceholderNode placeholderNode = (PlaceholderNode)n;
				encoded.append("<span data-encoded-placeholder=\"true\" translate=\"no\">");
				encoded.append(placeholderNode.placeholder.toString());
				encoded.append("</span>");
			}
		}

		return encoded.toString();
	}
}
