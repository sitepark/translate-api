package com.sitepark.translate;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class FormatTest {

  @Test
  void testText() {
    assertEquals("text", Format.TEXT.toString(), "should be lower case");
  }

  @Test
  void testHtml() {
    assertEquals("html", Format.HTML.toString(), "should be lower case");
  }
}
