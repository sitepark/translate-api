package com.sitepark.translate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@JsonDeserialize(builder = Glossary.Builder.class)
public final class Glossary {

	private final String name;

	private final TranslationLanguage language;

	private final List<GlossaryEntry> entryList;

	private Glossary(Builder builder) {
		this.name = builder.name;
		this.language = builder.language;
		this.entryList = Collections.unmodifiableList(builder.entryList);
	}

	public String getName() {
		return this.name;
	}

	public TranslationLanguage getLanguage() {
		return this.language;
	}

	@SuppressFBWarnings("EI_EXPOSE_REP")
	public List<GlossaryEntry> getEntryList() {
		return this.entryList;
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
		if (this.name != null) {
			hashCode += this.name.hashCode();
		}
		if (this.language != null) {
			hashCode += this.language.hashCode();
		}
		if (this.entryList != null) {
			hashCode += this.entryList.hashCode();
		}

		return hashCode;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Glossary)) {
			return false;
		}
		Glossary glossar = (Glossary)o;
		return
				Objects.equals(glossar.getName(), this.name) &&
				Objects.equals(glossar.getLanguage(), this.language) &&
				Objects.equals(glossar.getEntryList(), this.entryList);
	}

	@Override
	public String toString() {

		StringBuilder b = new StringBuilder(50)
				.append("name:")
				.append(this.name)
				.append(", ")
				.append("language:")
				.append(this.language)
				.append('\n');
		for (GlossaryEntry entity : this.entryList) {
			b.append(entity).append('\n');
		}

		return b.toString();
	}

	@JsonPOJOBuilder(withPrefix = "", buildMethodName = "build")
	public final static class Builder {

		private String name;

		private TranslationLanguage language;

		private final List<GlossaryEntry> entryList = new ArrayList<>();

		private Builder() {
		}

		private Builder(Glossary glossar) {
			this.name = glossar.name;
			this.language = glossar.language;
			this.entryList.addAll(glossar.entryList);
		}

		public Builder name(String name) {
			Objects.requireNonNull(name, "name is null");
			if (name.isBlank()) {
				throw new IllegalArgumentException("name is blank");
			}
			this.name = name;
			return this;
		}

		public Builder language(TranslationLanguage language) {
			Objects.requireNonNull(language, "language is null");
			this.language = language;
			return this;
		}

		public Builder entryList(GlossaryEntry... entryList) {
			Objects.requireNonNull(entryList, "entryList is null");
			for (GlossaryEntry entry : entryList) {
				this.entry(entry);
			}
			return this;
		}

		public Builder entryList(List<GlossaryEntry> entryList) {
			Objects.requireNonNull(entryList, "entryList is null");
			for (GlossaryEntry entry : entryList) {
				this.entry(entry);
			}
			return this;
		}

		public Builder entry(GlossaryEntry entry) {
			Objects.requireNonNull(entry, "entry is null");
			this.entryList.add(entry);
			return this;
		}

		public Glossary build() {
			if (this.name == null) {
				throw new IllegalStateException("name not set");
			}
			if (this.language == null) {
				throw new IllegalStateException("language not set");
			}
			return new Glossary(this);
		}
	}
}
