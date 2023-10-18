package com.sitepark.translate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@SuppressWarnings({
	"PMD.JUnitTestContainsTooManyAsserts",
	"PMD.AvoidDuplicateLiterals"
})
class SupportedLanguagesTest {

	@Test
	void testBuilder() {

		Language de = Language.builder()
				.code("de")
				.name("deutsch")
				.targets("en", "fr")
				.build();

		Language en = Language.builder()
				.code("en")
				.name("engish")
				.targets("de", "fr")
				.build();

		SupportedLanguages supportedLanguages = SupportedLanguages.builder()
				.language(de)
				.language(en.toBuilder())
				.build();

		assertEquals(
				Arrays.asList(de, en),
				supportedLanguages.getAll(),
				"unexpected supported languages");
		assertSame(
				de,
				supportedLanguages.getSourceLanguage("de").get(),
				"unexpected source language");
		assertEquals(
				en,
				supportedLanguages.getTargetLanguage("de", "en").get(),
				"unexpected target language");

	}

	@Test
	void testToBuilder() {

		Language de = Language.builder()
				.code("de")
				.name("deutsch")
				.targets("en", "fr")
				.build();

		Language en = Language.builder()
				.code("en")
				.name("engish")
				.targets("de", "fr")
				.build();

		SupportedLanguages supportedLanguages = SupportedLanguages.builder()
				.language(de)
				.build();

		supportedLanguages = supportedLanguages.toBuilder()
				.language(en)
				.build();

		assertSame(
				de,
				supportedLanguages.getSourceLanguage("de").get(),
				"unexprected source language");
		assertSame(
				en,
				supportedLanguages.getSourceLanguage("en").get(),
				"unexprected source language");
	}

	@Test
	void testSetLanguageToNull() {
		assertThrows(NullPointerException.class, () -> {
			SupportedLanguages.builder().language((Language)null);
		});
	}

	@Test
	void testSetLanguageBuilderToNull() {
		assertThrows(NullPointerException.class, () -> {
			SupportedLanguages.builder().language((Language.Builder)null);
		});
	}

	@Test
	void testReturnMissingSourceLanguage() {

		SupportedLanguages supportedLanguages = SupportedLanguages.builder()
				.build();

		assertEquals(
				Optional.empty(),
				supportedLanguages.getSourceLanguage("de"),
				"source languge 'de' should not be found");
	}

	@Test
	void testReturnMissingTargetLanguage() {

		Language de = Language.builder()
				.code("de")
				.name("deutsch")
				.targets("en", "fr")
				.build();

		Language en = Language.builder()
				.code("en")
				.name("engish")
				.targets("de", "fr")
				.build();

		SupportedLanguages supportedLanguages = SupportedLanguages.builder()
				.language(de)
				.language(en)
				.build();

		assertEquals(
				Optional.empty(),
				supportedLanguages.getTargetLanguage("de", "it"),
				"target language 'it' should not be found");
		assertEquals(
				Optional.empty(),
				supportedLanguages.getTargetLanguage("fr", "de"),
				"source language 'fr' should not be found");
	}

	@Test
	void testDeserialze() throws JsonMappingException, JsonProcessingException {

		String json = "[{" +
				"\"code\":\"de\"," +
				"\"name\":\"deutsch\"," +
				"\"targets\":[\"en\"]" +
		"}]";

		ObjectMapper objectMapper = new ObjectMapper();
		SupportedLanguages supportedLanguages =
				objectMapper.readValue(json, SupportedLanguages.class);

		assertTrue(
				supportedLanguages.getSourceLanguage("de").isPresent(),
				"language 'de' should be present"
		);
	}

}
