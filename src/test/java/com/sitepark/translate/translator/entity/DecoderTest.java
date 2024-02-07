package com.sitepark.translate.translator.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class DecoderTest {

  @Test
  void test() {
    String s =
        Decoder.decode(
            "Ein Text mit <span data-encoded-entity=\"true\" translate=\"no\">${text}</span>.");
    assertEquals("Ein Text mit ${text}.", s, "Unexpected text");
  }
}
