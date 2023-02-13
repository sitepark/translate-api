package com.sitepark.translate.provider.deepl;

import java.util.Arrays;

public class TransportResponse {

	private TransportTranslation[] translations;

	public String[] getTranslations() {
		return Arrays.stream(this.translations)
			.map(translation -> translation.getText())
			.toArray(String[]::new);
	}
}
