package com.sitepark.translate.provider.deepl;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.sitepark.translate.SupportedLanguages;
import com.sitepark.translate.SupportedProvider;
import com.sitepark.translate.TranslationConfiguration;
import com.sitepark.translate.TranslationLanguage;
import com.sitepark.translate.translator.UnifiedSourceText;

class DeeplTranslationProviderTest {

	@Test
	void testTranslation() {

		TranslationConfiguration config = Mockito.mock(TranslationConfiguration.class);
		when(config.isEncodePlaceholder()).thenReturn(true);
		TransportResponse response = Mockito.mock(TransportResponse.class);
		when(response.getTranslations()).thenReturn(new String[] {"Hello", "World"});

		DeeplTranslationProvider provider = new DeeplTranslationProvider(config) {
			@Override
			protected TransportResponse translationRequest(
					TranslationLanguage language,
					UnifiedSourceText unifiedSourceText)
					throws IOException, InterruptedException {
				return response;
			}
		};

		TranslationLanguage language = TranslationLanguage.builder()
				.providerType(SupportedProvider.DEEPL)
				.source("de")
				.target("en")
				.build();

		String[] translated = provider.translate(language, new String[] {"Hallo", "Welt"});

		assertArrayEquals(
				new String[] {"Hello", "World"},
				translated,
				"unexpected translation");
	}

	@Test
	void testGetSupportedLanguages() {

		TranslationConfiguration config = Mockito.mock(TranslationConfiguration.class);
		when(config.isEncodePlaceholder()).thenReturn(true);
		TransportResponse response = Mockito.mock(TransportResponse.class);
		when(response.getTranslations()).thenReturn(new String[] {"Hello", "World"});

		TransportLanguage de = Mockito.mock(TransportLanguage.class);
		when(de.getLanguage()).thenReturn("de");
		when(de.getName()).thenReturn("deutsch");
		TransportLanguage en = Mockito.mock(TransportLanguage.class);
		when(en.getLanguage()).thenReturn("en");
		when(en.getName()).thenReturn("english");

		DeeplTranslationProvider provider = new DeeplTranslationProvider(config) {
			@Override
			protected List<TransportLanguage> getLanguages(LanguageType type) {
				if (type == LanguageType.SOURCE) {
					return Arrays.asList(de, en);
				} else if (type == LanguageType.TARGET) {
					return Arrays.asList(en);
				}
				throw new IllegalArgumentException("Unsupported type: '" + type + "'");
			}
		};

		SupportedLanguages supportedLanguages = provider.getSupportedLanguages();

		assertEquals(
				"de",
				supportedLanguages.getSourceLanguage("de").get().getCode(),
				"unexpected source language");
		assertEquals(
				"en",
				supportedLanguages.getTargetLanguage("de", "en").get().getCode(),
				"unexpected source language");
	}
}
