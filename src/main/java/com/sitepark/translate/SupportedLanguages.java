package com.sitepark.translate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.node.ArrayNode;

@JsonFormat(shape = JsonFormat.Shape.ARRAY)
@JsonDeserialize(using = SupportedLanguages.Deserializer.class)
public final class SupportedLanguages {

	private final Map<String, Language> languages;

	private SupportedLanguages(Builder builder) {
		this.languages = builder.languages;
	}

	public List<Language> getAll() {
		return new ArrayList<>(this.languages.values());
	}

	public Optional<Language> getSourceLanguage(String source) {
		return Optional.ofNullable(this.languages.get(source));
	}

	public Optional<Language> getTargetLanguage(String source, String target) {
		Language sourceLanguage = this.languages.get(source);
		if (sourceLanguage == null) {
			return Optional.empty();
		}

		if (!sourceLanguage.getTargets().contains(target)) {
			return Optional.empty();
		}

		return Optional.ofNullable(this.languages.get(target));
	}

	public Builder toBuilder() {
		return new Builder(this);
	}

	public static Builder builder() {
		return new Builder();
	}

	public final static class Builder {

		private final Map<String, Language> languages = new ConcurrentHashMap<>();

		private Builder() {
		}

		private Builder(SupportedLanguages supportedLanguages) {
			this.languages.putAll(supportedLanguages.languages);
		}

		public Builder language(Language language) {
			Objects.requireNonNull(language, "language is null");
			this.languages.put(language.getCode(), language);
			return this;
		}

		public Builder language(Language.Builder languageBuilder) {
			Objects.requireNonNull(languageBuilder, "languageBuilder is null");
			Language language = languageBuilder.build();
			this.languages.put(language.getCode(), language);
			return this;
		}

		public SupportedLanguages build() {
			return new SupportedLanguages(this);
		}
	}

	public static class Deserializer extends JsonDeserializer<SupportedLanguages> {

		@Override
		public SupportedLanguages deserialize(JsonParser parser, DeserializationContext ctxt)
				throws IOException, JsonProcessingException {

			Builder builder = SupportedLanguages.builder();

			ObjectCodec codec = parser.getCodec();
			TreeNode node = codec.readTree(parser);

			if (node.isArray()) {
				for (JsonNode n : (ArrayNode) node) {
					Language language = ctxt.readTreeAsValue(n, Language.class);
					builder.language(language);
				}
			}
			return builder.build();
		}
	}
}
