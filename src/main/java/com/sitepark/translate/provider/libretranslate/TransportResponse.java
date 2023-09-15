package com.sitepark.translate.provider.libretranslate;

import java.util.Arrays;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings({
	"UWF_UNWRITTEN_FIELD",
	"NP_UNWRITTEN_FIELD"
})
public class TransportResponse {

	private String[] translatedText;

	public String[] getTranslatedText() {
		return Arrays.copyOf(this.translatedText, this.translatedText.length);
	}
}
