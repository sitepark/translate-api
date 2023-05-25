package com.sitepark.translate;

public final class TranslationLanguage {

	private final SupportedProvider providerType;

	private final String source;

	private final String target;

	private TranslationLanguage(Builder builder) {
		this.providerType = builder.providerType;
		this.source = builder.source;
		this.target = builder.target;
	}

	public SupportedProvider getProviderType() {
		return this.providerType;
	}

	public String getSource() {
		return this.source;
	}

	public String getTarget() {
		return this.target;
	}

	public static Builder builder() {
		return new Builder();
	}

	public Builder toBuilder() {
		return new Builder(this);
	}

	public final static class Builder {

		private SupportedProvider providerType;

		private String source;

		private String target;

		private Builder() { }

		private Builder(TranslationLanguage translationLanguage) {
			this.providerType = translationLanguage.providerType;
			this.source = translationLanguage.source;
			this.target = translationLanguage.target;
		}

		public Builder providerType(SupportedProvider providerType) {
			assert providerType != null : "providerType is null";
			this.providerType = providerType;
			return this;
		}

		public Builder source(String source) {
			assert source != null : "source is null";
			this.source = source;
			return this;
		}

		public Builder target(String target) {
			assert target != null : "target is null";
			this.target = target;
			return this;
		}

		public TranslationLanguage build() {
			assert providerType != null : "providerType is null";
			assert source != null : "source is null";
			assert target != null : "target is null";
			return new TranslationLanguage(this);
		}
	}
}
