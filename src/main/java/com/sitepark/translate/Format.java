package com.sitepark.translate;

import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Format {
	TEXT,
	HTML;

	@Override
	@JsonValue
	public String toString() {
		return name().toLowerCase(Locale.ENGLISH);
	}
}
