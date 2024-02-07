package com.sitepark.translate;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.sitepark.translate.provider.deepl.DeeplTranslationProvider;
import com.sitepark.translate.provider.libretranslate.LibreTranslateTranslationProvider;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class TranslationProviderFactoryTest {

  @Test
  void testCreateLibreTranslateTranslationProvider() {

    TranslationConfiguration config = Mockito.mock(TranslationConfiguration.class);
    TranslationProviderFactory factory = new TranslationProviderFactory(config);
    TranslationProvider provider = factory.create(SupportedProvider.LIBRE_TRANSLATE);
    assertThat(
        "libretranslate provider expected",
        provider,
        instanceOf(LibreTranslateTranslationProvider.class));
  }

  @Test
  void testCreateDeeplTranslationProvider() {

    TranslationConfiguration config = Mockito.mock(TranslationConfiguration.class);
    TranslationProviderFactory factory = new TranslationProviderFactory(config);
    TranslationProvider provider = factory.create(SupportedProvider.DEEPL);
    assertThat("deepl provider expected", provider, instanceOf(DeeplTranslationProvider.class));
  }

  @Test
  void testInvalidTranslationProvider() {

    TranslationConfiguration config = Mockito.mock(TranslationConfiguration.class);
    TranslationProviderFactory factory = new TranslationProviderFactory(config);
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          factory.create(null);
        },
        "IllegalArgumentException expected");
  }
}
