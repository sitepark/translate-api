package com.sitepark.translate;

import java.util.Objects;

public final class TranslationEvent {

	private final TranslationLanguage translationLanguage;

	private final long translationTime;

	private final int chunks;

	private final int sourceBytes;

	private final int targetBytes;

	private TranslationEvent(Builder builder) {
		this.translationLanguage = builder.translationLanguage;
		this.translationTime = builder.translationTime;
		this.chunks = builder.chunks;
		this.sourceBytes = builder.sourceBytes;
		this.targetBytes = builder.targetBytes;
	}

	/**
	 * Source and target language of the translation.
	 */
	public TranslationLanguage getTranslationLanguage() {
		return this.translationLanguage;
	}

	/**
	 * Time in milliseconds that the translation took.
	 */
	public long getTranslationTime() {
		return this.translationTime;
	}

	/**
	 * Number of texts passed to libretranslate in an array.
	 */
	public int getChunks() {
		return this.chunks;
	}

	/**
	 * Number of bytes that were translated
	 */
	public int getSourceBytes() {
		return this.sourceBytes;
	}

	/**
	 * Number of bytes of the translated text.
	 */
	public int getTargetBytes() {
		return this.targetBytes;
	}

	public static Builder builder() {
		return new Builder();
	}

	public final static class Builder {

		private TranslationLanguage translationLanguage;

		private long translationTime;

		private int chunks;

		private int sourceBytes;

		private int targetBytes;

		private Builder() {}

		public Builder translationLanguage(TranslationLanguage translationLanguage) {
			Objects.requireNonNull(translationLanguage, "translationLanguage is null");
			this.translationLanguage = translationLanguage;
			return this;
		}

		public Builder translationTime(long translationTime) {
			this.translationTime = translationTime;
			return this;
		}

		public Builder chunks(int chunks) {
			this.chunks = chunks;
			return this;
		}

		public Builder sourceBytes(int sourceBytes) {
			this.sourceBytes = sourceBytes;
			return this;
		}

		public Builder targetBytes(int targetBytes) {
			this.targetBytes = targetBytes;
			return this;
		}

		public TranslationEvent build() {
			if (this.translationLanguage == null) {
				throw new IllegalStateException("translationLanguage is not set");
			}
			return new TranslationEvent(this);
		}
	}
}
