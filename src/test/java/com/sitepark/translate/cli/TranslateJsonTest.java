package com.sitepark.translate.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class TranslateJsonTest {

  @Test
  void testParseArgumentsWithDeepl() {
    TranslateJson translateJson = new TranslateJson();
    translateJson.parseArguments(
        "deepl:https://dummy?authKey=abc",
        "src/test/resources/translate-json",
        "de",
        "target/test/translate-json");
    assertEquals(1, translateJson.getTranslators().size(), "one translator expected");
  }

  @Test
  void testParseArgumentsWithLibreTranslate() {
    TranslateJson translateJson = new TranslateJson();
    translateJson.parseArguments(
        "libretranslate:https://dummy?apiKey=abc",
        "src/test/resources/translate-json",
        "de",
        "target/test/translate-json");
    assertEquals(1, translateJson.getTranslators().size(), "one translator expected");
  }
}
