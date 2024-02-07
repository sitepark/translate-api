package com.sitepark.translate.provider.libretranslate;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.mockito.Mockito.when;

import com.sitepark.translate.Format;
import com.sitepark.translate.SupportedProvider;
import com.sitepark.translate.TranslationConfiguration;
import com.sitepark.translate.TranslationLanguage;
import com.sitepark.translate.TranslationParameter;
import com.sitepark.translate.TranslationRequest;
import com.sitepark.translate.TranslationResult;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

@SuppressWarnings({"PMD.AvoidDuplicateLiterals", "PMD.JUnitTestContainsTooManyAsserts"})
class LibreTranslateTranslationProviderTest {

  @Test
  void testTranslation() {

    TranslationConfiguration config = Mockito.mock(TranslationConfiguration.class);
    when(config.isEncodePlaceholder()).thenReturn(true);

    LibreTranslateTranslationProvider provider =
        new HelloWorldLibreTranslateTranslationProvider(config);

    TranslationLanguage language = TranslationLanguage.builder().source("de").target("en").build();

    TranslationRequest req =
        TranslationRequest.builder()
            .parameter(
                TranslationParameter.builder()
                    .format(Format.TEXT)
                    .language(language)
                    .providerType(SupportedProvider.LIBRE_TRANSLATE)
                    .build())
            .sourceText("Hallo", "Welt")
            .build();

    TranslationResult translated = provider.translate(req);

    assertArrayEquals(
        new String[] {"Hello", "World"}, translated.getText(), "unexpected translation");
  }

  private static final class HelloWorldLibreTranslateTranslationProvider
      extends LibreTranslateTranslationProvider {

    public HelloWorldLibreTranslateTranslationProvider(
        TranslationConfiguration translatorConfiguration) {
      super(translatorConfiguration);
    }

    @Override
    protected String[] translationRequest(TranslationParameter parameter, String... source)
        throws IOException, InterruptedException {
      return new String[] {"Hello", "World"};
    }
  }
}
