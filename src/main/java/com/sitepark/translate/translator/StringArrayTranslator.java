package com.sitepark.translate.translator;

public final class StringArrayTranslator extends Translator {

	private StringArrayTranslator(Builder builder) {
		super(builder);
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder extends Translator.Builder<Builder> {
		protected Builder() {}

		protected Builder(StringArrayTranslator stringTranslator) {
			super(stringTranslator);
		}

		@Override
		protected Builder self() {
			return this;
		}

		@Override
		public StringArrayTranslator build() {
			return new StringArrayTranslator(this);
		}
	}
}
