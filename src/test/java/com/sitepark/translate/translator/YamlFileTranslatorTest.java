package com.sitepark.translate.translator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

@SuppressWarnings("PMD.UseConcurrentHashMap")
class YamlFileTranslatorTest {

  @Test
  void test() throws Exception {

    SupportedLanguages supportedLanguages =
        SupportedLanguages.builder()
            .language(Language.builder().code("de").name("deutsch").targets("en"))
            .build();

    Map<String, String> dictionary = new HashMap<>();
    dictionary.put("Hallo", "Hello");

    TranslationConfiguration translatorConfiguration =
        this.createTranslatorConfiguration(supportedLanguages, dictionary);

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

  @Test
  @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
  void testWithExplicitTargetsAndEnUsCopy() throws Exception {

    SupportedLanguages supportedLanguages =
        SupportedLanguages.builder()
            .language(Language.builder().code("de").name("deutsch").targets("en-us"))
            .build();

    Map<String, String> dictionary = new HashMap<>();
    dictionary.put("Hallo", "Hello");

    TranslationConfiguration translatorConfiguration =
        this.createTranslatorConfiguration(supportedLanguages, dictionary);

    Path resources = Paths.get("src/test/resources/YamlFileTranslator");
    Path testdir = Paths.get("target/test/YamlFileTranslatorEnUs");

    this.clean(testdir);
    this.copyFiles(resources, testdir);

    YamlFileTranslator yamlFileTranslator =
        YamlFileTranslator.builder()
            .dir(testdir)
            .sourceLang("de")
            .translatorConfiguration(translatorConfiguration)
            .build();

    yamlFileTranslator.translate(SupportedProvider.LIBRE_TRANSLATE, List.of("en-us"));

    YAMLMapper mapper = new YAMLMapper();

    String translatedEnUs =
        mapper.readTree(testdir.resolve("a.en-us.yaml").toFile()).get("text").asText();
    assertEquals("Hello", translatedEnUs, "wrong translation in a.en-us.yaml");

    String translatedEn =
        mapper.readTree(testdir.resolve("a.en.yaml").toFile()).get("text").asText();
    assertEquals("Hello", translatedEn, "en-us must also be written as en");
  }

  @Test
  @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
  void testSkipsSourceLangAndUnwantedTargets() throws Exception {

    SupportedLanguages supportedLanguages =
        SupportedLanguages.builder()
            .language(Language.builder().code("de").name("deutsch").targets("en", "de", "fr"))
            .build();

    Map<String, String> dictionary = new HashMap<>();
    dictionary.put("Hallo", "Hello");

    TranslationConfiguration translatorConfiguration =
        this.createTranslatorConfiguration(supportedLanguages, dictionary);

    Path resources = Paths.get("src/test/resources/YamlFileTranslator");
    Path testdir = Paths.get("target/test/YamlFileTranslatorSkip");

    this.clean(testdir);
    this.copyFiles(resources, testdir);

    YamlFileTranslator yamlFileTranslator =
        YamlFileTranslator.builder()
            .dir(testdir)
            .sourceLang("de")
            .targetLangList(Set.of("en"))
            .translatorConfiguration(translatorConfiguration)
            .build();

    yamlFileTranslator.translate(SupportedProvider.LIBRE_TRANSLATE);

    assertEquals(true, Files.exists(testdir.resolve("a.en.yaml")), "en must be translated");
    assertEquals(
        false, Files.exists(testdir.resolve("a.fr.yaml")), "fr must be skipped, not in targetList");
  }

  @Test
  void testWithUnsupportedSourceLanguageThrows() {

    SupportedLanguages supportedLanguages =
        SupportedLanguages.builder()
            .language(Language.builder().code("de").name("deutsch").targets("en"))
            .build();

    TranslationConfiguration translatorConfiguration =
        this.createTranslatorConfiguration(supportedLanguages, Map.of());

    YamlFileTranslator yamlFileTranslator =
        YamlFileTranslator.builder()
            .dir(Paths.get("src/test/resources/YamlFileTranslator"))
            .sourceLang("xx")
            .translatorConfiguration(translatorConfiguration)
            .build();

    assertThrows(
        IllegalArgumentException.class,
        () -> yamlFileTranslator.translate(SupportedProvider.LIBRE_TRANSLATE));
  }

  @Test
  void testWithExcludes() throws Exception {

    SupportedLanguages supportedLanguages =
        SupportedLanguages.builder()
            .language(Language.builder().code("de").name("deutsch").targets("en"))
            .build();

    Map<String, String> dictionary = new HashMap<>();
    dictionary.put("Hallo", "Hello");

    TranslationConfiguration translatorConfiguration =
        this.createTranslatorConfiguration(supportedLanguages, dictionary);

    Path testdir = Paths.get("target/test/YamlFileTranslatorExcludes");
    this.clean(testdir);
    Files.createDirectories(testdir);
    Files.writeString(testdir.resolve("a.de.yaml"), "text: \"Hallo\"\n");
    Files.writeString(testdir.resolve("de.excludes"), "a.de.text\n");

    YamlFileTranslator yamlFileTranslator =
        YamlFileTranslator.builder()
            .dir(testdir)
            .sourceLang("de")
            .targetLangList("en")
            .translatorConfiguration(translatorConfiguration)
            .build();

    yamlFileTranslator.translate(SupportedProvider.LIBRE_TRANSLATE);

    YAMLMapper mapper = new YAMLMapper();
    String result = mapper.readTree(testdir.resolve("a.en.yaml").toFile()).get("text").asText();
    assertEquals("Hallo", result, "excluded text must not be translated");
  }

  @Test
  void testWithMalformedYamlThrows() throws Exception {

    SupportedLanguages supportedLanguages =
        SupportedLanguages.builder()
            .language(Language.builder().code("de").name("deutsch").targets("en"))
            .build();

    TranslationConfiguration translatorConfiguration =
        this.createTranslatorConfiguration(supportedLanguages, Map.of());

    Path testdir = Paths.get("target/test/YamlFileTranslatorMalformed");
    this.clean(testdir);
    Files.createDirectories(testdir);
    Files.writeString(testdir.resolve("a.de.yaml"), "text: [unclosed\n");

    YamlFileTranslator yamlFileTranslator =
        YamlFileTranslator.builder()
            .dir(testdir)
            .sourceLang("de")
            .targetLangList("en")
            .translatorConfiguration(translatorConfiguration)
            .build();

    assertThrows(
        TranslatorException.class,
        () -> yamlFileTranslator.translate(SupportedProvider.LIBRE_TRANSLATE));
  }

  @Test
  void testWithUnreadableCache() throws Exception {

    SupportedLanguages supportedLanguages =
        SupportedLanguages.builder()
            .language(Language.builder().code("de").name("deutsch").targets("en"))
            .build();

    Map<String, String> dictionary = new HashMap<>();
    dictionary.put("Hallo", "Hello");

    TranslationConfiguration translatorConfiguration =
        this.createTranslatorConfiguration(supportedLanguages, dictionary);

    Path testdir = Paths.get("target/test/YamlFileTranslatorBadCache");
    this.clean(testdir);
    Files.createDirectories(testdir);
    Files.writeString(testdir.resolve("a.de.yaml"), "text: \"Hallo\"\n");
    Files.createDirectories(testdir.resolve(".translation-cache/en"));

    YamlFileTranslator yamlFileTranslator =
        YamlFileTranslator.builder()
            .dir(testdir)
            .sourceLang("de")
            .targetLangList("en")
            .translatorConfiguration(translatorConfiguration)
            .build();

    yamlFileTranslator.translate(SupportedProvider.LIBRE_TRANSLATE);

    YAMLMapper mapper = new YAMLMapper();
    String result = mapper.readTree(testdir.resolve("a.en.yaml").toFile()).get("text").asText();
    assertEquals("Hello", result, "translation should still succeed despite cache error");
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
