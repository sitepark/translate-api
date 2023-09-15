package com.sitepark.translate.translator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.sitepark.translate.Format;
import com.sitepark.translate.TranslationCache;
import com.sitepark.translate.TranslationLanguage;

public final class TranslatableTextListTranslator extends Translator {

	private TranslatableTextListTranslator(Builder builder) {
		super(builder);
	}

	public void translate(TranslationLanguage language, List<? extends TranslatableText> translatableTextList) {

		List<? extends TranslatableText> untranslated =
				this.translateWithCacheIfPossible(translatableTextList);

		if (untranslated.isEmpty()) {
			return;
		}

		this.translate(Format.HTML, language, untranslated);
		this.translate(Format.TEXT, language, untranslated);

		if (this.getTranslationCache() != null) {
			this.getTranslationCache().update(translatableTextList);
		}
	}

	private void translate(
			Format format,
			TranslationLanguage language,
			List<? extends TranslatableText> translatableTextList) {

		List<? extends TranslatableText> formatFiltered = translatableTextList.stream()
				.filter(text -> text.getFormat() == format)
				.collect(Collectors.toList());

		if (formatFiltered.isEmpty()) {
			return;
		}

		String[] source = formatFiltered.stream()
				.map(node -> {
					String text = node.getSourceText();
					return text;
				})
				.toArray(String[]::new);

		String[] result = super.translate(format, language, source);
		for (int i = 0; i < result.length; i++) {
			TranslatableText node = formatFiltered.get(i);
			String text = result[i];
			node.setTargetText(text);
		}
	}

	private TranslationCache getTranslationCache() {
		return this.getTranslatorConfiguration().getTranslationCache()
					.orElse(null);
	}

	/**
	 * @return all untranslated texts
	 */
	private List<? extends TranslatableText> translateWithCacheIfPossible(
			List<? extends TranslatableText> translatableTextList) {

		if (this.getTranslationCache() == null) {
			return translatableTextList;
		}

		List<TranslatableText> untranslated = new ArrayList<>();

		for (TranslatableText text : translatableTextList) {
			Optional<String> translatedText =
					this.getTranslationCache().translate(text.getSourceText());
			if (translatedText.isPresent()) {
				String t = translatedText.get();
				if (text.getFormat() == Format.TEXT && (
							t.startsWith("&amp ") ||
							t.endsWith(" &amp") ||
							t.indexOf(" &amp ") != -1 ||
							t.startsWith("&amp; ") ||
							t.endsWith(" &amp;") ||
							t.indexOf(" &amp; ") != -1)
				) {
					untranslated.add(text);
				} else {
					text.setTargetText(translatedText.get());
				}
			} else {
				untranslated.add(text);
			}
		}

		return untranslated;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder extends Translator.Builder<Builder> {

		protected Builder() {
		}

		protected Builder(TranslatableTextListTranslator updatableListTranslator) {
			super(updatableListTranslator);
		}

		@Override
		protected Builder self() {
			return this;
		}

		@Override
		public TranslatableTextListTranslator build() {
			return new TranslatableTextListTranslator(this);
		}
	}
}