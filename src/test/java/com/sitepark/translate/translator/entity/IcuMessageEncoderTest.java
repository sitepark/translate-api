package com.sitepark.translate.translator.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class IcuMessageEncoderTest {

  private static final String PLURAL_HASH = "{value_int, plural, one {# Byte} other {# Bytes}}";
  private static final String PLURAL_TEXT = "{count, plural, one {Kategorie} other {Kategorien}}";
  private static final String NUMBER_ARG = "{value, number, ::.#} kB";

  @Test
  void testIsIcuWithTypedArgument() {
    assertTrue(IcuMessageEncoder.isIcu(NUMBER_ARG), "typed number arg is ICU");
  }

  @Test
  void testIsIcuWithPluralHash() {
    assertTrue(IcuMessageEncoder.isIcu(PLURAL_HASH), "plural with # is ICU");
  }

  @Test
  void testIsIcuWithPluralText() {
    assertTrue(IcuMessageEncoder.isIcu(PLURAL_TEXT), "plural with text is ICU");
  }

  @Test
  void testIsIcuWithSimpleArgument() {
    assertTrue(IcuMessageEncoder.isIcu("Hello {name}!"), "simple arg is ICU");
  }

  @Test
  void testIsIcuFalseForPlainText() {
    assertFalse(IcuMessageEncoder.isIcu("Hello World"), "plain text is not ICU");
  }

  @Test
  void testIsIcuFalseForDatePattern() {
    assertFalse(IcuMessageEncoder.isIcu("dd.MM.yyyy"), "no argument, not ICU");
  }

  @Test
  void testIsIcuFalseForUnbalancedBraces() {
    assertFalse(IcuMessageEncoder.isIcu("unclosed {brace"), "unparseable is not ICU");
  }

  @Test
  void testEncodeXmlTypedArgument() {
    assertEquals(
        "<x>{value, number, ::.#}</x> kB",
        IcuMessageEncoder.encodeXml(NUMBER_ARG),
        "Unexpected ICU encoding");
  }

  @Test
  void testEncodeXmlPluralWithHash() {
    assertEquals(
        "<x>{value_int, plural, one {#</x> Byte<x>} other {#</x> Bytes<x>}}</x>",
        IcuMessageEncoder.encodeXml(PLURAL_HASH),
        "Unexpected ICU encoding");
  }

  @Test
  void testEncodeXmlPluralWithText() {
    assertEquals(
        "<x>{count, plural, one {</x>Kategorie<x>} other {</x>Kategorien<x>}}</x>",
        IcuMessageEncoder.encodeXml(PLURAL_TEXT),
        "Unexpected ICU encoding");
  }

  @Test
  void testEncodeXmlSimpleArgument() {
    assertEquals(
        "Hello <x>{name}</x>!",
        IcuMessageEncoder.encodeXml("Hello {name}!"),
        "Unexpected ICU encoding");
  }

  @Test
  void testRoundTripTypedArgument() {
    assertEquals(
        NUMBER_ARG,
        Decoder.decodeXml(IcuMessageEncoder.encodeXml(NUMBER_ARG)),
        "Round trip must reconstruct the original ICU string");
  }

  @Test
  void testRoundTripPluralWithHash() {
    assertEquals(
        PLURAL_HASH,
        Decoder.decodeXml(IcuMessageEncoder.encodeXml(PLURAL_HASH)),
        "Round trip must reconstruct the original ICU string");
  }

  @Test
  void testRoundTripPluralWithText() {
    assertEquals(
        PLURAL_TEXT,
        Decoder.decodeXml(IcuMessageEncoder.encodeXml(PLURAL_TEXT)),
        "Round trip must reconstruct the original ICU string");
  }

  // --- Complex / multi-line ICU messages (regression coverage for folded-YAML scalars, nesting,
  // select, selectordinal, offsets, and multiple arguments). Each asserts a lossless round trip:
  // encodeXml -> decodeXml reconstructs the original string. ---

  @Test
  void testRoundTripMultilinePlural() {
    // The real-world folded-YAML case: the plural message contains newlines inside the structure.
    String msg = "{count, plural,\n    one   {Kategorie}\n    other {Kategorien}\n    }";
    assertEquals(
        msg,
        Decoder.decodeXml(IcuMessageEncoder.encodeXml(msg)),
        "multi-line plural must round-trip");
  }

  @Test
  void testMultilinePluralHasNoTagsAfterDecode() {
    String msg = "{count, plural,\n    one   {Kategorie}\n    other {Kategorien}\n    }";
    String decoded = Decoder.decodeXml(IcuMessageEncoder.encodeXml(msg));
    assertFalse(decoded.contains("<x>"), "decoded message must not contain <x> tags");
  }

  @Test
  void testRoundTripSelect() {
    String msg = "{gender, select, male {He replied} female {She replied} other {They replied}}";
    assertEquals(
        msg, Decoder.decodeXml(IcuMessageEncoder.encodeXml(msg)), "select must round-trip");
  }

  @Test
  void testRoundTripSelectOrdinal() {
    String msg = "{place, selectordinal, one {#st} two {#nd} few {#rd} other {#th}} place";
    assertEquals(
        msg, Decoder.decodeXml(IcuMessageEncoder.encodeXml(msg)), "selectordinal must round-trip");
  }

  @Test
  void testRoundTripPluralWithOffset() {
    String msg = "{count, plural, offset:1 =0 {nobody} one {someone} other {# people}}";
    assertEquals(
        msg,
        Decoder.decodeXml(IcuMessageEncoder.encodeXml(msg)),
        "plural with offset/explicit value must round-trip");
  }

  @Test
  void testRoundTripNestedSelectInPlural() {
    String msg =
        "{count, plural, one {{gender, select, male {his} female {her} other {their}} book}"
            + " other {books}}";
    assertEquals(
        msg,
        Decoder.decodeXml(IcuMessageEncoder.encodeXml(msg)),
        "nested select inside plural must round-trip");
  }

  @Test
  void testRoundTripMultipleArgumentsWithSurroundingText() {
    String msg = "You have {count, plural, one {# message} other {# messages}} from {sender}.";
    assertEquals(
        msg,
        Decoder.decodeXml(IcuMessageEncoder.encodeXml(msg)),
        "multiple args with surrounding text must round-trip");
  }

  @Test
  void testNestedSelectInPluralTranslatesLiteralsOnly() {
    // Simulate the provider: translate only the bare (non-<x>) literal segments, keep <x> verbatim,
    // then decode. The ICU structure and nesting must survive; only literals change. The literal
    // words are chosen to not be substrings of any ICU keyword/selector so the naive replace only
    // touches literal text.
    String msg =
        "{count, plural, one {{gender, select, male {him} female {she} other {them}} chapter}"
            + " other {chapters}}";
    String encoded = IcuMessageEncoder.encodeXml(msg);
    String translated =
        encoded
            .replace("him", "ihn")
            .replace("she", "sie")
            .replace("them", "sie")
            .replace(" chapter", " Kapitel")
            .replace("chapters", "Kapitel");
    assertEquals(
        "{count, plural, one {{gender, select, male {ihn} female {sie} other {sie}} Kapitel}"
            + " other {Kapitel}}",
        Decoder.decodeXml(translated),
        "only literals translated, ICU structure intact");
  }
}
