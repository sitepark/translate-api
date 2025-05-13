package com.sitepark.translate.translator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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

class JsonStringTranslatorTest {

  @Test
  void test() throws Exception {

    TranslationResult translationResult =
        TranslationResult.builder()
            .request(mock(TranslationRequest.class))
            .text("Heading", "A beautiful text", "Flowers")
            .statistic(TranslationResultStatistic.EMPTY)
            .build();

    TranslationProvider transporter = mock(TranslationProvider.class);
    when(transporter.translate(any(TranslationRequest.class))).thenReturn(translationResult);

    TranslationProviderFactory transporterFactory = mock(TranslationProviderFactory.class);
    when(transporterFactory.create(any())).thenReturn(transporter);

    TranslationConfiguration translatorConfiguration =
        TranslationConfiguration.builder().translationProviderFactory(transporterFactory).build();

    JsonStringTranslator translator =
        JsonStringTranslator.builder().translatorConfiguration(translatorConfiguration).build();

    TranslationLanguage language = TranslationLanguage.builder().source("de").target("en").build();

    TranslationParameter parameter =
        TranslationParameter.builder()
            .providerType(SupportedProvider.LIBRE_TRANSLATE)
            .language(language)
            .build();

    String result =
        translator.translate(
            parameter,
            "{\n"
                + "    \"headline\":\"Überschrift\",\n"
                + "    \"text\":\"Ein schöner Text\",\n"
                + "    \"items\" : [\n"
                + "        {\n"
                + "            \"text\" : \"Blumen\",\n"
                + "            \"number\" : 10,\n"
                + "            \"boolean\" :true\n"
                + "        }\n"
                + "    ]\n"
                + "}");

    String expected =
        "{\"headline\":\"Heading\","
            + "\"text\":\"A beautiful text\","
            + "\"items\":[{\"text\":\"Flowers\",\"number\":10,\"boolean\":true}]}";

    assertEquals(expected, result, "unexpected json data");
  }
}
