package com.sitepark.translate.provider.deepl;

import com.sitepark.translate.TranslationProviderException;

public class ErrorResponse {
	public String message;
	public String detail;

	public TranslationProviderException toException() {
		return new TranslationProviderException(
			this.message + "\n" +
			this.detail);
	}
}
