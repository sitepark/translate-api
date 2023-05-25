package com.sitepark.translate.provider.deepl;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;

class JsonBodyHandlerTest {

	@Test
	void test() {
		String json = "{\"translations\":[" +
				"{\"detected_source_language\":\"de\", \"text\":\"Hello\"}," +
				"{\"detected_source_language\":\"de\", \"text\":\"World\"}" +
		"]}";
		ByteArrayInputStream in = new ByteArrayInputStream(
				json.getBytes(StandardCharsets.UTF_8));

		Supplier<TransportResponse> supplier =
				JsonBodyHandler.toSupplierOfType(in, TransportResponse.class);

		TransportResponse res = supplier.get();

		String[] translations = res.getTranslations();

		assertArrayEquals(
				new String[] {"Hello", "World"},
				translations,
				"unexpected translations");
	}

}
