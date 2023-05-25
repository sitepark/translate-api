package com.sitepark.translate;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

@SuppressWarnings({
	"PMD.JUnitTestContainsTooManyAsserts"
})
class TranslationProviderConfigurationTest {

	@Test
	void testBuilder() {

		TranslationCache translationCache =
				Mockito.mock(TranslationCache.class);
		TranslationListener translationListener =
				Mockito.mock(TranslationListener.class);
		TranslationProviderFactory translationProviderFactory =
				Mockito.mock(TranslationProviderFactory.class);
		TranslationProviderConfiguration translationProviderConfiguration =
				Mockito.mock(TranslationProviderConfiguration.class);
		when(translationProviderConfiguration.getType()).thenReturn(SupportedProvider.DEEPL);

		TranslationConfiguration config = TranslationConfiguration.builder()
				.translationCache(translationCache)
				.translationListener(translationListener)
				.translationProviderConfiguration(translationProviderConfiguration)
				.translationProviderFactory(translationProviderFactory)
				.encodePlaceholder(true)
				.build();

		assertSame(
				translationCache,
				config.getTranslationCache().get(),
				"unexpected translationCache");
		assertSame(
				translationListener,
				config.getTranslationListener().get(),
				"unexpected translationListener");
		assertSame(
				translationProviderFactory,
				config.getTranslationProviderFactory(),
				"unexpected translationProviderFactory");
		assertArrayEquals(
				new SupportedProvider[] {translationProviderConfiguration.getType()},
				config.getConfiguredProvider(),
				"unexpected translationProviderConfiguration");
		assertEquals(
				true,
				config.isEncodePlaceholder(),
				"unexpected encodePlaceholder");
	}

	@Test
	void testGetTranslationProviderConfiguration() {

		TranslationConfiguration config = TranslationConfiguration.builder()
				.translationProviderConfiguration(new TranslationProviderConfigurationDummy())
				.build();

		TranslationProviderConfigurationDummy testProviderConfiguration =
				config.getTranslationProviderConfiguration(TranslationProviderConfigurationDummy.class);

		assertNotNull(
				testProviderConfiguration,
				"expect non null providerConfiguration");
	}

	@Test
	void testMissingTranslationProviderConfiguration() {

		TranslationConfiguration config = TranslationConfiguration.builder()
				.build();

		assertThrows(IllegalArgumentException.class, () -> {
			config.getTranslationProviderConfiguration(TranslationProviderConfigurationDummy.class);
		});
	}

	@Test
	void testToBuilder() {

		TranslationCache translationCache =
				Mockito.mock(TranslationCache.class);
		TranslationListener translationListener =
				Mockito.mock(TranslationListener.class);
		TranslationProviderFactory translationProviderFactory =
				Mockito.mock(TranslationProviderFactory.class);
		TranslationProviderConfiguration translationProviderConfiguration =
				Mockito.mock(TranslationProviderConfiguration.class);
		when(translationProviderConfiguration.getType()).thenReturn(SupportedProvider.DEEPL);

		TranslationConfiguration config = TranslationConfiguration.builder()
				.translationCache(translationCache)
				.translationListener(translationListener)
				.translationProviderConfiguration(translationProviderConfiguration)
				.translationProviderFactory(translationProviderFactory)
				.encodePlaceholder(true)
				.build();

		TranslationListener translationListener2 =
				Mockito.mock(TranslationListener.class);

		config = config.toBuilder()
				.translationListener(translationListener2)
				.build();

		assertSame(
				translationCache,
				config.getTranslationCache().get(),
				"unexpected translationCache");
		assertSame(
				translationListener2,
				config.getTranslationListener().get(),
				"unexpected translationListener");
		assertSame(
				translationProviderFactory,
				config.getTranslationProviderFactory(),
				"unexpected translationProviderFactory");
		assertArrayEquals(
				new SupportedProvider[] {translationProviderConfiguration.getType()},
				config.getConfiguredProvider(),
				"unexpected translationProviderConfiguration");
		assertEquals(
				true,
				config.isEncodePlaceholder(),
				"unexpected encodePlaceholder");
	}

	@Test
	void testSetTranslationCacheToNull() {
		assertThrows(AssertionError.class, () -> {
			TranslationConfiguration.builder().translationCache(null);
		});
	}

	@Test
	void testSetTranslationListenerToNull() {
		assertThrows(AssertionError.class, () -> {
			TranslationConfiguration.builder().translationListener(null);
		});
	}

	@Test
	void testSetTranslationProviderFactoryToNull() {
		assertThrows(AssertionError.class, () -> {
			TranslationConfiguration.builder().translationProviderFactory(null);
		});
	}

	@Test
	void testSetTranslationProviderConfigurationToNull() {
		assertThrows(AssertionError.class, () -> {
			TranslationConfiguration.builder().translationProviderConfiguration(null);
		});
	}

	private static final class TranslationProviderConfigurationDummy
			implements TranslationProviderConfiguration {

		@Override
		public SupportedProvider getType() {
			return SupportedProvider.DEEPL;
		}

	}
}
