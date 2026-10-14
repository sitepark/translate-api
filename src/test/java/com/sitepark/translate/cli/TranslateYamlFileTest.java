package com.sitepark.translate.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.sitepark.translate.LocaleFormat;
import java.util.List;
import org.junit.jupiter.api.Test;

class TranslateYamlFileTest {

  @Test
  void testParseArgumentsWithDeepl() {
    TranslateYamlFile translateYamlFile = new TranslateYamlFile();
    translateYamlFile.parseArguments(
        "deepl:https://dummy?authKey=abc", "src/test/resources/translate-yaml", "de");
    assertNotNull(translateYamlFile.getTranslator(), "translator expected");
  }

  @Test
  void testParseArgumentsWithLibreTranslate() {
    TranslateYamlFile translateYamlFile = new TranslateYamlFile();
    translateYamlFile.parseArguments(
        "libretranslate:https://dummy?apiKey=abc", "src/test/resources/translate-yaml", "de");
    assertNotNull(translateYamlFile.getTranslator(), "translator expected");
  }

  @Test
  void testParseArgumentsWithLibreTranslateWithoutQuery() {
    TranslateYamlFile translateYamlFile = new TranslateYamlFile();
    translateYamlFile.parseArguments(
        "libretranslate:https://dummy", "src/test/resources/translate-yaml", "de");
    assertNotNull(translateYamlFile.getTranslator(), "translator expected");
  }

  @Test
  void testParseArgumentsWithOutputDir() {
    TranslateYamlFile translateYamlFile = new TranslateYamlFile();
    translateYamlFile.parseArguments(
        "deepl:https://dummy?authKey=abc",
        "src/test/resources/translate-yaml",
        "de",
        "target/test/translate-yaml-generated");
    assertNotNull(translateYamlFile.getTranslator(), "translator expected");
  }

  @Test
  void testParseArgumentsWithTargetLanguages() {
    TranslateYamlFile translateYamlFile = new TranslateYamlFile();
    translateYamlFile.parseArguments(
        "deepl:https://dummy?authKey=abc",
        "src/test/resources/translate-yaml",
        "de",
        "target/test/translate-yaml-target-langs",
        "en",
        "fr");
    assertNotNull(translateYamlFile.getTranslator(), "translator expected");
  }

  @Test
  void testParseArgumentsWithMissingArguments() {
    TranslateYamlFile translateYamlFile = new TranslateYamlFile();
    assertThrows(
        IllegalArgumentException.class,
        () -> translateYamlFile.parseArguments("deepl:https://dummy?authKey=abc"));
  }

  @Test
  void testParseArgumentsWithNonExistingDir() {
    TranslateYamlFile translateYamlFile = new TranslateYamlFile();
    assertThrows(
        IllegalArgumentException.class,
        () ->
            translateYamlFile.parseArguments(
                "deepl:https://dummy?authKey=abc", "src/test/resources/does-not-exist", "de"));
  }

  @Test
  void testParseArgumentsWithMalformedUrl() {
    TranslateYamlFile translateYamlFile = new TranslateYamlFile();
    assertThrows(
        IllegalArgumentException.class,
        () ->
            translateYamlFile.parseArguments(
                "deepl:https://dummy?authKey=abc def", "src/test/resources/translate-yaml", "de"));
  }

  @Test
  void testParseArgumentsWithoutLocaleFormatUsesDefault() {
    TranslateYamlFile translateYamlFile = new TranslateYamlFile();
    translateYamlFile.parseArguments(
        "deepl:https://dummy?authKey=abc", "src/test/resources/translate-yaml", "de");
    assertEquals(
        LocaleFormat.PROVIDER, translateYamlFile.getLocaleFormat(), "default format expected");
  }

  @Test
  void testParseArgumentsWithLocaleFormatFirst() {
    TranslateYamlFile translateYamlFile = new TranslateYamlFile();
    translateYamlFile.parseArguments(
        "--output-locale-format=icu",
        "deepl:https://dummy?authKey=abc",
        "src/test/resources/translate-yaml",
        "de");
    assertEquals(LocaleFormat.ICU, translateYamlFile.getLocaleFormat(), "icu format expected");
  }

  @Test
  @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
  void testParseArgumentsWithLocaleFormatBetweenTargets() {
    TranslateYamlFile translateYamlFile = new TranslateYamlFile();
    translateYamlFile.parseArguments(
        "deepl:https://dummy?authKey=abc",
        "src/test/resources/translate-yaml",
        "de",
        "target/test/translate-yaml-generated",
        "en_GB",
        "--output-locale-format=BCP47",
        "pt-br");
    assertEquals(LocaleFormat.BCP47, translateYamlFile.getLocaleFormat(), "bcp47 format expected");
    assertEquals(
        List.of("en_GB", "pt-br"),
        translateYamlFile.getTargetLanguages(),
        "option must not be taken as target language");
  }

  @Test
  void testParseArgumentsWithUnknownLocaleFormat() {
    TranslateYamlFile translateYamlFile = new TranslateYamlFile();
    assertThrows(
        IllegalArgumentException.class,
        () ->
            translateYamlFile.parseArguments(
                "--output-locale-format=symfony",
                "deepl:https://dummy?authKey=abc",
                "src/test/resources/translate-yaml",
                "de"));
  }

  @Test
  void testParseArgumentsWithUnknownOption() {
    TranslateYamlFile translateYamlFile = new TranslateYamlFile();
    assertThrows(
        IllegalArgumentException.class,
        () ->
            translateYamlFile.parseArguments(
                "--locale-format=icu",
                "deepl:https://dummy?authKey=abc",
                "src/test/resources/translate-yaml",
                "de"));
  }
}
