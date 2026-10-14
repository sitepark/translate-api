package com.sitepark.translate.cli;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Set;
import org.junit.jupiter.api.Test;

class CliOptionsTest {

  private static final Set<String> ALLOWED = Set.of(CliOptions.OUTPUT_LOCALE_FORMAT);

  @Test
  @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
  void testWithoutOptions() {
    CliOptions options = CliOptions.parse(ALLOWED, "a", "b");
    assertThat(options.positional()).containsExactly("a", "b");
    assertThat(options.get(CliOptions.OUTPUT_LOCALE_FORMAT)).isEmpty();
  }

  @Test
  @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
  void testOptionAtAnyPosition() {
    CliOptions options = CliOptions.parse(ALLOWED, "a", "--output-locale-format=icu", "b");
    assertThat(options.positional()).containsExactly("a", "b");
    assertThat(options.get(CliOptions.OUTPUT_LOCALE_FORMAT)).contains("icu");
  }

  @Test
  void testUnknownOption() {
    assertThatThrownBy(() -> CliOptions.parse(ALLOWED, "--foo=bar"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("--foo");
  }

  @Test
  void testOptionWithoutValue() {
    assertThatThrownBy(() -> CliOptions.parse(ALLOWED, "--output-locale-format"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("requires a value");
  }

  @Test
  void testOptionGivenTwice() {
    assertThatThrownBy(
            () ->
                CliOptions.parse(
                    ALLOWED, "--output-locale-format=icu", "--output-locale-format=bcp47"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("more than once");
  }
}
