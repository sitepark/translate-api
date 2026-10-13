package com.sitepark.translate.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.sitepark.translate.LocaleFormat;
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

  @Test
  void testParseArgumentsWithOutputDir() {
    TranslateJsonFile translateJsonFile = new TranslateJsonFile();
    translateJsonFile.parseArguments(
        "deepl:https://dummy?authKey=abc",
        "src/test/resources/translate-json",
        "de",
        "target/test/translate-json-generated");
    assertNotNull(translateJsonFile.getTranslator(), "translator expected");
  }

  @Test
  void testParseArgumentsWithLocaleFormat() {
    TranslateJsonFile translateJsonFile = new TranslateJsonFile();
    translateJsonFile.parseArguments(
        "deepl:https://dummy?authKey=abc",
        "src/test/resources/translate-json",
        "de",
        "--output-locale-format=icu");
    assertEquals(LocaleFormat.ICU, translateJsonFile.getLocaleFormat(), "icu format expected");
  }
}
