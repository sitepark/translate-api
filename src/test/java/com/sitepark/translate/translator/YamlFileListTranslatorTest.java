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
class YamlFileListTranslatorTest {

  @Test
  @SuppressWarnings({"PMD.UnitTestContainsTooManyAsserts"})
  void test() throws Exception {

    SupportedLanguages supportedLanguages =
        SupportedLanguages.builder()
            .language(Language.builder().code("de").name("deutsch").targets("en"))
            .build();

    Map<String, String> dictionary = new HashMap<>();
    dictionary.put("Hallo", "Hello");
    dictionary.put("Welt", "World");

    TranslationConfiguration translatorConfiguration =
        this.createTranslatorConfiguration(supportedLanguages, dictionary);

    Path dir = Paths.get("src/test/resources/YamlFileListTranslator");
    Path output = Paths.get("target/test/YamlFileListTranslator/translations");
    this.clean(output);

    YamlFileListTranslator yamlFileListTranslator =
        YamlFileListTranslator.builder()
            .dir(dir)
            .output(output)
            .sourceLang("de")
            .targetLangList("en")
            .translatorConfiguration(translatorConfiguration)
            .build();

    yamlFileListTranslator.translate(SupportedProvider.LIBRE_TRANSLATE);

    YAMLMapper mapper = new YAMLMapper();

    Path resultA = output.resolve("en/a.yaml");
    String translatedA = mapper.readTree(resultA.toFile()).get("text").asText();
    assertEquals("Hello", translatedA, "wrong translation in en/a.yaml");

    Path resultC = output.resolve("en/b/c.yaml");
    String translatedC = mapper.readTree(resultC.toFile()).get("d").asText();
    assertEquals("World", translatedC, "wrong translation in en/b/c.yaml");
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
    dictionary.put("Welt", "World");

    TranslationConfiguration translatorConfiguration =
        this.createTranslatorConfiguration(supportedLanguages, dictionary);

    Path dir = Paths.get("src/test/resources/YamlFileListTranslator");
    Path output = Paths.get("target/test/YamlFileListTranslatorEnUs/translations");
    this.clean(output);

    YamlFileListTranslator yamlFileListTranslator =
        YamlFileListTranslator.builder()
            .dir(dir)
            .output(output)
            .sourceLang("de")
            .translatorConfiguration(translatorConfiguration)
            .build();

    yamlFileListTranslator.translate(SupportedProvider.LIBRE_TRANSLATE, List.of("en-us"));
    // run again so the "en" output already exists, exercising the copy-skip-if-exists path
    yamlFileListTranslator.translate(SupportedProvider.LIBRE_TRANSLATE, List.of("en-us"));

    YAMLMapper mapper = new YAMLMapper();

    Path resultEnUs = output.resolve("en-us/a.yaml");
    String translatedEnUs = mapper.readTree(resultEnUs.toFile()).get("text").asText();
    assertEquals("Hello", translatedEnUs, "wrong translation in en-us/a.yaml");

    Path resultEn = output.resolve("en/a.yaml");
    String translatedEn = mapper.readTree(resultEn.toFile()).get("text").asText();
    assertEquals("Hello", translatedEn, "en-us must be copied to en");
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
    dictionary.put("Welt", "World");

    TranslationConfiguration translatorConfiguration =
        this.createTranslatorConfiguration(supportedLanguages, dictionary);

    Path dir = Paths.get("src/test/resources/YamlFileListTranslator");
    Path output = Paths.get("target/test/YamlFileListTranslatorSkip/translations");
    this.clean(output);

    YamlFileListTranslator yamlFileListTranslator =
        YamlFileListTranslator.builder()
            .dir(dir)
            .output(output)
            .sourceLang("de")
            .targetLangList(Set.of("en"))
            .translatorConfiguration(translatorConfiguration)
            .build();

    yamlFileListTranslator.translate(SupportedProvider.LIBRE_TRANSLATE);

    assertEquals(true, Files.exists(output.resolve("en/a.yaml")), "en must be translated");
    assertEquals(
        false, Files.exists(output.resolve("de/a.yaml")), "de must be skipped as source lang");
    assertEquals(
        false, Files.exists(output.resolve("fr/a.yaml")), "fr must be skipped, not in targetList");
  }

  @Test
  void testWithUnsupportedSourceLanguageThrows() {

    SupportedLanguages supportedLanguages =
        SupportedLanguages.builder()
            .language(Language.builder().code("de").name("deutsch").targets("en"))
            .build();

    TranslationConfiguration translatorConfiguration =
        this.createTranslatorConfiguration(supportedLanguages, Map.of());

    Path dir = Paths.get("src/test/resources/YamlFileListTranslator");

    YamlFileListTranslator yamlFileListTranslator =
        YamlFileListTranslator.builder()
            .dir(dir)
            .output(Paths.get("target/test/YamlFileListTranslatorUnsupported/translations"))
            .sourceLang("xx")
            .translatorConfiguration(translatorConfiguration)
            .build();

    assertThrows(
        IllegalArgumentException.class,
        () -> yamlFileListTranslator.translate(SupportedProvider.LIBRE_TRANSLATE));
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

    Path dir = Paths.get("target/test/YamlFileListTranslatorExcludes");
    this.clean(dir);
    Files.createDirectories(dir.resolve("de"));
    Files.writeString(dir.resolve("de/a.yaml"), "text: \"Hallo\"\n");
    Files.writeString(dir.resolve("de.excludes"), "a.text\n");

    Path output = dir.resolve("translations");

    YamlFileListTranslator yamlFileListTranslator =
        YamlFileListTranslator.builder()
            .dir(dir)
            .output(output)
            .sourceLang("de")
            .targetLangList("en")
            .translatorConfiguration(translatorConfiguration)
            .build();

    yamlFileListTranslator.translate(SupportedProvider.LIBRE_TRANSLATE);

    YAMLMapper mapper = new YAMLMapper();
    String result = mapper.readTree(output.resolve("en/a.yaml").toFile()).get("text").asText();
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

    Path dir = Paths.get("target/test/YamlFileListTranslatorMalformed");
    this.clean(dir);
    Files.createDirectories(dir.resolve("de"));
    Files.writeString(dir.resolve("de/a.yaml"), "text: [unclosed\n");

    YamlFileListTranslator yamlFileListTranslator =
        YamlFileListTranslator.builder()
            .dir(dir)
            .output(dir.resolve("translations"))
            .sourceLang("de")
            .targetLangList("en")
            .translatorConfiguration(translatorConfiguration)
            .build();

    assertThrows(
        TranslatorException.class,
        () -> yamlFileListTranslator.translate(SupportedProvider.LIBRE_TRANSLATE));
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

    Path dir = Paths.get("target/test/YamlFileListTranslatorBadCache");
    this.clean(dir);
    Files.createDirectories(dir.resolve("de"));
    Files.writeString(dir.resolve("de/a.yaml"), "text: \"Hallo\"\n");

    Path output = dir.resolve("translations");
    Files.createDirectories(output.resolve("en/.translation-cache-file"));

    YamlFileListTranslator yamlFileListTranslator =
        YamlFileListTranslator.builder()
            .dir(dir)
            .output(output)
            .sourceLang("de")
            .targetLangList("en")
            .translatorConfiguration(translatorConfiguration)
            .build();

    yamlFileListTranslator.translate(SupportedProvider.LIBRE_TRANSLATE);

    YAMLMapper mapper = new YAMLMapper();
    String result = mapper.readTree(output.resolve("en/a.yaml").toFile()).get("text").asText();
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
}
