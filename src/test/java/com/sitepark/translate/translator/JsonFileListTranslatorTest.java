package com.sitepark.translate.translator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sitepark.translate.Language;
import com.sitepark.translate.LocaleFormat;
import com.sitepark.translate.SupportedLanguages;
import com.sitepark.translate.SupportedProvider;
import com.sitepark.translate.TranslationConfiguration;
import com.sitepark.translate.TranslationProvider;
import com.sitepark.translate.TranslationProviderFactory;
import com.sitepark.translate.TranslationRequest;
import com.sitepark.translate.TranslationResult;
import com.sitepark.translate.TranslationResultStatistic;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class JsonFileListTranslatorTest {

  @Test
  @SuppressWarnings({"PMD.UseConcurrentHashMap", "PMD.UnitTestContainsTooManyAsserts"})
  void test() throws Exception {

    SupportedLanguages supportedLanguages =
        SupportedLanguages.builder()
            .language(Language.builder().code("de").name("deutsch").targets("en"))
            .build();

    Map<String, String> dictionary = new HashMap<>();
    dictionary.put("Hallo", "Hello");
    dictionary.put("Welt", "World");

    TranslationProvider transporter = mock(TranslationProvider.class);
    when(transporter.translate(any(TranslationRequest.class)))
        .thenAnswer(
            invocationOnMock -> {
              TranslationRequest req = (TranslationRequest) invocationOnMock.getArguments()[0];
              String[] sourceText = req.getSourceText();
              String[] translations = new String[sourceText.length];
              for (int i = 0; i < translations.length; i++) {
                translations[i] = dictionary.get(sourceText[i]);
              }

              return TranslationResult.builder()
                  .request(req)
                  .text(translations)
                  .statistic(TranslationResultStatistic.EMPTY)
                  .build();
            });
    when(transporter.getSupportedLanguages()).thenReturn(supportedLanguages);

    TranslationProviderFactory transporterFactory = mock(TranslationProviderFactory.class);
    when(transporterFactory.create(any())).thenReturn(transporter);

    TranslationConfiguration translatorConfiguration =
        TranslationConfiguration.builder().translationProviderFactory(transporterFactory).build();

    Path dir = Paths.get("src/test/resources/JsonFileListTranslator");
    Path output = Paths.get("target/test/JsonFileListTranslator/translations");
    this.cleanCache(output);

    JsonFileListTranslator jsonFileListTranslator =
        JsonFileListTranslator.builder()
            .dir(dir)
            .output(output)
            .sourceLang("de")
            .targetLangList("en")
            .translatorConfiguration(translatorConfiguration)
            .build();

    jsonFileListTranslator.translate(SupportedProvider.LIBRE_TRANSLATE);

    Path resultA = output.resolve("en/a.json");
    assertEquals(
        "{\n" + "  \"text\" : \"Hello\"\n" + "}",
        Files.readString(resultA, StandardCharsets.UTF_8),
        "wrong content in en/a.json");

    Path resultC = output.resolve("en/b/c.json");
    assertEquals(
        "{\n" + "  \"d\" : \"World\"\n" + "}",
        Files.readString(resultC, StandardCharsets.UTF_8),
        "wrong content in en/b/c.json");
  }

  @Test
  @SuppressWarnings({"PMD.UseConcurrentHashMap", "PMD.UnitTestContainsTooManyAsserts"})
  void testWithIcuLocaleFormat() throws Exception {

    SupportedLanguages supportedLanguages =
        SupportedLanguages.builder()
            .language(Language.builder().code("de").name("deutsch").targets("zh-hans", "en-us"))
            .build();

    Map<String, String> dictionary = new HashMap<>();
    dictionary.put("Hallo", "Hello");
    dictionary.put("Welt", "World");

    TranslationConfiguration translatorConfiguration =
        this.createTranslatorConfiguration(supportedLanguages, dictionary);

    Path dir = Paths.get("src/test/resources/JsonFileListTranslator");
    Path output = Paths.get("target/test/JsonFileListTranslatorIcu/translations");
    this.cleanCache(output);

    JsonFileListTranslator jsonFileListTranslator =
        JsonFileListTranslator.builder()
            .dir(dir)
            .output(output)
            .sourceLang("de")
            .localeFormat(LocaleFormat.ICU)
            .translatorConfiguration(translatorConfiguration)
            .build();

    jsonFileListTranslator.translate(SupportedProvider.LIBRE_TRANSLATE);

    assertEquals(
        "{\n" + "  \"d\" : \"World\"\n" + "}",
        Files.readString(output.resolve("zh_Hans/b/c.json"), StandardCharsets.UTF_8),
        "wrong content in zh_Hans/b/c.json");
    assertTrue(Files.exists(output.resolve("en_US/a.json")), "en-us must be written to en_US");
    assertTrue(Files.exists(output.resolve("en/a.json")), "en_US must be copied to en");
    assertFalse(Files.exists(output.resolve("zh-hans/a.json")), "provider code must not be used");
    assertTrue(
        Files.exists(output.resolve("zh-hans/.translation-cache-file")),
        "cache must stay under the provider code");
  }

  private TranslationConfiguration createTranslatorConfiguration(
      SupportedLanguages supportedLanguages, Map<String, String> dictionary) {

    TranslationProvider transporter = mock(TranslationProvider.class);
    when(transporter.translate(any(TranslationRequest.class)))
        .thenAnswer(
            invocationOnMock -> {
              TranslationRequest req = (TranslationRequest) invocationOnMock.getArguments()[0];
              String[] sourceText = req.getSourceText();
              String[] translations = new String[sourceText.length];
              for (int i = 0; i < translations.length; i++) {
                translations[i] = dictionary.get(sourceText[i]);
              }

              return TranslationResult.builder()
                  .request(req)
                  .text(translations)
                  .statistic(TranslationResultStatistic.EMPTY)
                  .build();
            });
    when(transporter.getSupportedLanguages()).thenReturn(supportedLanguages);

    TranslationProviderFactory transporterFactory = mock(TranslationProviderFactory.class);
    when(transporterFactory.create(any())).thenReturn(transporter);

    return TranslationConfiguration.builder()
        .translationProviderFactory(transporterFactory)
        .build();
  }

  private void cleanCache(Path dir) throws IOException {
    if (!Files.exists(dir)) {
      return;
    }
    Files.walk(dir).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
  }
}
