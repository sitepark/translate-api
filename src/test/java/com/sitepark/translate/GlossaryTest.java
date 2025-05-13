package com.sitepark.translate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.List;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

@SuppressWarnings("PMD.AvoidDuplicateLiterals")
class GlossaryTest {

  @Test
  @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
  void testEqualsContract() {
    EqualsVerifier.forClass(Glossary.class).verify();
  }

  @Test
  void testSetName() {

    Glossary glossar =
        Glossary.builder()
            .name("test:de/en")
            .language(TranslationLanguage.builder().source("de").target("en").build())
            .build();

    assertEquals("test:de/en", glossar.getName(), "unexpected name");
  }

  @Test
  void testNullName() {
    assertThrows(
        NullPointerException.class,
        () -> {
          Glossary.builder().name(null);
        },
        "Setting null as name should not be allowed");
  }

  @Test
  void testBlankName() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          Glossary.builder().name(" ");
        },
        "Setting blank name should not be allowed");
  }

  @Test
  void testUnsetName() {
    assertThrows(
        IllegalStateException.class,
        () -> {
          Glossary.builder()
              .language(TranslationLanguage.builder().source("de").target("en").build())
              .build();
        },
        "Missing name must lead to an error");
  }

  @Test
  void testSetLanguage() {

    Glossary glossar =
        Glossary.builder()
            .name("test:de/en")
            .language(TranslationLanguage.builder().source("de").target("en").build())
            .build();

    TranslationLanguage expected = TranslationLanguage.builder().source("de").target("en").build();

    assertEquals(expected, glossar.getLanguage(), "unexpected language");
  }

  @Test
  void testNullSourceLanguage() {
    assertThrows(
        NullPointerException.class,
        () -> {
          Glossary.builder().language(null);
        },
        "Setting null as language should not be allowed");
  }

  @Test
  void testUnsetLanguage() {
    assertThrows(
        IllegalStateException.class,
        () -> {
          Glossary.builder().name("test:de/en").build();
        },
        "Missing language must lead to an error");
  }

  @Test
  void testSetEntry() {

    GlossaryEntry entry = GlossaryEntry.builder().source("Hallo").target("Hey").build();

    Glossary glossar =
        Glossary.builder()
            .name("test:de/en")
            .language(TranslationLanguage.builder().source("de").target("en").build())
            .entry(entry)
            .build();

    assertEquals(Arrays.asList(entry), glossar.getEntryList(), "unexpected entryList");
  }

  @Test
  void testSetEntryArray() {

    GlossaryEntry[] entryArray =
        new GlossaryEntry[] {GlossaryEntry.builder().source("Hallo").target("Hey").build()};

    Glossary glossar =
        Glossary.builder()
            .name("test:de/en")
            .language(TranslationLanguage.builder().source("de").target("en").build())
            .entryList(entryArray)
            .build();

    assertEquals(Arrays.asList(entryArray), glossar.getEntryList(), "unexpected entryList");
  }

  @Test
  void testSetEntryList() {

    List<GlossaryEntry> entryList =
        Arrays.asList(GlossaryEntry.builder().source("Hallo").target("Hey").build());

    Glossary glossar =
        Glossary.builder()
            .name("test:de/en")
            .language(TranslationLanguage.builder().source("de").target("en").build())
            .entryList(entryList)
            .build();

    assertEquals(entryList, glossar.getEntryList(), "unexpected entryList");
  }

  @Test
  void testNullEntryList() {
    assertThrows(
        NullPointerException.class,
        () -> {
          Glossary.builder().entryList((List<GlossaryEntry>) null);
        });
  }

  @Test
  void testNullEntryInList() {
    assertThrows(
        NullPointerException.class,
        () -> {
          Glossary.builder().entryList(Arrays.asList((GlossaryEntry) null));
        });
  }

  @Test
  void testNullEntryArray() {
    assertThrows(
        NullPointerException.class,
        () -> {
          Glossary.builder().entryList((GlossaryEntry[]) null);
        });
  }

  @Test
  void testNullEntry() {
    assertThrows(
        NullPointerException.class,
        () -> {
          Glossary.builder().entry(null);
        });
  }

  @Test
  void testToBuilder() {

    Glossary glossary =
        Glossary.builder()
            .name("test:de/en")
            .language(TranslationLanguage.builder().source("de").target("en").build())
            .entry(GlossaryEntry.builder().source("Hallo").target("Hey").build())
            .build();

    Glossary copy = glossary.toBuilder().build();

    Glossary expected =
        Glossary.builder()
            .name("test:de/en")
            .language(TranslationLanguage.builder().source("de").target("en").build())
            .entry(GlossaryEntry.builder().source("Hallo").target("Hey").build())
            .build();

    assertEquals(expected, copy, "unexpected glossary");
  }
}
