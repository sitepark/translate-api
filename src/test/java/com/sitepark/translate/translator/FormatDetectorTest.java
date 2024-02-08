package com.sitepark.translate.translator;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

@SuppressWarnings("PMD.AvoidDuplicateLiterals")
class FormatDetectorTest {

  @Test
  void testContainsHtmlPlainText() {
    assertFalse(FormatDetector.containsHtml("A text without HTML"), "should be plain text");
  }

  @Test
  void testContainsHtmlPlainTextGreaterThan() {
    assertFalse(
        FormatDetector.containsHtml("A text without HTML, but with >"), "should be plain text");
  }

  @Test
  void testContainsHtmlPlainTextLowerThan() {
    assertFalse(
        FormatDetector.containsHtml("A text without HTML, but with <"), "should be plain text");
  }

  @Test
  void testContainsHtmlPlainTextLowerThanGreaterThanMix() {
    assertFalse(
        FormatDetector.containsHtml("A text without HTML, but with < 1 > 7"),
        "should be plain text");
  }

  @Test
  void testContainsHtmlWithHtmlEntity() {
    assertTrue(
        FormatDetector.containsHtml("A text with entity &amp;, so it's HTML"),
        "should be html text");
  }

  @Test
  void testContainsHtmlWithHtmlTag() {
    assertTrue(
        FormatDetector.containsHtml("A text with tag <a>, so it's HTML"), "should be html text");
  }

  @Test
  void testContainsHtmlWithHtmlBodyTag() {
    assertTrue(
        FormatDetector.containsHtml("A text with body-tag <strong>text</strong>, so it's HTML"),
        "should be html text");
  }

  @Test
  void testContainsPlaceholder() {
    assertTrue(
        FormatDetector.containsPlaceholder("Good morning ${name}"), "should find placeholder");
  }
}
