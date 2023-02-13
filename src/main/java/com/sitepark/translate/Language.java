package com.sitepark.translate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@JsonDeserialize(builder = Language.Builder.class)
public final class Language {

	private final String code;

	private final String name;

	private final List<String> targets;

	private Language(Builder builder) {
		this.code = builder.code;
		this.name = builder.name;
		this.targets = Collections.unmodifiableList(builder.targets);
	}

	public String getCode() {
		return this.code;
	}

	public String getName() {
		return this.name;
	}

	@SuppressFBWarnings("EI_EXPOSE_REP")
	public List<String> getTargets() {
		return this.targets;
	}

	public static Builder builder() {
		return new Builder();
	}

	public Builder toBuilder() {
		return new Builder(this);
	}

	@Override
	public String toString() {
		return this.code + " (" + this.name + ") targets: " + this.targets;
	}

	@JsonPOJOBuilder(withPrefix = "", buildMethodName = "build")
	public static class Builder {

		private String code;

		private String name;

		private final List<String> targets = new ArrayList<>();

		private Builder() { }

		private Builder(Language language) {
			this.code = language.code;
			this.name = language.name;
		}

		public Builder code(String code) {
			assert code != null : "code is null";
			this.code = code;
			return this;
		}

		public Builder name(String name) {
			assert name != null : "name is null";
			this.name = name;
			return this;
		}

		@JsonProperty
		public Builder targets(String... targets) {
			assert targets != null : "target is null";
			this.targets.addAll(Arrays.asList(targets));
			return this;
		}

		public Builder targets(List<String> targets) {
			assert targets != null : "target is null";
			this.targets.addAll(targets);
			return this;
		}

		public Language build() {
			return new Language(this);
		}
	}
}