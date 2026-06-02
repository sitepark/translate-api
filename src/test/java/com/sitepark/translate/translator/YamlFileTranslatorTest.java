package com.sitepark.translate.translator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
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
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class YamlFileTranslatorTest {

  @Test
  @SuppressWarnings({"PMD.UseConcurrentHashMap"})
  void test() throws Exception {

    SupportedLanguages supportedLanguages =
        SupportedLanguages.builder()
            .language(Language.builder().code("de").name("deutsch").targets("en"))
            .build();

    Map<String, String> dictionary = new HashMap<>();
    dictionary.put("Hallo", "Hello");

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

    Path resources = Paths.get("src/test/resources/YamlFileTranslator");
    Path testdir = Paths.get("target/test/YamlFileTranslator");

    this.clean(testdir);
    this.copyFiles(resources, testdir);

    YamlFileTranslator yamlFileTranslator =
        YamlFileTranslator.builder()
            .dir(testdir)
            .sourceLang("de")
            .targetLangList("en")
            .translatorConfiguration(translatorConfiguration)
            .build();

    yamlFileTranslator.translate(SupportedProvider.LIBRE_TRANSLATE);

    Path resultA = testdir.resolve("a.en.yaml");
    YAMLMapper mapper = new YAMLMapper();
    String translated = mapper.readTree(resultA.toFile()).get("text").asText();
    assertEquals("Hello", translated, "wrong translation in a.en.yaml");
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
