package com.sitepark.translate.cli;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class TranslateJsonFileTest {

  @Test
  void testParseArgumentsWithDeepl() {
    TranslateJsonFile translateJsonFile = new TranslateJsonFile();
    translateJsonFile.parseArguments(
        "deepl:https://dummy?authKey=abc", "src/test/resources/translate-json", "de");
    assertNotNull(translateJsonFile.getTranslator(), "translator expected");
  }

  @Test
  void testParseArgumentsWithLibreTranslate() {
    TranslateJsonFile translateJsonFile = new TranslateJsonFile();
    translateJsonFile.parseArguments(
        "libretranslate:https://dummy?apiKey=abc", "src/test/resources/translate-json", "de");
    assertNotNull(translateJsonFile.getTranslator(), "translator expected");
  }
}
