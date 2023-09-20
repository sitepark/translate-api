package com.sitepark.translate.provider.libretranslate;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.sitepark.translate.Format;
import com.sitepark.translate.SupportedProvider;
import com.sitepark.translate.TranslationConfiguration;
import com.sitepark.translate.TranslationEvent;
import com.sitepark.translate.TranslationLanguage;
import com.sitepark.translate.TranslationListener;
import com.sitepark.translate.provider.deepl.DeeplTranslationProvider;
import com.sitepark.translate.provider.deepl.LanguageType;
import com.sitepark.translate.provider.deepl.TransportLanguage;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressWarnings({
	"PMD.AvoidDuplicateLiterals",
	"PMD.JUnitTestContainsTooManyAsserts"
})
class LibreTranslateTranslationProviderTest {

	@Test
	void testTranslation() {

		TranslationConfiguration config = Mockito.mock(TranslationConfiguration.class);
		when(config.isEncodePlaceholder()).thenReturn(true);

		LibreTranslateTranslationProvider provider = new HelloWorldLibreTranslateTranslationProvider(config);

		TranslationLanguage language = TranslationLanguage.builder()
				.providerType(SupportedProvider.LIBRE_TRANSLATE)
				.source("de")
				.target("en")
				.build();

		String[] translated = provider.translate(
				Format.TEXT,
				language,
				new String[] {"Hallo", "Welt"});

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
		when(config.getTranslationListener()).thenReturn(Optional.of(listener));
		when(config.isEncodePlaceholder()).thenReturn(true);

		LibreTranslateTranslationProvider provider = new HelloWorldLibreTranslateTranslationProvider(config);

		TranslationLanguage language = TranslationLanguage.builder()
				.providerType(SupportedProvider.LIBRE_TRANSLATE)
				.source("de")
				.target("en")
				.build();

		provider.translate(
				Format.TEXT,
				language,
				new String[] {"Hallo", "Welt"});

		assertNotNull(listener.event, "event expected");
		assertSame(
				language,
				listener.event.getTranslationLanguage(),
				"unexpected language");
		assertEquals(2, listener.event.getChunks(), "unexpected chunks");
		assertEquals(9, listener.event.getSourceBytes(), "unexpected sourceBytes");
		assertEquals(10, listener.event.getTargetBytes(), "unexpected targetBytes");
	}

	private static final class TranslationListenerDummy implements TranslationListener {
		public TranslationEvent event;
		@Override
		public void translated(TranslationEvent event) {
			this.event = event;
		}
	}

	private static final class HelloWorldLibreTranslateTranslationProvider extends LibreTranslateTranslationProvider {

		public HelloWorldLibreTranslateTranslationProvider(TranslationConfiguration translatorConfiguration) {
			super(translatorConfiguration);
		}

		@Override
		protected String[] translationRequest(
				Format format,
				TranslationLanguage language,
				String... source)
				throws IOException, InterruptedException {
			return new String[] {"Hello", "World"};
		}
	}

}
