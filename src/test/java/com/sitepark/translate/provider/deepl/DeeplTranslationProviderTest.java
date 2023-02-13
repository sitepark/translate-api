package com.sitepark.translate.provider.deepl;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.sitepark.translate.SupportedLanguages;
import com.sitepark.translate.TranslationLanguage;
import com.sitepark.translate.TranslationConfiguration;

@Disabled
class DeeplTranslationProviderTest {

	private DeeplTranslationProvider createProvider() throws IOException, URISyntaxException {
		DeeplTestConnection con = DeeplTestConnection.get();
		return new DeeplTranslationProvider(
				TranslationConfiguration.builder()
						.build(),
				DeeplTranslationProviderConfiguration.builder()
						.url(con.getUrl())
						.authKey(con.getAuthKey())
				.build()
		);
	}

	@Test
	void testSupportedLanguages() throws URISyntaxException, IOException, InterruptedException {

		DeeplTranslationProvider translator = this.createProvider();

		SupportedLanguages supportedLanguages = translator.getSupportedLanguages();

		assertTrue(supportedLanguages.getAll().size() > 0, "supportedLanguages should not be empty");
	}

	@Test
	void testTranslate() throws URISyntaxException, IOException, InterruptedException {

		DeeplTranslationProvider translator = this.createProvider();

		TranslationLanguage translationLanguage = TranslationLanguage.builder()
				.source("de")
				.target("en")
				.build();

		String[] res = translator.translate(translationLanguage, new String[] {
				"Hallo", "Welt"
		});

		assertArrayEquals(new String[] {"Hello", "World"}, res, "Unexpected translation");
	}

}
