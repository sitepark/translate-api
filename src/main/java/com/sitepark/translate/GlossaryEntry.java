package com.sitepark.translate;

import java.util.Objects;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonDeserialize(builder = GlossaryEntry.Builder.class)
public final class GlossaryEntry {

	private final String source;

	private final String target;

	private GlossaryEntry(Builder builder) {
		this.source = builder.source;
		this.target = builder.target;
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

	@Override
	public int hashCode() {
		int hashCode = 0;
		if (this.source != null) {
			hashCode += this.source.hashCode();
		}
		if (this.target != null) {
			hashCode += this.target.hashCode();
		}
		return hashCode;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof GlossaryEntry)) {
			return false;
		}

		GlossaryEntry entry = (GlossaryEntry)o;
		return
				Objects.equals(entry.getSource(), this.source) &&
				Objects.equals(entry.getTarget(), this.target);
	}

	@Override
	public String toString() {
		return this.source + ": " + this.target;
	}


	@JsonPOJOBuilder(withPrefix = "", buildMethodName = "build")
	public final static class Builder {

		private String source;

		private String target;

		private Builder() {
		}

		private Builder(GlossaryEntry glossarEntry) {
			this.source = glossarEntry.source;
			this.target = glossarEntry.target;
		}

		public Builder source(String source) {
			assert source != null : "source is null";
			assert !source.isBlank() : "source is blank";
			this.source = source;
			return this;
		}

		public Builder target(String target) {
			assert target != null : "target is null";
			assert !target.isBlank() : "target is blank";
			this.target = target;
			return this;
		}

		public GlossaryEntry build() {

			assert source != null : "source is null";
			assert target != null : "target is null";

			return new GlossaryEntry(this);
		}
	}
}
