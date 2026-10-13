package com.sitepark.translate.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.sitepark.translate.LocaleFormat;
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
    assertNotNull(translateJson.getTranslator(), "translator expected");
  }

  @Test
  void testParseArgumentsWithLibreTranslate() {
    TranslateJson translateJson = new TranslateJson();
    translateJson.parseArguments(
        "libretranslate:https://dummy?apiKey=abc",
        "src/test/resources/translate-json",
        "de",
        "target/test/translate-json");
    assertNotNull(translateJson.getTranslator(), "translator expected");
  }

  @Test
  void testParseArgumentsWithLocaleFormat() {
    TranslateJson translateJson = new TranslateJson();
    translateJson.parseArguments(
        "deepl:https://dummy?authKey=abc",
        "src/test/resources/translate-json",
        "de",
        "target/test/translate-json",
        "--output-locale-format=icu");
    assertEquals(LocaleFormat.ICU, translateJson.getLocaleFormat(), "icu format expected");
  }
}
