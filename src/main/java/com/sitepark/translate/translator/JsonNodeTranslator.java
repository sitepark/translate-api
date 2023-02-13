package com.sitepark.translate.translator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.sitepark.translate.TranslationLanguage;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public final class JsonNodeTranslator extends Translator {

	private final boolean copy;

	private List<JsonNode> jsonList;

	private List<TranslatableTextNode> translatableTextNodeList;

	private JsonNodeTranslator(Builder builder) {
		super(builder);
		this.copy = builder.copy;
	}

	public JsonNode translate(TranslationLanguage language, JsonNode json) {
		List<JsonNode> jsonList = this.translate(language, Arrays.asList(json));
		return jsonList.get(0);
	}

	@SuppressFBWarnings({"EI_EXPOSE_REP2", "EI_EXPOSE_REP"})
	public List<JsonNode> translate(TranslationLanguage language, List<JsonNode> jsonList) {

		if (this.copy) {
			this.jsonList = this.deepCopy(jsonList);
		} else {
			this.jsonList = jsonList;
		}

		this.collectTranslatableText();

		TranslatableTextListTranslator translatableTextListTranslator = TranslatableTextListTranslator.builder()
				.translatorConfiguration(this.getTranslatorConfiguration())
				.build();

		translatableTextListTranslator.translate(language, this.translatableTextNodeList);

		return this.jsonList;
	}

	private List<JsonNode> deepCopy(List<JsonNode> jsonList) {
		List<JsonNode> copy = new ArrayList<>();
		for (JsonNode json : jsonList) {
			copy.add(json.deepCopy());
		}
		return copy;
	}

	public static Builder builder() {
		return new Builder();
	}

	private void collectTranslatableText() {
		TranslatableTextNodeCollector collector = new TranslatableTextNodeCollector();
		this.translatableTextNodeList = collector.collect(this.jsonList);
	}

	public static class Builder extends Translator.Builder<Builder> {

		private boolean copy = true;

		protected Builder() {
		}

		protected Builder(JsonNodeTranslator jsonNodeTranslator) {
			super(jsonNodeTranslator);
		}

		public Builder copy(boolean copy) {
			this.copy = copy;
			return this;
		}

		@Override
		protected Builder self() {
			return this;
		}

		@Override
		public JsonNodeTranslator build() {
			return new JsonNodeTranslator(this);
		}
	}
}
