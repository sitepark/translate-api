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
	public void testSetSourceLanguage() {
		Glossary glossar = Glossary.builder()
				.sourceLanguage("de")
				.targetLanguage("en")
				.build();
		assertEquals("de", glossar.getSourceLanguage(), "unexpected sourceLanguage");
	}

	@Test
	public void testNullSourceLanguage() {
		assertThrows(AssertionError.class, () -> {
			Glossary.builder().sourceLanguage(null);
		}, "Setting null as sourceLanguage should not be allowed");
	}

	@Test
	public void testBlankSourceLanguage() {
		assertThrows(AssertionError.class, () -> {
			Glossary.builder().sourceLanguage(" ");
		}, "Setting blank string as sourceLanguage should not be allowed");
	}

	@Test
	public void testUnsetSourceLanguage() {
		assertThrows(AssertionError.class, () -> {
			Glossary.builder().targetLanguage("en").build();
		}, "Unset sourceLanguage should not be allowed");
	}

	@Test
	public void testSetTargetLanguage() {
		Glossary glossar = Glossary.builder()
				.sourceLanguage("de")
				.targetLanguage("en")
				.build();
		assertEquals("de", glossar.getSourceLanguage(), "unexpected sourceLanguage");
	}

	@Test
	public void testNullTargetLanguage() {
		assertThrows(AssertionError.class, () -> {
			Glossary.builder().targetLanguage(null);
		}, "Setting null as targetLanguage should not be allowed");
	}

	@Test
	public void testBlankTargetLanguage() {
		assertThrows(AssertionError.class, () -> {
			Glossary.builder().targetLanguage(" ");
		}, "Setting blank string as targetLanguage should not be allowed");
	}

	@Test
	public void testUnsetTargetLanguage() {
		assertThrows(AssertionError.class, () -> {
			Glossary.builder().sourceLanguage("de").build();
		}, "Unset targetLanguage should not be allowed");
	}

	@Test
	public void testSetEntry() {

		GlossaryEntry entry = GlossaryEntry.builder()
				.source("Hallo")
				.target("Hey")
				.build();

		Glossary glossar = Glossary.builder()
				.sourceLanguage("de")
				.targetLanguage("en")
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
				.sourceLanguage("de")
				.targetLanguage("en")
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
				.sourceLanguage("de")
				.targetLanguage("en")
				.entryList(entryList)
				.build();

		assertEquals(entryList, glossar.getEntryList(), "unexpected entryList");
	}

	@Test
	public void testNullEntryList() {
		assertThrows(AssertionError.class, () -> {
			Glossary.builder().entryList((List<GlossaryEntry>)null);
		});
	}

	@Test
	public void testNullEntryInList() {
		assertThrows(AssertionError.class, () -> {
			Glossary.builder().entryList(Arrays.asList((GlossaryEntry)null));
		});
	}

	@Test
	public void testNullEntryArray() {
		assertThrows(AssertionError.class, () -> {
			Glossary.builder().entryList((GlossaryEntry[])null);
		});
	}

	@Test
	public void testNullEntry() {
		assertThrows(AssertionError.class, () -> {
			Glossary.builder().entry(null);
		});
	}

	@Test
	public void testToBuilder() {

		Glossary glossary = Glossary.builder()
				.sourceLanguage("de")
				.targetLanguage("en")
				.entry(GlossaryEntry.builder()
						.source("Hallo")
						.target("Hey")
						.build())
				.build();

		Glossary copy = glossary.toBuilder().build();

		Glossary expected = Glossary.builder()
				.sourceLanguage("de")
				.targetLanguage("en")
				.entry(GlossaryEntry.builder()
						.source("Hallo")
						.target("Hey")
						.build())
				.build();

		assertEquals(expected, copy, "unexpected glossary");
	}
}
