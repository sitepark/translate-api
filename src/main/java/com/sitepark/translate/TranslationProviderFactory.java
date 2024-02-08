package com.sitepark.translate;

import com.sitepark.translate.provider.deepl.DeeplTranslationProvider;
import com.sitepark.translate.provider.libretranslate.LibreTranslateTranslationProvider;

public class TranslationProviderFactory {

  private final TranslationConfiguration translatorConfiguration;

  public TranslationProviderFactory(TranslationConfiguration translatorConfiguration) {
    this.translatorConfiguration = translatorConfiguration;
  }

  public TranslationProvider create(SupportedProvider type) {
    if (type == SupportedProvider.LIBRE_TRANSLATE) {
      return this.createLibreTranslateTranslationProvider();
    } else if (type == SupportedProvider.DEEPL) {
      return this.createDeeplTranslationProvider();
    } else {
      throw new IllegalArgumentException("Unsupported provider " + type);
    }
  }

  private LibreTranslateTranslationProvider createLibreTranslateTranslationProvider() {
    return new LibreTranslateTranslationProvider(this.translatorConfiguration);
  }

  private DeeplTranslationProvider createDeeplTranslationProvider() {
    return new DeeplTranslationProvider(this.translatorConfiguration);
  }
}
