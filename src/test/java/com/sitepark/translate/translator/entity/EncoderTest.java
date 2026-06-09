package com.sitepark.translate.translator.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class EncoderTest {

  @Test
  void test() {
    String s = Encoder.encode("Ein Text mit ${text}.");
    assertEquals(
        "Ein Text mit <span data-encoded-entity=\"true\" translate=\"no\">${text}</span>.",
        s,
        "Unexpected encoding");
  }

  @Test
  void testEncodeXmlWithPlaceholder() {
    assertEquals(
        "Hello <x>{name}</x>!", Encoder.encodeXml("Hello {name}!"), "Unexpected XML encoding");
  }

  @Test
  void testEncodeXmlWithDollarBracePlaceholder() {
    assertEquals(
        "Hello <x>${name}</x>!", Encoder.encodeXml("Hello ${name}!"), "Unexpected XML encoding");
  }

  @Test
  void testEncodeXmlWithoutPlaceholderIsUnchanged() {
    assertEquals("Hello World", Encoder.encodeXml("Hello World"), "Plain text should be unchanged");
  }

  @Test
  void testEncodeXmlEscapesSpecialChars() {
    assertEquals(
        "A &amp; B &lt;x&gt; <x>{val}</x>",
        Encoder.encodeXml("A & B <x> {val}"),
        "Unexpected XML escaping");
  }
}
