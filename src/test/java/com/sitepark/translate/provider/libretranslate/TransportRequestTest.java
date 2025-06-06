package com.sitepark.translate.provider.libretranslate;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.sitepark.translate.Format;
import org.junit.jupiter.api.Test;

@SuppressWarnings({"PMD.UnitTestContainsTooManyAsserts", "PMD.AvoidDuplicateLiterals"})
class TransportRequestTest {

  @Test
  void testBuilder() {

    TransportRequest request =
        TransportRequest.builder()
            .q("Hallo", "Welt")
            .source("de")
            .target("en")
            .format(Format.HTML)
            .apiKey("abc")
            .build();

    assertArrayEquals(new String[] {"Hallo", "Welt"}, request.getQ(), "unexpected q-field");
    assertEquals("de", request.getSource(), "unexpected source");
    assertEquals("en", request.getTarget(), "unexpected target");
    assertEquals(Format.HTML, request.getFormat(), "unexpected format");
    assertEquals("abc", request.getApiKey(), "unexpected apiKey");
  }

  @Test
  void testToBuilder() {

    TransportRequest request =
        TransportRequest.builder()
            .q("Hallo", "Welt")
            .source("de")
            .target("en")
            .format(Format.HTML)
            .apiKey("abc")
            .build();

    request = request.toBuilder().q("Apfel").build();

    assertArrayEquals(new String[] {"Apfel"}, request.getQ(), "unexpected q-field");
    assertEquals("de", request.getSource(), "unexpected source");
    assertEquals("en", request.getTarget(), "unexpected target");
    assertEquals(Format.HTML, request.getFormat(), "unexpected format");
    assertEquals("abc", request.getApiKey(), "unexpected apiKey");
  }

  @Test
  void testQSetNullArray() {
    assertThrows(
        NullPointerException.class,
        () -> {
          TransportRequest.builder().q((String[]) null);
        });
  }

  @Test
  void testQSetEmptyArray() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          TransportRequest.builder().q();
        });
  }

  @Test
  void testQSetNullString() {
    assertThrows(
        NullPointerException.class,
        () -> {
          TransportRequest.builder().q((String) null);
        });
  }

  @Test
  void testSourceSetNull() {
    assertThrows(
        NullPointerException.class,
        () -> {
          TransportRequest.builder().source(null);
        });
  }

  @Test
  void testTargetSetNull() {
    assertThrows(
        NullPointerException.class,
        () -> {
          TransportRequest.builder().target(null);
        });
  }

  @Test
  void testFormatSetNull() {
    assertThrows(
        NullPointerException.class,
        () -> {
          TransportRequest.builder().format(null);
        });
  }

  @Test
  void testApiKeysSetNull() {
    assertThrows(
        NullPointerException.class,
        () -> {
          TransportRequest.builder().apiKey(null);
        });
  }
}
