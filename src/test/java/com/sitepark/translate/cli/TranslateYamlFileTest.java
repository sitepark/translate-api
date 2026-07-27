package com.sitepark.translate.cli;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
}
