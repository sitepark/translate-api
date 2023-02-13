package com.sitepark.translate.translator.placeholder;

public class Placeholder {

	private final PlaceholderType type;
	private final String name;

	public Placeholder(PlaceholderType type, String name) {
		this.type = type;
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public PlaceholderType getType() {
		return this.type;
	}

	@Override
	public String toString() {
		return this.type.getStart() + this.name + this.type.getEnd();
	}
}
