package com.sitepark.translate.translator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.sitepark.translate.Language;
import com.sitepark.translate.SupportedLanguages;
import com.sitepark.translate.SupportedProvider;
import com.sitepark.translate.TranslationConfiguration;
import com.sitepark.translate.TranslationProvider;
import com.sitepark.translate.TranslationProviderFactory;
import com.sitepark.translate.TranslationRequest;
import com.sitepark.translate.TranslationResult;
import com.sitepark.translate.TranslationResultStatistic;

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
		when(transporter.translate(any(TranslationRequest.class))).thenAnswer(invocationOnMock -> {

			TranslationRequest req = (TranslationRequest)invocationOnMock.getArguments()[0];
			String[] sourceText = req.getSourceText();
			String[] translations = new String[sourceText.length];
			for (int i = 0; i < translations.length; i++) {
				translations[i] = dictionary.get(sourceText[i]);
			}

			return TranslationResult.builder()
					.request(req)
					.text(translations)
					.statistic(TranslationResultStatistic.EMPTY)
					.build();
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
				+ "}", Files.readString(resultA, StandardCharsets.UTF_8),
				"wrong content in en/a.json");

		Path resultC = output.resolve("en/b/c.json");
		assertEquals("{\n"
				+ "  \"d\" : \"World\"\n"
				+ "}", Files.readString(resultC, StandardCharsets.UTF_8),
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
