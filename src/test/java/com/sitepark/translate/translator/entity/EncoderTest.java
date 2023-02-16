package com.sitepark.translate.translator.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class EncoderTest {

	@Test
	void test() {
		String s = Encoder.encode("Ein Text mit ${text}.");
		assertEquals(
				"Ein Text mit <span data-encoded-entity=\"true\" translate=\"no\">${text}</span>.",
				s,
				"Unexpected encoding");
	}

}
