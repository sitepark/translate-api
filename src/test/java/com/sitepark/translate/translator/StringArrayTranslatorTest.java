package com.sitepark.translate.translator;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sitepark.translate.Format;
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

class StringArrayTranslatorTest {

  @Test
  void test() throws Exception {

    TranslationResult translationResult =
        TranslationResult.builder()
            .request(mock(TranslationRequest.class))
            .text("Hello", "World")
            .statistic(TranslationResultStatistic.EMPTY)
            .build();

    TranslationProvider transporter = mock(TranslationProvider.class);
    when(transporter.translate(any(TranslationRequest.class))).thenReturn(translationResult);

    TranslationProviderFactory transporterFactory = mock(TranslationProviderFactory.class);
    when(transporterFactory.create(any())).thenReturn(transporter);

    TranslationConfiguration translatorConfiguration =
        TranslationConfiguration.builder().translationProviderFactory(transporterFactory).build();

    StringArrayTranslator translator =
        StringArrayTranslator.builder().translatorConfiguration(translatorConfiguration).build();

    TranslationLanguage language = TranslationLanguage.builder().source("de").target("en").build();

    TranslationParameter parameter =
        TranslationParameter.builder()
            .format(Format.TEXT)
            .language(language)
            .providerType(SupportedProvider.LIBRE_TRANSLATE)
            .build();

    TranslationRequest req =
        TranslationRequest.builder().parameter(parameter).sourceText("Hallo", "Welt").build();

    TranslationResult result = translator.translate(req);

    assertArrayEquals(new String[] {"Hello", "World"}, result.getText(), "wrong translation");
  }
}
