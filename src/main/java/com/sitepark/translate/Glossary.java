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

	private final String sourceLanguage;

	private final String targetLanguage;

	private final List<GlossaryEntry> entryList;

	private Glossary(Builder builder) {
		this.sourceLanguage = builder.sourceLanguage;
		this.targetLanguage = builder.targetLanguage;
		this.entryList = Collections.unmodifiableList(builder.entryList);
	}

	public String getSourceLanguage() {
		return this.sourceLanguage;
	}

	public String getTargetLanguage() {
		return this.targetLanguage;
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
		if (this.sourceLanguage != null) {
			hashCode += this.sourceLanguage.hashCode();
		}
		if (this.targetLanguage != null) {
			hashCode += this.targetLanguage.hashCode();
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
		if (!Objects.equals(glossar.getSourceLanguage(), this.sourceLanguage)) {
			return false;
		} else if (!Objects.equals(glossar.getTargetLanguage(), this.targetLanguage)) {
				return false;
		}

		return Objects.equals(glossar.getEntryList(), this.entryList);
	}

	@Override
	public String toString() {

		StringBuilder b = new StringBuilder();
		b.append("sourceLanguage:");
		b.append(this.sourceLanguage);
		b.append(", targetLanguage:");
		b.append(this.targetLanguage);
		b.append("\n");
		for (GlossaryEntry entity : this.entryList) {
			b.append(entity);
			b.append("\n");
		}

		return b.toString();
	}

	@JsonPOJOBuilder(withPrefix = "", buildMethodName = "build")
	public final static class Builder {

		private String sourceLanguage;

		private String targetLanguage;

		private final List<GlossaryEntry> entryList = new ArrayList<>();

		private Builder() {
		}

		private Builder(Glossary glossar) {
			this.sourceLanguage = glossar.sourceLanguage;
			this.targetLanguage = glossar.targetLanguage;
			this.entryList.addAll(glossar.entryList);
		}

		public Builder sourceLanguage(String sourceLanguage) {
			assert sourceLanguage != null : "sourceLanguage is null";
			assert !sourceLanguage.isBlank() : "sourceLanguage is blank";
			this.sourceLanguage = sourceLanguage;
			return this;
		}

		public Builder entryList(GlossaryEntry... entryList) {
			assert entryList != null : "entryList is null";
			for (GlossaryEntry entry : entryList) {
				this.entry(entry);
			}
			return this;
		}

		public Builder entryList(List<GlossaryEntry> entryList) {
			assert entryList != null : "entryList is null";
			for (GlossaryEntry entry : entryList) {
				this.entry(entry);
			}
			return this;
		}

		public Builder entry(GlossaryEntry entry) {
			assert entry != null : "entry is null";
			this.entryList.add(entry);
			return this;
		}

		public Builder targetLanguage(String targetLanguage) {
			assert targetLanguage != null : "targetLanguage is null";
			assert !targetLanguage.isBlank() : "targetLanguage is blank";
			this.targetLanguage = targetLanguage;
			return this;
		}

		public Glossary build() {
			assert sourceLanguage != null : "sourceLanguage is null";
			assert targetLanguage != null : "targetLanguage is null";
			return new Glossary(this);
		}
	}
}
