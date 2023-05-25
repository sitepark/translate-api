package com.sitepark.translate.provider.libretranslate;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.sitepark.translate.SupportedProvider;
import com.sitepark.translate.TranslationConfiguration;
import com.sitepark.translate.TranslationLanguage;
import com.sitepark.translate.translator.UnifiedSourceText;

class LibreTranslateTranslationProviderTest {

	@Test
	void testParseArgument() {

		TranslationConfiguration config = Mockito.mock(TranslationConfiguration.class);
		when(config.isEncodePlaceholder()).thenReturn(true);
		TransportResponse response = Mockito.mock(TransportResponse.class);
		when(response.getTranslatedText()).thenReturn(new String[] {"Hello", "World"});

		LibreTranslateTranslationProvider provider = new LibreTranslateTranslationProvider(config) {
			@Override
			protected TransportResponse translationRequest(
					TranslationLanguage language,
					UnifiedSourceText unifiedSourceText)
					throws IOException, InterruptedException {
				return response;
			}
		};

		TranslationLanguage language = TranslationLanguage.builder()
				.providerType(SupportedProvider.LIBRE_TRANSLATE)
				.source("de")
				.target("en")
				.build();

		String[] translated = provider.translate(language, new String[] {"Hallo", "Welt"});

		assertArrayEquals(
				new String[] {"Hello", "World"},
				translated,
				"unexpected translation");
	}

}
