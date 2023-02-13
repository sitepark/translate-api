package com.sitepark.translate;

public interface TranslationProvider {
	SupportedLanguages getSupportedLanguages();
	String[] translate(TranslationLanguage language, final String... sourceText);
}
