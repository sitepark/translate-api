package com.sitepark.translate;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

class LanguageTest {

	@Test
	@SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
	void testDeserialize() throws Exception {
		String json = "{\"code\":\"de\", \"name\":\"deutsch\",\"targets\":[\"en\",\"it\"]}";
		ObjectMapper mapper = new ObjectMapper();
		Language language = mapper.readValue(json, Language.class);
		assertEquals("de", language.getCode(), "wrong code");
		assertEquals("deutsch", language.getName(), "wrong name");
		assertEquals(Arrays.asList(new String[] {"en", "it"}), language.getTargets(), "wrong name");
	}

}
