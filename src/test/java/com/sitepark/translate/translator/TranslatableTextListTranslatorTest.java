package com.sitepark.translate.translator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.sitepark.translate.SupportedProvider;
import com.sitepark.translate.TranslationCache;
import com.sitepark.translate.TranslationConfiguration;
import com.sitepark.translate.TranslationLanguage;
import com.sitepark.translate.TranslationProvider;
import com.sitepark.translate.TranslationProviderFactory;

class TranslatableTextListTranslatorTest {

	@Test
	@SuppressWarnings("PMD.AvoidDuplicateLiterals")
	void test() throws Exception {

		TranslationProvider transporter = mock(TranslationProvider.class);
		when(transporter.translate(any(TranslationLanguage.class), any(String[].class))).thenReturn(new String[] {
				"Flowers",
				"Blue"
		});

		TranslationProviderFactory transporterFactory = mock(TranslationProviderFactory.class);
		when(transporterFactory.create(any())).thenReturn(transporter);

		TranslationConfiguration translatorConfiguration = TranslationConfiguration.builder()
				.translationProviderFactory(transporterFactory)
				.build();

		List<TranslatableText> translatableTextList = new ArrayList<>();
		translatableTextList.add(new TranslatableText("Blume"));
		translatableTextList.add(new TranslatableText("Blau"));

		TranslatableTextListTranslator translator = TranslatableTextListTranslator.builder()
				.translatorConfiguration(translatorConfiguration)
				.build();

		TranslationLanguage language = TranslationLanguage.builder()
				.providerType(SupportedProvider.LIBRE_TRANSLATE)
				.source("de")
				.target("en")
				.build();

		translator.translate(language, translatableTextList);

		assertEquals("Flowers", translatableTextList.get(0).getTargetText(), "unexpected translation");
		assertEquals("Blue", translatableTextList.get(1).getTargetText(), "unexpected translation");
	}

	@Test
	void testWithCache() throws Exception {

		TranslationProvider transporter = mock(TranslationProvider.class);
		when(transporter.translate(any(), any())).thenReturn(new String[] {
				"Blue"
		});

		TranslationCache translationCache = mock(TranslationCache.class);
		// last match wins
		when(translationCache.translate(any())).thenReturn(Optional.empty());
		when(translationCache.translate("Blume")).thenReturn(Optional.of("Flowers"));

		TranslationProviderFactory transporterFactory = mock(TranslationProviderFactory.class);
		when(transporterFactory.create(any())).thenReturn(transporter);

		TranslationConfiguration translatorConfiguration = TranslationConfiguration.builder()
				.translationProviderFactory(transporterFactory)
				.translationCache(translationCache)
				.build();


		List<TranslatableText> translatableTextList = new ArrayList<>();
		translatableTextList.add(new TranslatableText("Blume"));
		translatableTextList.add(new TranslatableText("Blau"));

		TranslatableTextListTranslator translator = TranslatableTextListTranslator.builder()
				.translatorConfiguration(translatorConfiguration)
				.build();

		TranslationLanguage language = TranslationLanguage.builder()
				.providerType(SupportedProvider.LIBRE_TRANSLATE)
				.source("de")
				.target("en")
				.build();

		translator.translate(language, translatableTextList);

		assertEquals("Flowers", translatableTextList.get(0).getTargetText(), "unexpected translation");
		assertEquals("Blue", translatableTextList.get(1).getTargetText(), "unexpected translation");
	}
}
