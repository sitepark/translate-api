package com.sitepark.translate.translator;

import com.sitepark.translate.TranslationParameter;
import com.sitepark.translate.TranslationRequest;
import com.sitepark.translate.TranslationResult;

public final class StringTranslator extends Translator {

	private StringTranslator(Builder builder) {
		super(builder);
	}

	public String translate(TranslationParameter parameter, String q) {
		TranslationRequest req = TranslationRequest.builder()
				.parameter(parameter)
				.sourceText(q)
				.build();
		TranslationResult result = super.translate(req);
		return result.getText()[0];
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder extends Translator.Builder<Builder> {
		protected Builder() {}

		protected Builder(StringTranslator stringTranslator) {
			super(stringTranslator);
		}

		@Override
		protected Builder self() {
			return this;
		}

		@Override
		public StringTranslator build() {
			return new StringTranslator(this);
		}
	}
}
