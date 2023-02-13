package com.sitepark.translate.translator;

import com.sitepark.translate.TranslationLanguage;

public final class StringTranslator extends Translator {

	private StringTranslator(Builder builder) {
		super(builder);
	}

	public String translate(TranslationLanguage language, String q) {
		return super.translate(language, q)[0];
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
