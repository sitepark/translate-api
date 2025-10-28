package com.sitepark.translate;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class TranslationConfiguration {

  private final TranslationCache translationCache;

  private final TranslationProviderFactory translationProviderFactory;

  private final Map<
          Class<? extends TranslationProviderConfiguration>, TranslationProviderConfiguration>
      translationProviderConfigurations;

  private final boolean encodePlaceholder;

  private TranslationConfiguration(Builder builder) {
    this.translationCache = builder.translationCache;
    this.translationProviderConfigurations =
        Collections.unmodifiableMap(builder.translationProviderConfigurations);
    this.encodePlaceholder = builder.encodePlaceholder;
    if (builder.translationProviderFactory == null) {
      this.translationProviderFactory = new TranslationProviderFactory(this);
    } else {
      this.translationProviderFactory = builder.translationProviderFactory;
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  public Optional<TranslationCache> getTranslationCache() {
    return Optional.ofNullable(this.translationCache);
  }

  public TranslationProviderFactory getTranslationProviderFactory() {
    return this.translationProviderFactory;
  }

  public SupportedProvider[] getConfiguredProvider() {
    return this.translationProviderConfigurations.values().stream()
        .map(TranslationProviderConfiguration::getType)
        .toArray(SupportedProvider[]::new);
  }

  @SuppressWarnings("unchecked")
  public <T extends TranslationProviderConfiguration> T getTranslationProviderConfiguration(
      Class<T> type) {
    T provider = (T) this.translationProviderConfigurations.get(type);
    if (provider == null) {
      throw new IllegalArgumentException("Provider configuration " + type.getName() + " not found");
    }
    return provider;
  }

  public boolean isEncodePlaceholder() {
    return this.encodePlaceholder;
  }

  public Builder toBuilder() {
    return new Builder(this);
  }

  public static final class Builder {

    private final Map<
            Class<? extends TranslationProviderConfiguration>, TranslationProviderConfiguration>
        translationProviderConfigurations = new ConcurrentHashMap<>();
    private TranslationCache translationCache;
    private TranslationProviderFactory translationProviderFactory;
    private boolean encodePlaceholder;

    private Builder() {}

    private Builder(TranslationConfiguration translatorConfiguration) {
      this.translationCache = translatorConfiguration.translationCache;
      this.translationProviderFactory = translatorConfiguration.translationProviderFactory;
      this.translationProviderConfigurations.putAll(
          translatorConfiguration.translationProviderConfigurations);
      this.encodePlaceholder = translatorConfiguration.encodePlaceholder;
    }

    public Builder translationCache(TranslationCache translationCache) {
      Objects.requireNonNull(translationCache, "translationCache is null");
      this.translationCache = translationCache;
      return this;
    }

    public Builder translationProviderFactory(
        TranslationProviderFactory translatorProviderFactory) {
      Objects.requireNonNull(translatorProviderFactory, "translatorProviderFactory is null");
      this.translationProviderFactory = translatorProviderFactory;
      return this;
    }

    public Builder translationProviderConfiguration(
        TranslationProviderConfiguration translationProviderConfiguration) {
      Objects.requireNonNull(
          translationProviderConfiguration, "translationProviderConfiguration is null");
      this.translationProviderConfigurations.put(
          translationProviderConfiguration.getClass(), translationProviderConfiguration);
      return this;
    }

    public Builder encodePlaceholder(boolean encodePlaceholder) {
      this.encodePlaceholder = encodePlaceholder;
      return this;
    }

    public TranslationConfiguration build() {
      return new TranslationConfiguration(this);
    }
  }
}
