package com.sitepark.translate.provider.libretranslate;

import com.sitepark.translate.SupportedProvider;
import com.sitepark.translate.TranslationProviderConfiguration;
import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.Optional;

public final class LibreTranslateTranslationProviderConfiguration
    implements TranslationProviderConfiguration {

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
  public static final class Builder {

    private URI uri;

    private ProxySelector proxy;

    private String apiKey;

    private Builder() {}

    private Builder(LibreTranslateTranslationProviderConfiguration translatorConfiguration) {
      this.uri = translatorConfiguration.uri;
      this.proxy = translatorConfiguration.proxy;
      this.apiKey = translatorConfiguration.apiKey;
    }

    public Builder url(String url) throws URISyntaxException {
      Objects.requireNonNull(url, "url is null");
      this.uri = new URI(url);
      return this;
    }

    public Builder apiKey(String apiKey) {
      Objects.requireNonNull(apiKey, "apiKey is null");
      this.apiKey = apiKey;
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

    public LibreTranslateTranslationProviderConfiguration build() {
      if (this.uri == null) {
        throw new IllegalStateException("uri not set");
      }
      return new LibreTranslateTranslationProviderConfiguration(this);
    }
  }
}
