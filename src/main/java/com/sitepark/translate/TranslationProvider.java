package com.sitepark.translate;

import java.util.Optional;

public interface TranslationProvider {
	SupportedLanguages getSupportedLanguages();
	String[] translate(Format format, TranslationLanguage language, final String... sourceText);
	Optional<Glossary> getGlossary(String id);
	Optional<String> getGlossaryId(String sourceLanguage, String targetLanguage);
	String recreate(Glossary glossar);
	void removeGlossary(String id);
}
