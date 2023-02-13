package com.sitepark.translate.translator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import com.sitepark.translate.SupportedProvider;
import com.sitepark.translate.TranslationConfiguration;
import com.sitepark.translate.TranslationLanguage;
import com.sitepark.translate.TranslationProvider;
import com.sitepark.translate.TranslationProviderFactory;

class StringTranslatorTest {

	@Test
	void test() throws Exception {

		TranslationProvider transporter = mock(TranslationProvider.class);
		when(transporter.translate(any(), any())).thenReturn(new String[] {"Hello"});

		TranslationProviderFactory transporterFactory = mock(TranslationProviderFactory.class);
		when(transporterFactory.create(any())).thenReturn(transporter);

		TranslationConfiguration translatorConfiguration = TranslationConfiguration.builder()
				.transporterFactory(transporterFactory)
				.build();

		StringTranslator translator = StringTranslator.builder()
				.translatorConfiguration(translatorConfiguration)
				.build();

		TranslationLanguage language = TranslationLanguage.builder()
				.providerType(SupportedProvider.LIBRE_TRANSLATE)
				.source("de")
				.target("en")
				.build();

		String result = translator.translate(language, "Hallo");

		assertEquals("Hello", result, "wrong translation");
	}
}
