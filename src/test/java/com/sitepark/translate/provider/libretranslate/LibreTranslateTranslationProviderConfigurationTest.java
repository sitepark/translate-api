package com.sitepark.translate.provider.libretranslate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URISyntaxException;

import org.junit.jupiter.api.Test;

import com.sitepark.translate.SupportedProvider;

@SuppressWarnings({
	"PMD.JUnitTestContainsTooManyAsserts",
	"PMD.AvoidDuplicateLiterals"
})
class LibreTranslateTranslationProviderConfigurationTest {

	@Test
	void testBuilder() throws URISyntaxException {

		LibreTranslateTranslationProviderConfiguration providerConfig =
				LibreTranslateTranslationProviderConfiguration.builder()
			.url("https://test")
			.apiKey("abc")
			.proxy("sitepark.com", 8080)
			.build();

		assertEquals(new URI("https://test"), providerConfig.getUri(), "unexpected uri");
		assertEquals("abc", providerConfig.getApiKey().get(), "unexpected authKey");
		assertTrue(providerConfig.getProxy().isPresent(), "proxy expected");
	}

	@Test
	void testToBuilder() throws URISyntaxException {

		LibreTranslateTranslationProviderConfiguration providerConfig =
				LibreTranslateTranslationProviderConfiguration.builder()
			.url("https://test")
			.apiKey("abc")
			.proxy("sitepark.com", 8080)
			.build();

		providerConfig = providerConfig.toBuilder()
			.apiKey("cde")
			.build();

		assertEquals(new URI("https://test"), providerConfig.getUri(), "unexpected uri");
		assertEquals("cde", providerConfig.getApiKey().get(), "unexpected authKey");
		assertTrue(providerConfig.getProxy().isPresent(), "proxy expected");
	}

	@Test
	void testBuilderWithProxySelector() throws URISyntaxException {

		LibreTranslateTranslationProviderConfiguration providerConfig =
				LibreTranslateTranslationProviderConfiguration.builder()
			.url("https://test")
			.apiKey("abc")
			.proxy(ProxySelector.of(new InetSocketAddress("sitepark.com", 8080)))
			.build();
		assertTrue(providerConfig.getProxy().isPresent(), "proxy expected");
	}

	@Test
	void testBuilderWithProxyInetSocketAddress() throws URISyntaxException {

		LibreTranslateTranslationProviderConfiguration providerConfig =
				LibreTranslateTranslationProviderConfiguration.builder()
			.url("https://test")
			.apiKey("abc")
			.proxy(new InetSocketAddress("sitepark.com", 8080))
			.build();
		assertTrue(providerConfig.getProxy().isPresent(), "proxy expected");
	}

	@Test
	void testSetUrlToNull() throws URISyntaxException {
		assertThrows(AssertionError.class, () -> {
			LibreTranslateTranslationProviderConfiguration.builder().url(null);
		});
	}

	@Test
	void testSetAuthKeyToNull() {
		assertThrows(AssertionError.class, () -> {
			LibreTranslateTranslationProviderConfiguration.builder().apiKey(null);
		});
	}

	@Test
	void testSetProxyHostToNull() {
		assertThrows(AssertionError.class, () -> {
			LibreTranslateTranslationProviderConfiguration.builder().proxy(null, 8080);
		});
	}

	@Test
	void testSetProxyPortToZero() {
		assertThrows(AssertionError.class, () -> {
			LibreTranslateTranslationProviderConfiguration.builder().proxy("test.de", 0);
		});
	}

	@Test
	void testSetProxySelectorToNull() throws URISyntaxException {
		assertThrows(AssertionError.class, () -> {
			LibreTranslateTranslationProviderConfiguration.builder().proxy((ProxySelector)null);
		});
	}

	@Test
	void testSetProxyInetSocketAddressToNull() throws URISyntaxException {
		assertThrows(AssertionError.class, () -> {
			LibreTranslateTranslationProviderConfiguration.builder().proxy((InetSocketAddress)null);
		});
	}

	@Test
	void testMissingUri() throws URISyntaxException {
		assertThrows(AssertionError.class, () -> {
			LibreTranslateTranslationProviderConfiguration.builder()
					.apiKey("abc")
					.proxy("sitepark.com", 8080)
					.build();
		});
	}

	@Test
	void tetGetType() throws URISyntaxException {

		LibreTranslateTranslationProviderConfiguration providerConfig =
				LibreTranslateTranslationProviderConfiguration.builder()
			.url("https://test")
			.apiKey("abc")
			.proxy("sitepark.com", 8080)
			.build();

		assertEquals(
				SupportedProvider.LIBRE_TRANSLATE,
				providerConfig.getType(),
				"unexpected type");
	}

}
