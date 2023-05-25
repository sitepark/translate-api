package com.sitepark.translate.provider.deepl;

import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonValue;

public enum LanguageType {
	SOURCE,
	TARGET;

	@Override
	@JsonValue
	public String toString() {
		return name().toLowerCase(Locale.ENGLISH);
	}
}
