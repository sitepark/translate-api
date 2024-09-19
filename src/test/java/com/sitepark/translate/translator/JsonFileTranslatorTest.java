package com.sitepark.translate.translator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sitepark.translate.Language;
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
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class JsonFileTranslatorTest {

  @Test
  @SuppressWarnings({"PMD.UseConcurrentHashMap"})
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

    Path resources = Paths.get("src/test/resources/JsonFileTranslator");
    Path testdir = Paths.get("target/test/JsonFileTranslator");

    this.clean(testdir);
    this.copyFiles(resources, testdir);

    JsonFileTranslator jsonFileListTranslator =
        JsonFileTranslator.builder()
            .dir(testdir)
            .sourceLang("de")
            .targetLangList("en")
            .translatorConfiguration(translatorConfiguration)
            .build();

    jsonFileListTranslator.translate(SupportedProvider.LIBRE_TRANSLATE);

    Path resultA = testdir.resolve("a.en.json");
    assertEquals(
        "{\n" + "  \"text\" : \"Hello\"\n" + "}",
        Files.readString(resultA, StandardCharsets.UTF_8),
        "wrong content in en/a.json");
  }

  private void clean(Path dir) throws IOException {
    if (!Files.exists(dir)) {
      return;
    }
    Files.walk(dir).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
  }

  private void copyFiles(Path source, Path destination) throws IOException {
    Files.createDirectories(destination);
    try (DirectoryStream<Path> stream = Files.newDirectoryStream(source); ) {
      for (Path file : stream) {
        Files.copy(file, destination.resolve(file.getFileName()));
      }
    }
  }
}
