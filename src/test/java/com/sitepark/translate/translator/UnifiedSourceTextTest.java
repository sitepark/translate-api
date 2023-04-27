package com.sitepark.translate.translator;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.junit.jupiter.api.Test;

@SuppressWarnings("PMD.AvoidDuplicateLiterals")
class UnifiedSourceTextTest {

	@Test
	void testGetSourceTextOnlyWithDifferentText() {
		String[] sourceText = new String[] {"Hallo", "Welt", "ich", "grüße", "dich"};
		UnifiedSourceText unifiedSourceText = new UnifiedSourceText(sourceText);
		assertArrayEquals(
				new String[] {"Hallo", "Welt", "ich", "grüße", "dich"},
				unifiedSourceText.getSourceText(),
				"should have no changes"
		);
	}

	@Test
	void testExpandTranslationOnlyWithDifferentText() {
		String[] sourceText = new String[] {"Hallo", "Welt", "ich", "grüße", "dich"};
		UnifiedSourceText unifiedSourceText = new UnifiedSourceText(sourceText);

		String[] translationText = new String[] {"Hello", "world", "i", "greet", "you"};
		assertArrayEquals(
				translationText,
				unifiedSourceText.expandTranslation(translationText),
				"wrong expanded");
	}

	@Test
	void testGetSourceTextWithEqualsText() {
		String[] sourceText = new String[] {"Hallo", "Welt", "Hallo", "Welt"};
		UnifiedSourceText unifiedSourceText = new UnifiedSourceText(sourceText);
		assertArrayEquals(
				new String[] {"Hallo", "Welt"},
				unifiedSourceText.getSourceText(),
				"should be unified");
	}

	@Test
	void testExpandTranslationWithEqualsText() {
		String[] sourceText = new String[] {
				"Eins",
				"Eins",
				"Zwei",
				"Eins",
				"Zwei",
				"Zwei",
				"Drei",
				"Vier",
				"Fünf",
				"Zwei",
				"Fünf",
				"Drei",
		};
		UnifiedSourceText unifiedSourceText = new UnifiedSourceText(sourceText);

		String[] translationText = new String[] {"One", "Two", "Three", "Four", "Five"};
		String[] expectedTranslationText = new String[] {
				"One",
				"One",
				"Two",
				"One",
				"Two",
				"Two",
				"Three",
				"Four",
				"Five",
				"Two",
				"Five",
				"Three",
		};

		assertArrayEquals(
				expectedTranslationText,
				unifiedSourceText.expandTranslation(translationText),
				"wrong expanded");
	}

}