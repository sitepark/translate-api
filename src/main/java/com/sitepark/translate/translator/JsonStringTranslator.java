package com.sitepark.translate.translator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sitepark.translate.TranslationParameter;

@SuppressWarnings("PMD.AvoidCatchingGenericException")
public final class JsonStringTranslator extends Translator {

  private JsonStringTranslator(Builder builder) {
    super(builder);
  }

  public static Builder builder() {
    return new Builder();
  }

  public String translate(TranslationParameter parameter, String json) {

    JsonNode jsonNode = this.parseJson(json);

    JsonNodeTranslator jsonNodeTranslator =
        JsonNodeTranslator.builder()
            .translatorConfiguration(this.getTranslatorConfiguration())
            .copy(false)
            .build();

    jsonNode = jsonNodeTranslator.translate(parameter, jsonNode);

    return this.jsonToString(jsonNode);
  }

  private String jsonToString(JsonNode jsonNode) {
    try {
      ObjectMapper mapper = new ObjectMapper();
      return mapper.writeValueAsString(jsonNode);
    } catch (Exception e) {
      throw new TranslatorException(e.getMessage(), e);
    }
  }

  private JsonNode parseJson(String s) {
    try {
      ObjectMapper mapper = new ObjectMapper();
      return mapper.readTree(s);
    } catch (Exception e) {
      throw new TranslatorException(e.getMessage(), e);
    }
  }

  public static class Builder extends Translator.Builder<Builder> {
    protected Builder() {}

    protected Builder(JsonStringTranslator stringTranslator) {
      super(stringTranslator);
    }

    @Override
    protected Builder self() {
      return this;
    }

    @Override
    public JsonStringTranslator build() {
      return new JsonStringTranslator(this);
    }
  }
}
