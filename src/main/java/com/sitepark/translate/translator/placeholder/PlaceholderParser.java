package com.sitepark.translate.translator.placeholder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlaceholderParser {

	private static final List<PlaceholderType> ORDERED_PLACEHOLDER_TYPES;

	static {
		ORDERED_PLACEHOLDER_TYPES = Arrays.asList(PlaceholderType.values());
		ORDERED_PLACEHOLDER_TYPES.sort(
			(t1, t2) -> {
				return t2.getStart().length() - t1.getStart().length();
			});
	}

	@SuppressWarnings({"PMD.AvoidReassigningLoopVariables", "PMD.AvoidInstantiatingObjectsInLoops"})
	public List<Node> parse(String text) {

		List<Node> nodes = new ArrayList<>();
		StringBuilder buffer = new StringBuilder();
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			PlaceholderType type = this.match(text, i);
			if (type == null) {
				buffer.append(c);
			} else {
				if (buffer.length() > 0) {
					nodes.add(new TextNode(buffer.toString()));
					buffer.setLength(0);
				}

				int end = text.indexOf(type.getEnd(), i + type.getStart().length());

				if (end == -1) {
					buffer.append(c);
				} else {
					String name = text.substring(i + type.getStart().length(), end);
					nodes.add(new PlaceholderNode(new Placeholder(type, name)));
					i = end;
				}
			}
		}

		if (buffer.length() > 0) {
			nodes.add(new TextNode(buffer.toString()));
			buffer.setLength(0);
		}

		return nodes;
	}

	private PlaceholderType match(String text, int start) {
		for (PlaceholderType type : ORDERED_PLACEHOLDER_TYPES) {
			if (text.startsWith(type.getStart(), start)) {
				return type;
			}
		}
		return null;
	}

	public static class Node {

	}

	public static class TextNode extends Node {
		public final String text;
		public TextNode(String text) {
			this.text = text;
		}
	}

	public static class PlaceholderNode extends Node {
		public final Placeholder placeholder;
		public PlaceholderNode(Placeholder placeholder) {
			this.placeholder = placeholder;
		}
	}
}
