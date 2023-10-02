package com.sitepark.translate;

import java.util.Optional;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public class GlossaryManager {

	private final TranslationProvider translationProvider;

	@SuppressFBWarnings("EI_EXPOSE_REP2")
	public GlossaryManager(TranslationProvider translationProvider) {
		this.translationProvider = translationProvider;
	}

	public Optional<Glossary> getGlossary(String id) {
		return translationProvider.getGlossary(id);
	}

	public Optional<String> getGlossaryId(String sourceLanguage, String targetLanguage) {
		return translationProvider.getGlossaryId(sourceLanguage, targetLanguage);
	}

	public String recreate(Glossary glossary) {
		return translationProvider.recreate(glossary);
	}

	public void remove(String id) {
		translationProvider.removeGlossary(id);
	}

}
