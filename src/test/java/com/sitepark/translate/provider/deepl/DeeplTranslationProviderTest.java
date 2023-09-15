package com.sitepark.translate.provider.deepl;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.sitepark.translate.Format;
import com.sitepark.translate.SupportedLanguages;
import com.sitepark.translate.SupportedProvider;
import com.sitepark.translate.TranslationConfiguration;
import com.sitepark.translate.TranslationEvent;
import com.sitepark.translate.TranslationLanguage;
import com.sitepark.translate.TranslationListener;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressWarnings({
	"PMD.AvoidDuplicateLiterals",
	"PMD.JUnitTestContainsTooManyAsserts"
})
class DeeplTranslationProviderTest {

	@Test
	void testTranslation() {

		TranslationConfiguration config = Mockito.mock(TranslationConfiguration.class);
		when(config.isEncodePlaceholder()).thenReturn(true);
		TransportResponse response = Mockito.mock(TransportResponse.class);
		when(response.getTranslations()).thenReturn(new String[] {"Hello", "World"});

		DeeplTranslationProvider provider = new DeeplTranslationProvider(config) {
			@Override
			protected String[] translationRequest(
					Format format,
					TranslationLanguage language,
					String... source)
					throws IOException, InterruptedException {
				return new String[] {"Hello", "World"};
			}
		};

		TranslationLanguage language = TranslationLanguage.builder()
				.providerType(SupportedProvider.DEEPL)
				.source("de")
				.target("en")
				.build();

		String[] translated = provider.translate(Format.TEXT, language, new String[] {"Hallo", "Welt"});

		assertArrayEquals(
				new String[] {"Hello", "World"},
				translated,
				"unexpected translation");
	}

	@Test
	@SuppressFBWarnings("UWF_FIELD_NOT_INITIALIZED_IN_CONSTRUCTOR")
	void testTranslationListener() {

		TranslationListenerDummy listener = new TranslationListenerDummy();

		TranslationConfiguration config = Mockito.mock(TranslationConfiguration.class);
		when(config.isEncodePlaceholder()).thenReturn(true);
		when(config.getTranslationListener()).thenReturn(Optional.of(listener));
		TransportResponse response = Mockito.mock(TransportResponse.class);
		when(response.getTranslations()).thenReturn(new String[] {"Hello", "World"});

		DeeplTranslationProvider provider = new DeeplTranslationProvider(config) {
			@Override
			protected String[] translationRequest(
					Format format,
					TranslationLanguage language,
					String... source)
					throws IOException, InterruptedException {
				return new String[] {"Hello", "World"};
			}
		};

		TranslationLanguage language = TranslationLanguage.builder()
				.providerType(SupportedProvider.DEEPL)
				.source("de")
				.target("en")
				.build();

		provider.translate(Format.TEXT, language, new String[] {"Hallo", "Welt"});

		assertSame(
				language,
				listener.event.getTranslationLanguage(),
				"unexpected language");
		assertEquals(2, listener.event.getChunks(), "unexpected chunks");
		assertEquals(9, listener.event.getSourceBytes(), "unexpected sourceBytes");
		assertEquals(10, listener.event.getTargetBytes(), "unexpected targetBytes");
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

	private static final class TranslationListenerDummy implements TranslationListener {
		public TranslationEvent event;
		@Override
		public void translated(TranslationEvent event) {
			this.event = event;
		}
	}
}
