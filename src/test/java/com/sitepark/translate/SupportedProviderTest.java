package com.sitepark.translate;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class SupportedProviderTest {

  @Test
  void testOfSchemeDeepl() {
    SupportedProvider supportedProvider = SupportedProvider.ofScheme("deepl");
    assertEquals(SupportedProvider.DEEPL, supportedProvider, SupportedProvider.DEEPL + " expected");
  }

  @Test
  void testOfSchemeLibretranslate() {
    SupportedProvider supportedProvider = SupportedProvider.ofScheme("libretranslate");
    assertEquals(
        SupportedProvider.LIBRE_TRANSLATE,
        supportedProvider,
        SupportedProvider.LIBRE_TRANSLATE + " expected");
  }

  @Test
  void testOfSchemeInvalidValue() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          SupportedProvider.ofScheme("abc");
        },
        "should throw an exception");
  }

  @Test
  void testSchemes() {
    assertArrayEquals(
        new String[] {"deepl", "libretranslate"},
        SupportedProvider.scheme(),
        "unexpected scheme list");
  }
}
