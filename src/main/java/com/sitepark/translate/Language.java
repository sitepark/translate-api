package com.sitepark.translate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

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
	public int hashCode() {
		int hashCode = 0;
		if (this.code != null) {
			hashCode += this.code.hashCode();
		}
		if (this.name != null) {
			hashCode += this.name.hashCode();
		}
		if (this.targets != null) {
			hashCode += this.targets.hashCode();
		}
		return hashCode;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Language)) {
			return false;
		}
		Language l = (Language)o;
		if (!Objects.equals(l.getCode(), this.code)) {
			return false;
		} else if (!Objects.equals(l.getName(), this.name)) {
			return false;
		}
		return Objects.equals(l.getTargets(), this.targets);
	}

	@Override
	public String toString() {
		return this.code + " (" + this.name + ") targets: " + this.targets;
	}

	@JsonPOJOBuilder(withPrefix = "", buildMethodName = "build")
	public final static class Builder {

		private String code;

		private String name;

		private final List<String> targets = new ArrayList<>();

		private Builder() { }

		private Builder(Language language) {
			this.code = language.code;
			this.name = language.name;
			this.targets.addAll(language.targets);
		}

		public Builder code(String code) {
			Objects.requireNonNull(code, "code is null");
			this.code = code;
			return this;
		}

		public Builder name(String name) {
			Objects.requireNonNull(name, "name is null");
			this.name = name;
			return this;
		}

		@JsonProperty
		public Builder targets(String... targets) {
			Objects.requireNonNull(targets, "targets is null");
			for (String target : targets) {
				this.target(target);
			}
			return this;
		}

		public Builder targets(List<String> targets) {
			Objects.requireNonNull(targets, "targets is null");
			for (String target : targets) {
				this.target(target);
			}
			return this;
		}

		public Builder target(String target) {
			Objects.requireNonNull(target, "target is null");
			this.targets.add(target);
			return this;
		}

		public Language build() {
			if (this.code == null) {
				throw new IllegalStateException("code not set");
			}
			if (this.name == null) {
				throw new IllegalStateException("name not set");
			}
			if (targets.isEmpty()) {
				throw new IllegalStateException("no targets set");
			}
			return new Language(this);
		}
	}
}