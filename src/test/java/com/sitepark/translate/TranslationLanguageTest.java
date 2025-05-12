package com.sitepark.translate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

@SuppressWarnings({"PMD.UnitTestContainsTooManyAsserts", "PMD.AvoidDuplicateLiterals"})
class TranslationLanguageTest {

  @Test
  @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
  void testEqualsContract() {
    EqualsVerifier.forClass(TranslationLanguage.class).verify();
  }

  @Test
  void testBuilder() {
    TranslationLanguage language =
        TranslationLanguage.builder().source("de_DE").target("en_US").build();
    assertEquals("de_DE", language.getSource(), "unexpected source");
    assertEquals("en_US", language.getTarget(), "unexpected target");
  }

  @Test
  void testSetNullSource() {
    assertThrows(
        NullPointerException.class,
        () -> {
          TranslationLanguage.builder().source(null);
        },
        "source null should not allowed");
  }

  @Test
  void testMissingSource() {
    assertThrows(
        IllegalStateException.class,
        () -> {
          TranslationLanguage.builder().target("en_US").build();
        },
        "source null should not allowed");
  }

  @Test
  void testSetNullTarget() {
    assertThrows(
        NullPointerException.class,
        () -> {
          TranslationLanguage.builder().target(null);
        },
        "target null should not allowed");
  }

  @Test
  void testMissingTarget() {
    assertThrows(
        IllegalStateException.class,
        () -> {
          TranslationLanguage.builder().source("de_DE").build();
        },
        "target null should not allowed");
  }

  @Test
  void testToBuilder() {
    TranslationLanguage language =
        TranslationLanguage.builder().source("de_DE").target("en_US").build();

    language = language.toBuilder().source("fr_FR").build();

    assertEquals("fr_FR", language.getSource(), "unexpected source");
    assertEquals("en_US", language.getTarget(), "unexpected target");
  }
}
