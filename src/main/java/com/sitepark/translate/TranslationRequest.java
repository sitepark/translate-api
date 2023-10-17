package com.sitepark.translate;

import java.util.Arrays;
import java.util.Objects;

public final class TranslationRequest {

	private final TranslationParameter parameter;

	private final String[] sourceText;

	private TranslationRequest(Builder builder) {
		this.parameter = builder.parameter;
		this.sourceText = builder.sourceText;
	}

	public String[] getSourceText() {
		return Arrays.copyOf(this.sourceText, this.sourceText.length);
	}

	public TranslationParameter getParameter() {
		return this.parameter;
	}

	public static Builder builder() {
		return new Builder();
	}

	public Builder toBuilder() {
		return new Builder(this);
	}

	public final static class Builder {

		private TranslationParameter parameter;

		private String[] sourceText;

		private Builder() {
		}

		private Builder(TranslationRequest translationRequest) {
			this.parameter = translationRequest.parameter;
			this.sourceText = translationRequest.sourceText;
		}

		public Builder sourceText(String... sourceText) {
			Objects.requireNonNull(sourceText, "sourceText is null");
			for (String text : sourceText) {
				Objects.requireNonNull(text, "sourceText contains null value");
			}
			this.sourceText = Arrays.copyOf(sourceText, sourceText.length);
			return this;
		}

		public Builder parameter(TranslationParameter parameter) {
			Objects.requireNonNull(parameter, "parameter is null");
			this.parameter = parameter;
			return this;
		}

		public TranslationRequest build() {

			if (this.parameter == null) {
				throw new IllegalStateException("parameter is not set");
			}
			if (this.sourceText == null) {
				throw new IllegalStateException("sourceText is not set");
			}

			return new TranslationRequest(this);
		}
	}
}
