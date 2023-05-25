package com.sitepark.translate.provider.deepl;

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
class DeeplTranslationProviderConfigurationTest {

	@Test
	void testBuilder() throws URISyntaxException {

		DeeplTranslationProviderConfiguration providerConfig = DeeplTranslationProviderConfiguration.builder()
			.url("https://test")
			.authKey("abc")
			.proxy("sitepark.com", 8080)
			.build();

		assertEquals(new URI("https://test"), providerConfig.getUri(), "unexpected uri");
		assertEquals("abc", providerConfig.getAuthKey(), "unexpected authKey");
		assertTrue(providerConfig.getProxy().isPresent(), "proxy expected");
	}

	@Test
	void testToBuilder() throws URISyntaxException {

		DeeplTranslationProviderConfiguration providerConfig = DeeplTranslationProviderConfiguration.builder()
			.url("https://test")
			.authKey("abc")
			.proxy("sitepark.com", 8080)
			.build();

		providerConfig = providerConfig.toBuilder()
			.authKey("cde")
			.build();

		assertEquals(new URI("https://test"), providerConfig.getUri(), "unexpected uri");
		assertEquals("cde", providerConfig.getAuthKey(), "unexpected authKey");
		assertTrue(providerConfig.getProxy().isPresent(), "proxy expected");
	}

	@Test
	void testBuilderWithProxySelector() throws URISyntaxException {

		DeeplTranslationProviderConfiguration providerConfig = DeeplTranslationProviderConfiguration.builder()
			.url("https://test")
			.authKey("abc")
			.proxy(ProxySelector.of(new InetSocketAddress("sitepark.com", 8080)))
			.build();
		assertTrue(providerConfig.getProxy().isPresent(), "proxy expected");
	}

	@Test
	void testBuilderWithProxyInetSocketAddress() throws URISyntaxException {

		DeeplTranslationProviderConfiguration providerConfig = DeeplTranslationProviderConfiguration.builder()
			.url("https://test")
			.authKey("abc")
			.proxy(new InetSocketAddress("sitepark.com", 8080))
			.build();
		assertTrue(providerConfig.getProxy().isPresent(), "proxy expected");
	}

	@Test
	void testSetUrlToNull() throws URISyntaxException {
		assertThrows(AssertionError.class, () -> {
			DeeplTranslationProviderConfiguration.builder().url(null);
		});
	}

	@Test
	void testSetAuthKeyToNull() throws URISyntaxException {
		assertThrows(AssertionError.class, () -> {
			DeeplTranslationProviderConfiguration.builder().authKey(null);
		});
	}

	@Test
	void testSetProxyHostToNull() throws URISyntaxException {
		assertThrows(AssertionError.class, () -> {
			DeeplTranslationProviderConfiguration.builder().proxy(null, 8080);
		});
	}

	@Test
	void testSetProxyPortToZero() throws URISyntaxException {
		assertThrows(AssertionError.class, () -> {
			DeeplTranslationProviderConfiguration.builder().proxy("test.de", 0);
		});
	}

	@Test
	void testSetProxySelectorToNull() throws URISyntaxException {
		assertThrows(AssertionError.class, () -> {
			DeeplTranslationProviderConfiguration.builder().proxy((ProxySelector)null);
		});
	}

	@Test
	void testSetProxyInetSocketAddressToNull() throws URISyntaxException {
		assertThrows(AssertionError.class, () -> {
			DeeplTranslationProviderConfiguration.builder().proxy((InetSocketAddress)null);
		});
	}

	@Test
	void testMissingUri() throws URISyntaxException {
		assertThrows(AssertionError.class, () -> {
			DeeplTranslationProviderConfiguration.builder()
					.authKey("abc")
					.proxy("sitepark.com", 8080)
					.build();
		});
	}

	@Test
	void testMissingAuthKey() throws URISyntaxException {
		assertThrows(AssertionError.class, () -> {
			DeeplTranslationProviderConfiguration.builder()
					.url("https://test")
					.proxy("sitepark.com", 8080)
					.build();
		});
	}

	@Test
	void tetGetType() throws URISyntaxException {

		DeeplTranslationProviderConfiguration providerConfig = DeeplTranslationProviderConfiguration.builder()
			.url("https://test")
			.authKey("abc")
			.proxy("sitepark.com", 8080)
			.build();

		assertEquals(SupportedProvider.DEEPL, providerConfig.getType(), "unexpected type");
	}
}
