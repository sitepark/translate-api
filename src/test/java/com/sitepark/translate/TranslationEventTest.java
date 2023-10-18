package com.sitepark.translate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

@SuppressWarnings({
	"PMD.JUnitTestContainsTooManyAsserts"
})
class TranslationEventTest {

	@Test
	void testBuilder() {
		TranslationLanguage language = Mockito.mock(TranslationLanguage.class);
		TranslationEvent event = TranslationEvent.builder()
			.translationLanguage(language)
			.translationTime(123)
			.chunks(12)
			.sourceBytes(345)
			.targetBytes(346)
			.build();

		assertEquals(language, event.getTranslationLanguage(), "unexpected translationLanguage");
		assertEquals(123, event.getTranslationTime(), "unexpected translationTime");
		assertEquals(12, event.getChunks(), "unexpected chunks");
		assertEquals(345, event.getSourceBytes(), "unexpected sourceBytes");
		assertEquals(346, event.getTargetBytes(), "unexpected targetBytes");
	}

	@Test
	void testSetTranslationLanguageToNull() {
		assertThrows(NullPointerException.class, () -> {
			TranslationEvent.builder().translationLanguage(null);
		});
	}

	@Test
	void testMissingTranslationLanguage() {
		assertThrows(IllegalStateException.class, () -> {
			TranslationEvent.builder()
					.translationTime(123)
					.chunks(12)
					.sourceBytes(345)
					.targetBytes(346)
					.build();
		});
	}
}
