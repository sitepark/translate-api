package com.sitepark.translate.cli;

import static org.junit.jupiter.api.Assertions.assertNotNull;

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
}
