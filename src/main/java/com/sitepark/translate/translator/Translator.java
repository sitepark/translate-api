package com.sitepark.translate.translator;

import com.sitepark.translate.Format;
import com.sitepark.translate.SupportedProvider;
import com.sitepark.translate.TranslationConfiguration;
import com.sitepark.translate.TranslationLanguage;
import com.sitepark.translate.TranslationProvider;
import com.sitepark.translate.TranslationProviderFactory;

public abstract class Translator {

	private final TranslationConfiguration translatorConfiguration;

	protected Translator(Builder<?> builder) {
		this.translatorConfiguration = builder.translatorConfiguration;
	}

	protected TranslationConfiguration getTranslatorConfiguration() {
		return this.translatorConfiguration;
	}

	protected TranslationProvider createTransporter(SupportedProvider providerType) {
		return this.getTransporterFactory().create(providerType);
	}

	private TranslationProviderFactory getTransporterFactory() {
		return this.translatorConfiguration.getTranslationProviderFactory();
	}

	protected String[] translate(Format format, TranslationLanguage language, String... source) {
		String[] target = this.createTransporter(language.getProviderType())
				.translate(
						format,
						language,
						source);
		return target;
	}

	public static abstract class Builder<B extends Builder<B>> {

		private TranslationConfiguration translatorConfiguration;

		protected Builder() {
		}

		protected Builder(Translator translator) {
			this.translatorConfiguration = translator.translatorConfiguration;
		}

		public B translatorConfiguration(TranslationConfiguration.Builder translatorConfiguration) {
			assert translatorConfiguration != null : "translatorConfiguration is null";
			this.translatorConfiguration = translatorConfiguration.build();
			return this.self();
		}

		public B translatorConfiguration(TranslationConfiguration translatorConfiguration) {
			assert translatorConfiguration != null : "translatorConfiguration is null";
			this.translatorConfiguration = translatorConfiguration;
			return this.self();
		}

		protected abstract B self();

		public abstract Translator build();
	}
}
