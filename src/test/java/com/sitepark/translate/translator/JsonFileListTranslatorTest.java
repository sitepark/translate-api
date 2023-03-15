package com.sitepark.translate.translator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.sitepark.translate.Language;
import com.sitepark.translate.SupportedLanguages;
import com.sitepark.translate.SupportedProvider;
import com.sitepark.translate.TranslationConfiguration;
import com.sitepark.translate.TranslationProvider;
import com.sitepark.translate.TranslationProviderFactory;

class JsonFileListTranslatorTest {

	@Test
	void test() throws Exception {

		SupportedLanguages supportedLanguages = SupportedLanguages.builder()
				.language(Language.builder()
						.code("de")
						.name("deutsch")
						.targets("en")
				)
				.build();

		Map<String, String> dictionary = new HashMap<>();
		dictionary.put("Hallo", "Hello");
		dictionary.put("Welt", "World");

		TranslationProvider transporter = mock(TranslationProvider.class);
		when(transporter.translate(any(), any())).thenAnswer(invocationOnMock -> {
			Object[] arguments = invocationOnMock.getArguments();
			String[] translations = new String[arguments.length];
			for (int i = 0; i < arguments.length; i++) {
				translations[i] = dictionary.get(arguments[i].toString());
			}
			return translations;
		});
		when(transporter.getSupportedLanguages()).thenReturn(supportedLanguages);

		TranslationProviderFactory transporterFactory = mock(TranslationProviderFactory.class);
		when(transporterFactory.create(any())).thenReturn(transporter);

		TranslationConfiguration translatorConfiguration = TranslationConfiguration.builder()
				.transporterFactory(transporterFactory)
				.build();

		Path dir = Paths.get("src/test/resources/JsonFileListTranslator");
		Path output = Paths.get("target/test/JsonFileListTranslator/translations");

		JsonFileListTranslator jsonFileListTranslator = JsonFileListTranslator.builder()
				.dir(dir)
				.output(output)
				.sourceLang("de")
				.targetLangList("en")
				.translatorConfiguration(translatorConfiguration)
				.build();

		jsonFileListTranslator.translate(SupportedProvider.LIBRE_TRANSLATE);

		Path resultA = output.resolve("en/a.json");
		assertEquals("{\n"
				+ "  \"text\" : \"World\"\n"
				+ "}", Files.readString(resultA),
				"wrong content in en/a.json");

		Path resultC = output.resolve("en/b/c.json");
		assertEquals("{\n"
				+ "  \"d\" : \"Hello\"\n"
				+ "}", Files.readString(resultC),
				"wrong content in en/b/c.json");
	}
}
