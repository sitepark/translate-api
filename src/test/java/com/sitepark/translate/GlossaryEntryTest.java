package com.sitepark.translate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

class GlossaryEntryTest {

	@Test
	@SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
	public void testEqualsContract() {
		EqualsVerifier.forClass(GlossaryEntry.class).verify();
	}

	@Test
	public void testSetSource() {
		GlossaryEntry entry = GlossaryEntry.builder()
				.source("de")
				.target("en")
				.build();
		assertEquals("de", entry.getSource(), "unexpected source");
	}

	@Test
	public void testNullSource() {
		assertThrows(NullPointerException.class, () -> {
			GlossaryEntry.builder().source(null);
		}, "Setting null as source should not be allowed");
	}

	@Test
	public void testBlankSource() {
		assertThrows(IllegalArgumentException.class, () -> {
			GlossaryEntry.builder().source(" ");
		}, "Setting blank string as source should not be allowed");
	}

	@Test
	public void testUnsetSource() {
		assertThrows(IllegalStateException.class, () -> {
			GlossaryEntry.builder().target("en").build();
		}, "Unset source should not be allowed");
	}

	@Test
	public void testSetTarget() {
		GlossaryEntry entry = GlossaryEntry.builder()
				.source("de")
				.target("en")
				.build();
		assertEquals("en", entry.getTarget(), "unexpected source");
	}

	@Test
	public void testNullTarget() {
		assertThrows(NullPointerException.class, () -> {
			GlossaryEntry.builder().target(null);
		}, "Setting null as target should not be allowed");
	}

	@Test
	public void testBlankTarget() {
		assertThrows(IllegalArgumentException.class, () -> {
			GlossaryEntry.builder().target(" ");
		}, "Setting blank string as target should not be allowed");
	}

	@Test
	public void testUnsetTarget() {
		assertThrows(IllegalStateException.class, () -> {
			GlossaryEntry.builder().source("de").build();
		}, "Unset source should not be allowed");
	}

	@Test
	public void testToBuilder() {

		GlossaryEntry entry = GlossaryEntry.builder()
				.source("de")
				.target("en")
				.build();

		GlossaryEntry copy = entry.toBuilder().build();

		assertEquals("de", copy.getSource(), "unexpected source");
		assertEquals("en", copy.getTarget(), "unexpected target");
	}
}
