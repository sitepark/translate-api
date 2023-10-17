package com.sitepark.translate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

@SuppressWarnings("PMD.AvoidDuplicateLiterals")
class GlossaryTest {

	@Test
	@SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
	public void testEqualsContract() {
		EqualsVerifier.forClass(Glossary.class).verify();
	}

	@Test
	public void testSetLanguage() {

		Glossary glossar = Glossary.builder()
				.language(TranslationLanguage.builder()
						.source("de")
						.target("en")
						.build()
				)
				.build();

		TranslationLanguage expected = TranslationLanguage.builder()
				.source("de")
				.target("en")
				.build();

		assertEquals(expected, glossar.getLanguage(), "unexpected language");
	}

	@Test
	public void testNullSourceLanguage() {
		assertThrows(NullPointerException.class, () -> {
			Glossary.builder().language(null);
		}, "Setting null as language should not be allowed");
	}


	@Test
	public void testSetEntry() {

		GlossaryEntry entry = GlossaryEntry.builder()
				.source("Hallo")
				.target("Hey")
				.build();

		Glossary glossar = Glossary.builder()
				.language(TranslationLanguage.builder()
						.source("de")
						.target("en")
						.build()
				)
				.entry(entry)
				.build();

		assertEquals(Arrays.asList(entry), glossar.getEntryList(), "unexpected entryList");
	}

	@Test
	public void testSetEntryArray() {

		GlossaryEntry[] entryArray = new GlossaryEntry[] {
				GlossaryEntry.builder()
					.source("Hallo")
					.target("Hey")
					.build()
		};

		Glossary glossar = Glossary.builder()
				.language(TranslationLanguage.builder()
						.source("de")
						.target("en")
						.build()
				)
				.entryList(entryArray)
				.build();

		assertEquals(Arrays.asList(entryArray), glossar.getEntryList(), "unexpected entryList");
	}

	@Test
	public void testSetEntryList() {

		List<GlossaryEntry> entryList = Arrays.asList(
				GlossaryEntry.builder()
					.source("Hallo")
					.target("Hey")
					.build());

		Glossary glossar = Glossary.builder()
				.language(TranslationLanguage.builder()
						.source("de")
						.target("en")
						.build()
				)
				.entryList(entryList)
				.build();

		assertEquals(entryList, glossar.getEntryList(), "unexpected entryList");
	}

	@Test
	public void testNullEntryList() {
		assertThrows(NullPointerException.class, () -> {
			Glossary.builder().entryList((List<GlossaryEntry>)null);
		});
	}

	@Test
	public void testNullEntryInList() {
		assertThrows(NullPointerException.class, () -> {
			Glossary.builder().entryList(Arrays.asList((GlossaryEntry)null));
		});
	}

	@Test
	public void testNullEntryArray() {
		assertThrows(NullPointerException.class, () -> {
			Glossary.builder().entryList((GlossaryEntry[])null);
		});
	}

	@Test
	public void testNullEntry() {
		assertThrows(NullPointerException.class, () -> {
			Glossary.builder().entry(null);
		});
	}

	@Test
	public void testToBuilder() {

		Glossary glossary = Glossary.builder()
				.language(TranslationLanguage.builder()
						.source("de")
						.target("en")
						.build()
				)
				.entry(GlossaryEntry.builder()
						.source("Hallo")
						.target("Hey")
						.build())
				.build();

		Glossary copy = glossary.toBuilder().build();

		Glossary expected = Glossary.builder()
				.language(TranslationLanguage.builder()
						.source("de")
						.target("en")
						.build()
				)
				.entry(GlossaryEntry.builder()
						.source("Hallo")
						.target("Hey")
						.build())
				.build();

		assertEquals(expected, copy, "unexpected glossary");
	}
}
