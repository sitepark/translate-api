package com.sitepark.translate.translator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

		String[] source = untranslated.stream()
				.map(node -> node.getSourceText())
				.toArray(String[]::new);

		String[] result = super.translate(language, source);
		for (int i = 0; i < result.length; i++) {
			TranslatableText node = untranslated.get(i);
			node.setTargetText(result[i]);
		}

		if (this.getTranslationCache() != null) {
			this.getTranslationCache().update(translatableTextList);
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
				text.setTargetText(translatedText.get());
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