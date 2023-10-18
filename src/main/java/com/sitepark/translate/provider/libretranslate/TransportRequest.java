package com.sitepark.translate.provider.libretranslate;

import java.util.Arrays;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sitepark.translate.Format;

public final class TransportRequest {

	private final String[] q;

	private final String source;

	private final String target;

	private final Format format;

	private final String apiKey;

	private TransportRequest(Builder builder) {
		this.q = builder.q;
		this.source = builder.source;
		this.target = builder.target;
		this.format = builder.format;
		this.apiKey = builder.apiKey;
	}

	public String[] getQ() {
		return Arrays.copyOf(this.q, this.q.length);
	}

	public String getSource() {
		return this.source;
	}

	public String getTarget() {
		return this.target;
	}

	public Format getFormat() {
		return this.format;
	}

	@JsonProperty("api_key")
	public String getApiKey() {
		return this.apiKey;
	}

	public static Builder builder() {
		return new Builder();
	}

	public Builder toBuilder() {
		return new Builder(this);
	}

	public final static class Builder {

		private String[] q;

		private String source;

		private String target;

		private Format format = Format.TEXT;

		private String apiKey = "";

		private Builder() { }

		private Builder(TransportRequest request) {
			this.q = request.q;
			this.source = request.source;
			this.target = request.target;
			this.format = request.format;
			this.apiKey = request.apiKey;
		}

		public Builder q(String... q) {
			Objects.requireNonNull(q, "q is null");
			if (q.length == 0) {
				throw new IllegalArgumentException("q is empty");
			}
			this.q = Arrays.copyOf(q, q.length);
			for (String item : q) {
				Objects.requireNonNull(item, "q item is null");
			}
			return this;
		}

		public Builder source(String source) {
			Objects.requireNonNull(source, "source is null");
			this.source = source;
			return this;
		}

		public Builder target(String target) {
			Objects.requireNonNull(target, "target is null");
			this.target = target;
			return this;
		}

		public Builder format(Format format) {
			Objects.requireNonNull(format, "format is null");
			this.format = format;
			return this;
		}

		public Builder apiKey(String apiKey) {
			Objects.requireNonNull(apiKey, "apiKey is null");
			this.apiKey = apiKey;
			return this;
		}

		public TransportRequest build() {
			if (this.q == null) {
				throw new IllegalStateException("q is not set");
			}
			if (this.source == null) {
				throw new IllegalStateException("source is not set");
			}
			if (this.target == null) {
				throw new IllegalStateException("target is not set");
			}
			return new TransportRequest(this);
		}
	}
}
