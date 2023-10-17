package com.sitepark.translate.provider.deepl;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sitepark.translate.TranslationProviderException;

public class ErrorResponse {

	@JsonProperty
	public String message;
	@JsonProperty
	public String detail;

	public TranslationProviderException toException() {
		return new TranslationProviderException(
			this.message + "\n" +
			this.detail);
	}
}
