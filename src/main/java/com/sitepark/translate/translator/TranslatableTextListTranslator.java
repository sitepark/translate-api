package com.sitepark.translate.translator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

		String[] source = untranslated.stream()
				.map(node -> {
					String text = node.getSourceText();
					if (node.getFormat() == Format.TEXT) {
						text = this.encodeHtml(text);
					}
					return text;
				})
				.toArray(String[]::new);

		String[] result = super.translate(language, source);
		for (int i = 0; i < result.length; i++) {
			TranslatableText node = untranslated.get(i);
			String text = result[i];
			if (node.getFormat() == Format.TEXT) {
				text = this.decodeHtml(text);
			}
			node.setTargetText(text);
		}

		if (this.getTranslationCache() != null) {
			this.getTranslationCache().update(translatableTextList);
		}
	}

	@SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
	private String encodeHtml(String s) {

		if (s == null) {
			return null;
		}

		StringBuffer sb = new StringBuffer();

		for (char c : s.toCharArray()) {
			if (c == '<') {
				sb.append("&lt;");
			} else if (c == '>') {
				sb.append("&gt;");
			} else if (c == '&') {
				sb.append("&amp;");
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	@SuppressWarnings({
		"PMD.CyclomaticComplexity",
		"PMD.AvoidLiteralsInIfCondition"
	})
	private String decodeHtml(String s) {

		if (s == null) {
			return null;
		}

		StringBuffer sb = new StringBuffer();
		StringBuffer eb = new StringBuffer();

		for (char c : s.toCharArray()) {
			if (c == '&') {
				eb.setLength(0);
				eb.append(c);
			} else if (c == ';' && eb.length() > 0) {
				String entity = eb.toString();
				if ("&lt;".equals(entity)) {
					sb.append('<');
				} else if ("&gt;".equals(entity)) {
					sb.append('>');
				} else if ("&amp;".equals(entity)) {
					sb.append('&');
				} else {
					sb.append(entity);
				}
				eb.setLength(0);
			} else if (eb.length() > 0 && Character.isLetter(c)) {
				eb.append(c);
			} else {
				eb.setLength(0);
				sb.append(c);
			}
		}
		return sb.toString();
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