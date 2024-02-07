package com.sitepark.translate.translator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sitepark.translate.SupportedProvider;
import com.sitepark.translate.TranslationCache;
import com.sitepark.translate.TranslationConfiguration;
import com.sitepark.translate.TranslationLanguage;
import com.sitepark.translate.TranslationParameter;
import com.sitepark.translate.TranslationProvider;
import com.sitepark.translate.TranslationProviderFactory;
import com.sitepark.translate.TranslationRequest;
import com.sitepark.translate.TranslationResult;
import com.sitepark.translate.TranslationResultStatistic;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class TranslatableTextListTranslatorTest {

  @Test
  @SuppressWarnings("PMD.AvoidDuplicateLiterals")
  void test() throws Exception {

    TranslationResult translationResult =
        TranslationResult.builder()
            .request(mock(TranslationRequest.class))
            .text(new String[] {"Flowers", "Blue"})
            .statistic(TranslationResultStatistic.EMPTY)
            .build();

    TranslationProvider transporter = mock(TranslationProvider.class);
    when(transporter.translate(any(TranslationRequest.class))).thenReturn(translationResult);

    TranslationProviderFactory transporterFactory = mock(TranslationProviderFactory.class);
    when(transporterFactory.create(any())).thenReturn(transporter);

    TranslationConfiguration translatorConfiguration =
        TranslationConfiguration.builder().translationProviderFactory(transporterFactory).build();

    List<TranslatableText> translatableTextList = new ArrayList<>();
    translatableTextList.add(new TranslatableText("Blume"));
    translatableTextList.add(new TranslatableText("Blau"));

    TranslatableTextListTranslator translator =
        TranslatableTextListTranslator.builder()
            .translatorConfiguration(translatorConfiguration)
            .build();

    TranslationLanguage language = TranslationLanguage.builder().source("de").target("en").build();

    TranslationParameter parameter =
        TranslationParameter.builder()
            .language(language)
            .providerType(SupportedProvider.LIBRE_TRANSLATE)
            .build();

    translator.translate(parameter, translatableTextList);

    assertEquals("Flowers", translatableTextList.get(0).getTargetText(), "unexpected translation");
    assertEquals("Blue", translatableTextList.get(1).getTargetText(), "unexpected translation");
  }

  @Test
  void testWithCache() throws Exception {

    TranslationResult translationResult =
        TranslationResult.builder()
            .request(mock(TranslationRequest.class))
            .text(new String[] {"Blue"})
            .statistic(TranslationResultStatistic.EMPTY)
            .build();

    TranslationProvider transporter = mock(TranslationProvider.class);
    when(transporter.translate(any(TranslationRequest.class))).thenReturn(translationResult);

    TranslationCache translationCache = mock(TranslationCache.class);
    // last match wins
    when(translationCache.translate(any())).thenReturn(Optional.empty());
    when(translationCache.translate("Blume")).thenReturn(Optional.of("Flowers"));

    TranslationProviderFactory transporterFactory = mock(TranslationProviderFactory.class);
    when(transporterFactory.create(any())).thenReturn(transporter);

    TranslationConfiguration translatorConfiguration =
        TranslationConfiguration.builder()
            .translationProviderFactory(transporterFactory)
            .translationCache(translationCache)
            .build();

    List<TranslatableText> translatableTextList = new ArrayList<>();
    translatableTextList.add(new TranslatableText("Blume"));
    translatableTextList.add(new TranslatableText("Blau"));

    TranslatableTextListTranslator translator =
        TranslatableTextListTranslator.builder()
            .translatorConfiguration(translatorConfiguration)
            .build();

    TranslationLanguage language = TranslationLanguage.builder().source("de").target("en").build();

    TranslationParameter parameter =
        TranslationParameter.builder()
            .language(language)
            .providerType(SupportedProvider.LIBRE_TRANSLATE)
            .build();

    translator.translate(parameter, translatableTextList);

    assertEquals("Flowers", translatableTextList.get(0).getTargetText(), "unexpected translation");
    assertEquals("Blue", translatableTextList.get(1).getTargetText(), "unexpected translation");
  }
}
