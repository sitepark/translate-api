package com.sitepark.translate.provider.libretranslate;

import java.util.Arrays;

public class TransportResponse {

	private String[] translatedText;

	public String[] getTranslatedText() {
		return Arrays.copyOf(this.translatedText, this.translatedText.length);
	}
}
