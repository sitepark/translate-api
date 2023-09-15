package com.sitepark.translate.translator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.sitepark.translate.Format;

class TranslatableTextTest {

	@Test
	void testAutoDetectPlainTextConstructor() {
		TranslatableText text = new TranslatableText("plain text");
		assertEquals(Format.TEXT, text.getFormat(), "format should be plain text");
	}

	@Test
	void testAutoDetectHtmlConstructor() {
		TranslatableText text = new TranslatableText("<strong>html text</strong>");
		assertEquals(Format.HTML, text.getFormat(), "format should be html");
	}

	@Test
	void testAutoDetectPlaceholderConstructor() {
		TranslatableText text = new TranslatableText("text with placeholder ${name}");
		assertEquals(Format.HTML, text.getFormat(), "format should be html");
	}

}
