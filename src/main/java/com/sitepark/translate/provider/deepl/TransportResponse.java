package com.sitepark.translate.provider.deepl;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TransportResponse {

	@JsonProperty
	private TransportTranslation[] translations;

	public String[] getTranslations() {
		return Arrays.stream(this.translations)
			.map(translation -> translation.getText())
			.toArray(String[]::new);
	}
}
