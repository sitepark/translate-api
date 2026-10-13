package com.sitepark.translate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class LocaleFormatTest {

  @ParameterizedTest
  @CsvSource({
    "en,      en,      en,      en",
    "en-us,   en-us,   en-US,   en_US",
    "en-gb,   en-gb,   en-GB,   en_GB",
    "pt-br,   pt-br,   pt-BR,   pt_BR",
    "zh-hans, zh-hans, zh-Hans, zh_Hans",
    "zh-hant, zh-hant, zh-Hant, zh_Hant",
    "es-419,  es-419,  es-419,  es_419",
    "ar,      ar,      ar,      ar"
  })
  @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
  void testFormat(String code, String lowercase, String bcp47, String icu) {
    assertThat(LocaleFormat.LOWERCASE.format(code)).isEqualTo(lowercase);
    assertThat(LocaleFormat.BCP47.format(code)).isEqualTo(bcp47);
    assertThat(LocaleFormat.ICU.format(code)).isEqualTo(icu);
  }

  @ParameterizedTest
  @CsvSource({
    "lowercase, LOWERCASE",
    "LOWERCASE, LOWERCASE",
    "bcp47,     BCP47",
    "Bcp47,     BCP47",
    "icu,       ICU",
    "ICU,       ICU"
  })
  void testFromName(String name, LocaleFormat expected) {
    assertThat(LocaleFormat.fromName(name)).isEqualTo(expected);
  }

  @ParameterizedTest
  @ValueSource(strings = {"", "symfony", "bcp-47", "icu "})
  void testFromNameWithUnknownName(String name) {
    assertThatThrownBy(() -> LocaleFormat.fromName(name))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("lowercase, bcp47, icu");
  }

  @ParameterizedTest
  @CsvSource({"en-gb, en-gb", "en-GB, en-gb", "en_GB, en-gb", "zh_Hans, zh-hans", "EN, en"})
  void testNormalize(String code, String expected) {
    assertThat(LocaleFormat.normalize(code)).isEqualTo(expected);
  }

  @Test
  void testFromNameWithNull() {
    assertThatThrownBy(() -> LocaleFormat.fromName(null)).isInstanceOf(NullPointerException.class);
  }
}
