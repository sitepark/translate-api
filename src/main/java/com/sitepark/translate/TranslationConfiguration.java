package com.sitepark.translate;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class TranslationConfiguration {

	private final TranslationCache translationCache;

	private final TranslationListener translationListener;

	private final TranslationProviderFactory translationProviderFactory;

	private final Map<Class<? extends TranslationProviderConfiguration>, TranslationProviderConfiguration>
			translationProviderConfigurations;

	private final boolean encodePlaceholder;

	private TranslationConfiguration(Builder builder) {
		this.translationCache = builder.translationCache;
		this.translationListener = builder.translationListener;
		this.translationProviderConfigurations = Collections.unmodifiableMap(
				builder.translationProviderConfigurations);
		this.encodePlaceholder = builder.encodePlaceholder;
		if (builder.translationProviderFactory == null) {
			this.translationProviderFactory = new TranslationProviderFactory(this);
		} else {
			this.translationProviderFactory = builder.translationProviderFactory;
		}
	}

	public Optional<TranslationCache> getTranslationCache() {
		return Optional.ofNullable(this.translationCache);
	}

	public Optional<TranslationListener> getTranslationListener() {
		return Optional.ofNullable(this.translationListener);
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
	public <T extends TranslationProviderConfiguration> T getTranslationProviderConfiguration(Class<T> type) {
		T provider = (T)this.translationProviderConfigurations.get(type);
		if (provider == null) {
			throw new IllegalArgumentException("Provider configuration " + type.getName() + " not found");
		}
		return provider;
	}

	public boolean isEncodePlaceholder() {
		return this.encodePlaceholder;
	}

	public static Builder builder() {
		return new Builder();
	}

	public Builder toBuilder() {
		return new Builder(this);
	}

	@SuppressWarnings("PMD.TooManyMethods")
	public final static class Builder {

		private TranslationCache translationCache;

		private TranslationListener translationListener;

		private TranslationProviderFactory translationProviderFactory;

		private final Map<Class<? extends TranslationProviderConfiguration>, TranslationProviderConfiguration>
				translationProviderConfigurations = new ConcurrentHashMap<>();

		private boolean encodePlaceholder;

		private Builder() { }

		private Builder(TranslationConfiguration translatorConfiguration) {
			this.translationCache = translatorConfiguration.translationCache;
			this.translationListener = translatorConfiguration.translationListener;
			this.translationProviderFactory = translatorConfiguration.translationProviderFactory;
			this.translationProviderConfigurations.putAll(
					translatorConfiguration.translationProviderConfigurations);
			this.encodePlaceholder = translatorConfiguration.encodePlaceholder;
		}

		public Builder translationCache(TranslationCache translationCache) {
			assert translationCache != null : "translationCache is null";
			this.translationCache = translationCache;
			return this;
		}

		public Builder translationListener(TranslationListener translationListener) {
			assert translationListener != null : "translationListener is null";
			this.translationListener = translationListener;
			return this;
		}

		public Builder translationProviderFactory(TranslationProviderFactory translatorProviderFactory) {
			assert translatorProviderFactory != null : "translatorProviderFactory is null";
			this.translationProviderFactory = translatorProviderFactory;
			return this;
		}

		public Builder translationProviderConfiguration(
				TranslationProviderConfiguration translationProviderConfiguration) {
			assert translationProviderConfiguration != null : "translationProviderConfiguration is null";
			this.translationProviderConfigurations.put(
					translationProviderConfiguration.getClass(),
					translationProviderConfiguration);
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
