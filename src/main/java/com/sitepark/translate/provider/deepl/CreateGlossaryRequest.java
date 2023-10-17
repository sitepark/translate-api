package com.sitepark.translate.provider.deepl;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sitepark.translate.Glossary;
import com.sitepark.translate.GlossaryEntry;
import com.sitepark.translate.TranslationProviderException;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public final class CreateGlossaryRequest {

	public final String name;
	@JsonProperty("source_lang")
	public final String sourceLang;
	@JsonProperty("target_lang")
	public final String targetLang;
	public final String entries;
	@JsonProperty("entries_format")
	public final String entriesFormat;

	private CreateGlossaryRequest(
			String name,
			String sourceLang,
			String targetLang,
			String entries,
			String entriesFormat) {
		this.name = name;
		this.sourceLang = sourceLang;
		this.targetLang = targetLang;
		this.entries = entries;
		this.entriesFormat = entriesFormat;
	}

	@SuppressFBWarnings("URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD")
	public static CreateGlossaryRequest build(Glossary glossary) {

		StringBuilder entries = new StringBuilder();
		for (GlossaryEntry entry : glossary.getEntryList()) {

			CreateGlossaryRequest.validateEntry(entry);

			entries
				.append(entry.getSource())
				.append('\t')
				.append(entry.getTarget())
				.append('\n');
		}

		return new CreateGlossaryRequest(
				glossary.getLanguage().toString(),
				glossary.getLanguage().getSource(),
				glossary.getLanguage().getTarget(),
				entries.toString(),
				"tsv");
	}

	private static void validateEntry(GlossaryEntry entry) {

		if (entry.getSource().indexOf('\t') != -1) {
			throw new TranslationProviderException(
					"The source text must not contain a tab: " + entry.getSource());
		}
		if (entry.getSource().indexOf('\n') != -1) {
			throw new TranslationProviderException(
					"The source text must not contain a newline: " + entry.getSource());
		}
		if (entry.getTarget().indexOf('\t') != -1) {
			throw new TranslationProviderException(
					"The target text must not contain a tab: " + entry.getSource());
		}
		if (entry.getTarget().indexOf('\n') != -1) {
			throw new TranslationProviderException(
					"The target text must not contain a newline: " + entry.getSource());
		}
	}
}
