package com.sitepark.translate;

import java.util.Arrays;
import java.util.Locale;

public enum SupportedProvider {

	DEEPL("deepl"),
	LIBRE_TRANSLATE("libretranslate");

	private final String schema;

	private SupportedProvider(String schema) {
		this.schema = schema;
	}

	public static SupportedProvider ofScheme(String name) {
		for (SupportedProvider type : SupportedProvider.values()) {
			if (type.getSchema().equals(name)) {
				return type;
			}
		}
		throw new IllegalArgumentException("scheme '" + name + "' not found");
	}

	public String getSchema() {
		return this.schema;
	}

	public static String[] scheme() {
		return Arrays.stream(SupportedProvider.values())
			.map(SupportedProvider::getSchema)
			.toArray(String[]::new);
	}
}
