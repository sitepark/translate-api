package com.sitepark.translate;

import java.util.Optional;

public interface TranslationProvider {
	SupportedLanguages getSupportedLanguages();
	TranslationResult translate(TranslationRequest req);
	Optional<Glossary> getGlossary(String id);
	Optional<String> getGlossaryId(String name);
	String recreate(Glossary glossar);
	void removeGlossary(String id);
}
