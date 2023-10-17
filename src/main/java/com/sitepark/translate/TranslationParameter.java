package com.sitepark.translate;

import java.util.Objects;
import java.util.Optional;

public final class TranslationParameter {

	private final Format format;

	private final SupportedProvider providerType;

	private final TranslationLanguage language;

	private final String glossaryId;

	private TranslationParameter(Builder builder) {
		this.format = builder.format;
		this.providerType = builder.providerType;
		this.language = builder.language;
		this.glossaryId = builder.glossaryId;
	}

	public Optional<Format> getFormat() {
		return Optional.ofNullable(this.format);
	}

	public SupportedProvider getProviderType() {
		return this.providerType;
	}

	public TranslationLanguage getLanguage() {
		return this.language;
	}

	public Optional<String> getGlossaryId() {
		return Optional.ofNullable(this.glossaryId);
	}

	public static Builder builder() {
		return new Builder();
	}

	public Builder toBuilder() {
		return new Builder(this);
	}

	public final static class Builder {

		private Format format;

		private SupportedProvider providerType;

		private TranslationLanguage language;

		private String glossaryId;

		private Builder() {
		}

		private Builder(TranslationParameter translationParameter) {
			this.format = translationParameter.format;
			this.providerType = translationParameter.providerType;
			this.language = translationParameter.language.toBuilder().build();
			this.glossaryId = translationParameter.glossaryId;
		}

		public Builder providerType(SupportedProvider providerType) {
			Objects.requireNonNull(providerType, "providerType is null");
			this.providerType = providerType;
			return this;
		}

		public Builder format(Format format) {
			Objects.requireNonNull(format, "format is null");
			this.format = format;
			return this;
		}

		public Builder language(TranslationLanguage language) {
			Objects.requireNonNull(language, "language is null");
			this.language = language;
			return this;
		}

		public Builder glossaryId(String glossaryId) {
			Objects.requireNonNull(glossaryId, "glossaryId is null");
			this.glossaryId = glossaryId;
			return this;
		}

		public TranslationParameter build() {

			if (this.providerType == null) {
				throw new IllegalStateException("providerType is not set");
			}
			if (this.language == null) {
				throw new IllegalStateException("language is not set");
			}

			return new TranslationParameter(this);
		}
	}
}
