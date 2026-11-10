package com.sitepark.translate.translator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.sitepark.translate.TranslationParameter;

@SuppressWarnings("PMD.AvoidCatchingGenericException")
public final class YamlStringTranslator extends Translator {

  private YamlStringTranslator(Builder builder) {
    super(builder);
  }

  public static Builder builder() {
    return new Builder();
  }

  public String translate(TranslationParameter parameter, String yaml) {

    JsonNode yamlNode = this.parseYaml(yaml);

    JsonNodeTranslator jsonNodeTranslator =
        JsonNodeTranslator.builder()
            .translatorConfiguration(this.getTranslatorConfiguration())
            .copy(false)
            .build();

    yamlNode = jsonNodeTranslator.translate(parameter, yamlNode);

    return this.yamlToString(yamlNode);
  }

  private String yamlToString(JsonNode yamlNode) {
    try {
      YAMLMapper mapper =
          YAMLMapper.builder().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER).build();
      return mapper.writeValueAsString(yamlNode);
    } catch (Exception e) {
      throw new TranslatorException(e.getMessage(), e);
    }
  }

  private JsonNode parseYaml(String s) {
    try {
      YAMLMapper mapper = new YAMLMapper();
      return mapper.readTree(s);
    } catch (Exception e) {
      throw new TranslatorException(e.getMessage(), e);
    }
  }

  public static class Builder extends Translator.Builder<Builder> {
    protected Builder() {}

    protected Builder(YamlStringTranslator stringTranslator) {
      super(stringTranslator);
    }

    @Override
    protected Builder self() {
      return this;
    }

    @Override
    public YamlStringTranslator build() {
      return new YamlStringTranslator(this);
    }
  }
}
