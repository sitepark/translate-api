package com.sitepark.translate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class GlossaryEntryTest {

  @Test
  void testEqualsContract() {
    EqualsVerifier.forClass(GlossaryEntry.class).verify();
  }

  @Test
  void testSetSource() {
    GlossaryEntry entry = GlossaryEntry.builder().source("de").target("en").build();
    assertEquals("de", entry.getSource(), "unexpected source");
  }

  @Test
  void testNullSource() {
    assertThrows(
        NullPointerException.class,
        () -> {
          GlossaryEntry.builder().source(null);
        },
        "Setting null as source should not be allowed");
  }

  @Test
  void testBlankSource() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          GlossaryEntry.builder().source(" ");
        },
        "Setting blank string as source should not be allowed");
  }

  @Test
  void testUnsetSource() {
    assertThrows(
        IllegalStateException.class,
        () -> {
          GlossaryEntry.builder().target("en").build();
        },
        "Unset source should not be allowed");
  }

  @Test
  void testSetTarget() {
    GlossaryEntry entry = GlossaryEntry.builder().source("de").target("en").build();
    assertEquals("en", entry.getTarget(), "unexpected source");
  }

  @Test
  void testNullTarget() {
    assertThrows(
        NullPointerException.class,
        () -> {
          GlossaryEntry.builder().target(null);
        },
        "Setting null as target should not be allowed");
  }

  @Test
  void testBlankTarget() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          GlossaryEntry.builder().target(" ");
        },
        "Setting blank string as target should not be allowed");
  }

  @Test
  void testUnsetTarget() {
    assertThrows(
        IllegalStateException.class,
        () -> {
          GlossaryEntry.builder().source("de").build();
        },
        "Unset source should not be allowed");
  }

  @Test
  @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
  void testToBuilder() {

    GlossaryEntry entry = GlossaryEntry.builder().source("de").target("en").build();

    GlossaryEntry copy = entry.toBuilder().build();

    assertEquals("de", copy.getSource(), "unexpected source");
    assertEquals("en", copy.getTarget(), "unexpected target");
  }
}
