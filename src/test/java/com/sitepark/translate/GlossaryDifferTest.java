package com.sitepark.translate;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class GlossaryDifferTest {

	@Test
	void test() {

		Glossary a = Glossary.builder()
				.sourceLanguage("de")
				.targetLanguage("en")
				.entry(GlossaryEntry.builder()
						.source("Hallo")
						.target("Hello")
						.build()
				)
				.entry(GlossaryEntry.builder()
						.source("Blau")
						.target("Blue")
						.build()
				)
				.build();

		Glossary b = Glossary.builder()
				.sourceLanguage("de")
				.targetLanguage("en")
				.entry(GlossaryEntry.builder()
						.source("Hallo")
						.target("Hello")
						.build()
				)
				.entry(GlossaryEntry.builder()
						.source("Grün")
						.target("Green")
						.build()
				)
				.build();

		GlossaryDiffer differ = new GlossaryDiffer(a, b);
		GlossaryChangeSet changeSet = differ.diff();

		GlossaryChangeSet expected = new GlossaryChangeSet();
		expected.added(GlossaryEntry.builder()
				.source("Grün")
				.target("Green")
				.build());
		expected.deleted(GlossaryEntry.builder()
				.source("Blau")
				.target("Blue")
				.build());

		assertEquals(expected, changeSet, "unexpected changeset");
	}

}
