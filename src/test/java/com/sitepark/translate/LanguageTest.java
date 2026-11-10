package com.sitepark.translate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.List;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

@SuppressWarnings({"PMD.UnitTestContainsTooManyAsserts", "PMD.AvoidDuplicateLiterals"})
class LanguageTest {

  @Test
  void testEqualsContract() {
    EqualsVerifier.forClass(Language.class).verify();
  }

  @Test
  @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
  void testDeserialize() throws Exception {
    String json = "{\"code\":\"de\", \"name\":\"deutsch\",\"targets\":[\"en\",\"it\"]}";
    ObjectMapper mapper = new ObjectMapper();
    Language language = mapper.readValue(json, Language.class);
    assertEquals("de", language.getCode(), "wrong code");
    assertEquals("deutsch", language.getName(), "wrong name");
    assertEquals(Arrays.asList("en", "it"), language.getTargets(), "wrong name");
  }

  @Test
  void testBuilder() {
    Language language =
        Language.builder()
            .code("de")
            .name("deutsch")
            .targets(Arrays.asList("en", "fr"))
            .targets("it")
            .build();
    assertEquals("de", language.getCode(), "unexpected code");
    assertEquals("deutsch", language.getName(), "unexpected name");
    assertEquals(Arrays.asList("en", "fr", "it"), language.getTargets(), "unexpected targets");
  }

  @Test
  void testToBuilder() {
    Language language =
        Language.builder()
            .code("de")
            .name("deutsch")
            .targets(Arrays.asList("en", "fr"))
            .targets("it")
            .build();
    language = language.toBuilder().code("es").name("espanol").build();
    assertEquals("es", language.getCode(), "unexpected code");
    assertEquals("espanol", language.getName(), "unexpected name");
    assertEquals(Arrays.asList("en", "fr", "it"), language.getTargets(), "unexpected targets");
  }

  @Test
  void testSetCodeToNull() {
    assertThrows(
        NullPointerException.class,
        () -> {
          Language.builder().code(null);
        },
        "code null should not allowed");
  }

  @Test
  void testMissingCode() {
    assertThrows(
        IllegalStateException.class,
        () -> {
          Language.builder().name("deutsch").targets("it").build();
        },
        "source null should not allowed");
  }

  @Test
  void testSetNameToNull() {
    assertThrows(
        NullPointerException.class,
        () -> {
          Language.builder().name(null);
        },
        "name null should not allowed");
  }

  @Test
  void testMissingName() {
    assertThrows(
        IllegalStateException.class,
        () -> {
          Language.builder().code("de").targets("it").build();
        },
        "source null should not allowed");
  }

  @Test
  void testSetTargetsToNull() {
    assertThrows(
        NullPointerException.class,
        () -> {
          Language.builder().targets((String) null);
        },
        "targets null should not allowed");
  }

  @Test
  void testSetTargetsArrayToNull() {
    assertThrows(
        NullPointerException.class,
        () -> {
          Language.builder().targets((String[]) null);
        },
        "targets null should not allowed");
  }

  @Test
  void testSetTargetsListToNull() {
    assertThrows(
        NullPointerException.class,
        () -> {
          Language.builder().targets((List<String>) null);
        },
        "targets null should not allowed");
  }

  @Test
  void testMissingTargets() {
    assertThrows(
        IllegalStateException.class,
        () -> {
          Language.builder().code("de").name("deutsch").build();
        },
        "empty targets should not allowed");
  }

  @Test
  void testUnsupportedTargetsAreReturnedInGivenOrder() {
    Language language =
        Language.builder().code("de").name("deutsch").targets("en-us", "cs").build();

    assertThat(language.getUnsupportedTargets(List.of("cj", "cs", "cz")))
        .as("only codes not offered by the provider should be returned")
        .containsExactly("cj", "cz");
  }

  @Test
  void testTargetCheckIsCaseInsensitive() {
    Language language = Language.builder().code("de").name("deutsch").targets("en-us").build();

    assertThat(language.getUnsupportedTargets(List.of("EN-US")))
        .as("target language codes should be compared case-insensitively")
        .isEmpty();
  }

  @Test
  void testRequireSupportedTargetsRejectsUnsupportedCodes() {
    Language language = Language.builder().code("de").name("deutsch").targets("cs").build();

    assertThatThrownBy(() -> language.requireSupportedTargets(List.of("cj", "cs", "cz")))
        .as("all unsupported codes should be reported at once")
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("[cj, cz]");
  }

  @Test
  void testRequireSupportedTargetsAcceptsSupportedCodes() {
    Language language = Language.builder().code("de").name("deutsch").targets("cs", "en").build();

    assertThatNoException()
        .as("supported codes should be accepted")
        .isThrownBy(() -> language.requireSupportedTargets(List.of("cs", "en")));
  }
}
