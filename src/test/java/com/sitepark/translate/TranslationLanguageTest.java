package com.sitepark.translate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

@SuppressWarnings({
	"PMD.JUnitTestContainsTooManyAsserts",
	"PMD.AvoidDuplicateLiterals"
})
class TranslationLanguageTest {

	@Test
	void testBuilder() {
		TranslationLanguage language = TranslationLanguage.builder()
				.providerType(SupportedProvider.DEEPL)
				.source("de_DE")
				.target("en_US")
				.build();
		assertEquals(SupportedProvider.DEEPL, language.getProviderType(), "unexpected provider");
		assertEquals("de_DE", language.getSource(), "unexpected source");
		assertEquals("en_US", language.getTarget(), "unexpected target");
	}

	@Test
	void testSetNullProvider() {
		assertThrows(AssertionError.class, () -> {
			TranslationLanguage.builder().providerType(null);
		}, "providerType null should not allowed");
	}

	@Test
	void testMissingProvider() {
		assertThrows(AssertionError.class, () -> {
			TranslationLanguage.builder()
					.source("de_DE")
					.target("en_US")
					.build();
		}, "providerType null should not allowed");
	}

	@Test
	void testSetNullSource() {
		assertThrows(AssertionError.class, () -> {
			TranslationLanguage.builder().source(null);
		}, "source null should not allowed");
	}

	@Test
	void testMissingSource() {
		assertThrows(AssertionError.class, () -> {
			TranslationLanguage.builder()
					.providerType(SupportedProvider.DEEPL)
					.target("en_US")
					.build();
		}, "source null should not allowed");
	}

	@Test
	void testSetNullTarget() {
		assertThrows(AssertionError.class, () -> {
			TranslationLanguage.builder().target(null);
		}, "target null should not allowed");
	}

	@Test
	void testMissingTarget() {
		assertThrows(AssertionError.class, () -> {
			TranslationLanguage.builder()
					.providerType(SupportedProvider.DEEPL)
					.source("de_DE")
					.build();
		}, "target null should not allowed");
	}

	@Test
	void testToBuilder() {
		TranslationLanguage language = TranslationLanguage.builder()
				.providerType(SupportedProvider.DEEPL)
				.source("de_DE")
				.target("en_US")
				.build();

		language = language.toBuilder()
				.source("fr_FR")
				.build();

		assertEquals(SupportedProvider.DEEPL, language.getProviderType(), "unexpected provider");
		assertEquals("fr_FR", language.getSource(), "unexpected source");
		assertEquals("en_US", language.getTarget(), "unexpected target");
	}

}
