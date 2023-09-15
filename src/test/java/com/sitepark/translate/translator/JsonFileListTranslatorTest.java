package com.sitepark.translate.translator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.sitepark.translate.Format;
import com.sitepark.translate.Language;
import com.sitepark.translate.SupportedLanguages;
import com.sitepark.translate.SupportedProvider;
import com.sitepark.translate.TranslationConfiguration;
import com.sitepark.translate.TranslationLanguage;
import com.sitepark.translate.TranslationProvider;
import com.sitepark.translate.TranslationProviderFactory;

class JsonFileListTranslatorTest {

	@Test
	@SuppressWarnings({"PMD.UseConcurrentHashMap"})
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
		when(transporter.translate(
				any(Format.class),
				any(TranslationLanguage.class),
				any(String[].class))).thenAnswer(invocationOnMock -> {
			int argsBeforStringArray = 2;
			Object[] arguments = invocationOnMock.getArguments();
			String[] translations = new String[arguments.length - argsBeforStringArray];
			for (int i = 0; i < (arguments.length - argsBeforStringArray); i++) {
				translations[i] = dictionary.get(arguments[i + argsBeforStringArray].toString());
			}
			return translations;
		});
		when(transporter.getSupportedLanguages()).thenReturn(supportedLanguages);

		TranslationProviderFactory transporterFactory = mock(TranslationProviderFactory.class);
		when(transporterFactory.create(any())).thenReturn(transporter);

		TranslationConfiguration translatorConfiguration = TranslationConfiguration.builder()
				.translationProviderFactory(transporterFactory)
				.build();

		Path dir = Paths.get("src/test/resources/JsonFileListTranslator");
		Path output = Paths.get("target/test/JsonFileListTranslator/translations");
		this.cleanCache(output);

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
				+ "  \"text\" : \"Hello\"\n"
				+ "}", Files.readString(resultA),
				"wrong content in en/a.json");

		Path resultC = output.resolve("en/b/c.json");
		assertEquals("{\n"
				+ "  \"d\" : \"World\"\n"
				+ "}", Files.readString(resultC),
				"wrong content in en/b/c.json");
	}

	private void cleanCache(Path dir) throws IOException {
		if (!Files.exists(dir)) {
			return;
		}
		Files.walk(dir)
				.sorted(Comparator.reverseOrder())
				.map(Path::toFile)
				.forEach(File::delete);
	}
}
