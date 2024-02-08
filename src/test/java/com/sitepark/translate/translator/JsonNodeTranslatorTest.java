package com.sitepark.translate.translator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.sitepark.translate.SupportedProvider;
import com.sitepark.translate.TranslationConfiguration;
import com.sitepark.translate.TranslationLanguage;
import com.sitepark.translate.TranslationParameter;
import com.sitepark.translate.TranslationProvider;
import com.sitepark.translate.TranslationProviderFactory;
import com.sitepark.translate.TranslationRequest;
import com.sitepark.translate.TranslationResult;
import com.sitepark.translate.TranslationResultStatistic;
import org.junit.jupiter.api.Test;

class JsonNodeTranslatorTest {

  @Test
  void test() throws Exception {

    TranslationResult result =
        TranslationResult.builder()
            .request(mock(TranslationRequest.class))
            .text(new String[] {"Flowers", "Blue"})
            .statistic(TranslationResultStatistic.EMPTY)
            .build();

    TranslationProvider transporter = mock(TranslationProvider.class);
    when(transporter.translate(any(TranslationRequest.class))).thenReturn(result);

    TranslationProviderFactory transporterFactory = mock(TranslationProviderFactory.class);
    when(transporterFactory.create(any())).thenReturn(transporter);

    TranslationConfiguration translatorConfiguration =
        TranslationConfiguration.builder().translationProviderFactory(transporterFactory).build();

    JsonNodeFactory factory = JsonNodeFactory.instance;

    ArrayNode array = factory.arrayNode();
    ObjectNode object = factory.objectNode();
    TextNode textInObject = factory.textNode("Blume");
    TextNode textInArray = factory.textNode("Blaue");

    array.add(object);
    object.set("text", textInObject);
    array.add(textInArray);

    JsonNodeTranslator translator =
        JsonNodeTranslator.builder().translatorConfiguration(translatorConfiguration).build();

    TranslationLanguage language = TranslationLanguage.builder().source("de").target("en").build();

    TranslationParameter parameter =
        TranslationParameter.builder()
            .providerType(SupportedProvider.LIBRE_TRANSLATE)
            .language(language)
            .build();

    JsonNode translation = translator.translate(parameter, array);

    assertEquals("Flowers", translation.get(0).get("text").asText(), "wrong translation");
    assertEquals("Blue", translation.get(1).asText(), "wrong translation");
  }
}
