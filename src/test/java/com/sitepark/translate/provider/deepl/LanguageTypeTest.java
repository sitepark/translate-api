package com.sitepark.translate.provider.deepl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;


class LanguageTypeTest {

	@Test
	void testText() {
		assertEquals("source", LanguageType.SOURCE.toString(), "should be lower case");
	}

	@Test
	void testHtml() {
		assertEquals("target", LanguageType.TARGET.toString(), "should be lower case");
	}
}
