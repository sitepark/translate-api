package com.sitepark.translate.cli;

import static org.junit.jupiter.api.Assertions.assertNotNull;

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
}
