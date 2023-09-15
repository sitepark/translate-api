package com.sitepark.translate.translator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import com.sitepark.translate.Format;
import com.sitepark.translate.SupportedProvider;
import com.sitepark.translate.TranslationConfiguration;
import com.sitepark.translate.TranslationLanguage;
import com.sitepark.translate.TranslationProvider;
import com.sitepark.translate.TranslationProviderFactory;

class JsonStringTranslatorTest {

	@Test
	void test() throws Exception {

		TranslationProvider transporter = mock(TranslationProvider.class);
		when(transporter.translate(
				any(Format.class),
				any(TranslationLanguage.class),
				any(String[].class)))
		.thenReturn(new String[] {
				"Heading",
				"A beautiful text",
				"Flowers"
		});

		TranslationProviderFactory transporterFactory = mock(TranslationProviderFactory.class);
		when(transporterFactory.create(any())).thenReturn(transporter);

		TranslationConfiguration translatorConfiguration = TranslationConfiguration.builder()
				.translationProviderFactory(transporterFactory)
				.build();


		JsonStringTranslator translator = JsonStringTranslator.builder()
				.translatorConfiguration(translatorConfiguration)
				.build();

		TranslationLanguage language = TranslationLanguage.builder()
				.providerType(SupportedProvider.LIBRE_TRANSLATE)
				.source("de")
				.target("en")
				.build();

		String result = translator.translate(language,
				"{\n"
				+ "    \"headline\":\"Überschrift\",\n"
				+ "    \"text\":\"Ein schöner Text\",\n"
				+ "    \"items\" : [\n"
				+ "        {\n"
				+ "            \"text\" : \"Blumen\",\n"
				+ "            \"number\" : 10,\n"
				+ "            \"boolean\" :true\n"
				+ "        }\n"
				+ "    ]\n"
				+ "}");

		String expected = "{\"headline\":\"Heading\"," +
				"\"text\":\"A beautiful text\"," +
				"\"items\":[{\"text\":\"Flowers\",\"number\":10,\"boolean\":true}]}";

		assertEquals(expected, result, "unexpected json data");
	}

}
