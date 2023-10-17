package com.sitepark.translate.provider.deepl;

import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.Optional;

import com.sitepark.translate.SupportedProvider;
import com.sitepark.translate.TranslationProviderConfiguration;

public final class DeeplTranslationProviderConfiguration implements TranslationProviderConfiguration {

	private final URI uri;

	private final ProxySelector proxy;

	private final String authKey;

	private DeeplTranslationProviderConfiguration(Builder builder) {
		this.uri = builder.uri;
		this.proxy = builder.proxy;
		this.authKey = builder.authKey;
	}

	@Override
	public SupportedProvider getType() {
		return SupportedProvider.DEEPL;
	}

	public URI getUri() {
		return this.uri;
	}

	public String getAuthKey() {
		return this.authKey;
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

		private String authKey;

		private Builder() { }

		private Builder(DeeplTranslationProviderConfiguration translatorConfiguration) {
			this.uri = translatorConfiguration.uri;
			this.proxy = translatorConfiguration.proxy;
			this.authKey = translatorConfiguration.authKey;
		}

		public Builder url(String url) throws URISyntaxException {
			Objects.requireNonNull(url, "url is null");
			this.uri = new URI(url);
			return this;
		}

		public Builder authKey(String authKey) {
			Objects.requireNonNull(authKey, "authKey is null");
			this.authKey = authKey;
			return this;
		}

		public Builder proxy(ProxySelector proxy) {
			Objects.requireNonNull(proxy, "proxy is null");
			this.proxy = proxy;
			return this;
		}

		public Builder proxy(InetSocketAddress proxy) {
			Objects.requireNonNull(proxy, "proxy is null");
			this.proxy = ProxySelector.of(proxy);
			return this;
		}

		public Builder proxy(String host, int port) {
			Objects.requireNonNull(host, "host is null");
			if (port <= 0) {
				throw new IllegalArgumentException("port should be greater than 0");
			}
			this.proxy = ProxySelector.of(new InetSocketAddress(host, port));
			return this;
		}

		public DeeplTranslationProviderConfiguration build() {
			if (this.uri == null) {
				throw new IllegalStateException("uri not set");
			}
			if (this.authKey == null) {
				throw new IllegalStateException("authKey not set");
			}
			return new DeeplTranslationProviderConfiguration(this);
		}
	}
}
