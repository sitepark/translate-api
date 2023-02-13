package com.sitepark.translate.translator.placeholder;

public enum PlaceholderType {

	CURLY_BRACKET("{", "}"),
	DOLLAR_CURLY_BRACKET("${", "}");

	private final String start;
	private final String end;

	private PlaceholderType(String start, String end) {
		this.start = start;
		this.end = end;
	}

	public String getStart() {
		return this.start;
	}

	public String getEnd() {
		return this.end;
	}
}
