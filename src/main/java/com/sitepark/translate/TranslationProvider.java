package com.sitepark.translate;

public interface TranslationProvider {
	SupportedLanguages getSupportedLanguages();
	String[] translate(Format format, TranslationLanguage language, final String... sourceText);
}
