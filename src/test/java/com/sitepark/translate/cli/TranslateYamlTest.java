package com.sitepark.translate.cli;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class TranslateYamlTest {

  @Test
  void testParseArgumentsWithDeepl() {
    TranslateYaml translateYaml = new TranslateYaml();
    translateYaml.parseArguments(
        "deepl:https://dummy?authKey=abc",
        "src/test/resources/translate-yaml",
        "de",
        "target/test/translate-yaml");
    assertNotNull(translateYaml.getTranslator(), "translator expected");
  }

  @Test
  void testParseArgumentsWithLibreTranslate() {
    TranslateYaml translateYaml = new TranslateYaml();
    translateYaml.parseArguments(
        "libretranslate:https://dummy?apiKey=abc",
        "src/test/resources/translate-yaml",
        "de",
        "target/test/translate-yaml");
    assertNotNull(translateYaml.getTranslator(), "translator expected");
  }

  @Test
  void testParseArgumentsWithLibreTranslateWithoutQuery() {
    TranslateYaml translateYaml = new TranslateYaml();
    translateYaml.parseArguments(
        "libretranslate:https://dummy",
        "src/test/resources/translate-yaml",
        "de",
        "target/test/translate-yaml");
    assertNotNull(translateYaml.getTranslator(), "translator expected");
  }

  @Test
  void testParseArgumentsWithTargetLanguages() {
    TranslateYaml translateYaml = new TranslateYaml();
    translateYaml.parseArguments(
        "deepl:https://dummy?authKey=abc",
        "src/test/resources/translate-yaml",
        "de",
        "target/test/translate-yaml",
        "en",
        "fr");
    assertNotNull(translateYaml.getTranslator(), "translator expected");
  }

  @Test
  void testParseArgumentsWithMissingArguments() {
    TranslateYaml translateYaml = new TranslateYaml();
    assertThrows(
        IllegalArgumentException.class,
        () ->
            translateYaml.parseArguments(
                "deepl:https://dummy?authKey=abc", "src/test/resources/translate-yaml", "de"));
  }

  @Test
  void testParseArgumentsWithUnknownSourceLang() {
    TranslateYaml translateYaml = new TranslateYaml();
    assertThrows(
        IllegalArgumentException.class,
        () ->
            translateYaml.parseArguments(
                "deepl:https://dummy?authKey=abc",
                "src/test/resources/translate-yaml",
                "fr",
                "target/test/translate-yaml"));
  }

  @Test
  void testParseArgumentsWithMalformedUrl() {
    TranslateYaml translateYaml = new TranslateYaml();
    assertThrows(
        IllegalArgumentException.class,
        () ->
            translateYaml.parseArguments(
                "deepl:https://dummy?authKey=abc def",
                "src/test/resources/translate-yaml",
                "de",
                "target/test/translate-yaml"));
  }
}
