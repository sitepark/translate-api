package com.sitepark.translate.provider.libretranslate;

import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import com.sitepark.translate.SupportedProvider;
import com.sitepark.translate.TranslationProviderConfiguration;

public final class LibreTranslateTranslationProviderConfiguration implements TranslationProviderConfiguration {

	private final URI uri;

	private final ProxySelector proxy;

	private final String apiKey;

	private LibreTranslateTranslationProviderConfiguration(Builder builder) {
		this.uri = builder.uri;
		this.proxy = builder.proxy;
		this.apiKey = builder.apiKey;
	}

	@Override
	public SupportedProvider getType() {
		return SupportedProvider.LIBRE_TRANSLATE;
	}

	public URI getUri() {
		return this.uri;
	}

	public Optional<String> getApiKey() {
		return Optional.ofNullable(this.apiKey);
	}

	public Optional<ProxySelector> getProxy() {
		return Optional.ofNullable(this.proxy);
	}

	public static Builder builder() {
		return new Builder();
	}

	public Builder toBuilder() {
		return new Builder(this);
	}

	@SuppressWarnings("PMD.TooManyMethods")
	public final static class Builder {

		private URI uri;

		private ProxySelector proxy;

		private String apiKey;

		private Builder() { }

		private Builder(LibreTranslateTranslationProviderConfiguration translatorConfiguration) {
			this.uri = translatorConfiguration.uri;
			this.proxy = translatorConfiguration.proxy;
			this.apiKey = translatorConfiguration.apiKey;
		}

		public Builder url(String url) throws URISyntaxException {
			assert url != null : "url is null";
			this.uri = new URI(url);
			return this;
		}

		public Builder apiKey(String apiKey) {
			assert apiKey != null : "apiKey is null";
			this.apiKey = apiKey;
			return this;
		}

		public Builder proxy(ProxySelector proxy) {
			assert proxy != null : "proxy is null";
			this.proxy = proxy;
			return this;
		}

		public Builder proxy(InetSocketAddress proxy) {
			assert proxy != null : "proxy is null";
			this.proxy = ProxySelector.of(proxy);
			return this;
		}

		public Builder proxy(String host, int port) {
			assert host != null : "host is null";
			assert port > 0 : "port <= 0";
			this.proxy = ProxySelector.of(new InetSocketAddress(host, port));
			return this;
		}

		public LibreTranslateTranslationProviderConfiguration build() {
			assert this.uri != null : "uri is null";
			return new LibreTranslateTranslationProviderConfiguration(this);
		}
	}
}
