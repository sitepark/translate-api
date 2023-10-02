package com.sitepark.translate.provider.deepl;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sitepark.translate.Glossary;
import com.sitepark.translate.GlossaryEntry;
import com.sitepark.translate.TranslationProviderException;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public class CreateGlossaryRequest {

	public String name;
	@JsonProperty("source_lang")
	public String sourceLang;
	@JsonProperty("target_lang")
	public String targetLang;
	public String entries;
	@JsonProperty("entries_format")
	public String entriesFormat;

	@SuppressFBWarnings("URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD")
	public static CreateGlossaryRequest build(Glossary glossary) {

		CreateGlossaryRequest req = new CreateGlossaryRequest();
		req.name = glossary.getSourceLanguage() + " - " + glossary.getTargetLanguage();
		req.sourceLang = glossary.getSourceLanguage();
		req.targetLang = glossary.getTargetLanguage();

		StringBuilder entries = new StringBuilder();
		for (GlossaryEntry entry : glossary.getEntryList()) {

			CreateGlossaryRequest.validateEntry(entry);

			entries
				.append(entry.getSource())
				.append('\t')
				.append(entry.getTarget())
				.append('\n');
		}

		req.entries = entries.toString();
		req.entriesFormat = "tsv";

		return req;
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
