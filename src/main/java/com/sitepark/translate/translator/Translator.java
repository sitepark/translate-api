package com.sitepark.translate.translator;

import com.sitepark.translate.SupportedProvider;
import com.sitepark.translate.TranslationConfiguration;
import com.sitepark.translate.TranslationProvider;
import com.sitepark.translate.TranslationProviderFactory;
import com.sitepark.translate.TranslationRequest;
import com.sitepark.translate.TranslationResult;
import java.util.Objects;

public class Translator {

  private final TranslationConfiguration translatorConfiguration;

  protected Translator(Builder<?> builder) {
    this.translatorConfiguration = builder.translatorConfiguration;
  }

  protected TranslationConfiguration getTranslatorConfiguration() {
    return this.translatorConfiguration;
  }

  protected TranslationProvider createTransporter(SupportedProvider providerType) {
    return this.getTransporterFactory().create(providerType);
  }

  private TranslationProviderFactory getTransporterFactory() {
    return this.translatorConfiguration.getTranslationProviderFactory();
  }

  protected TranslationResult translate(TranslationRequest req) {
    return this.createTransporter(req.getParameter().getProviderType()).translate(req);
  }

  public abstract static class Builder<B extends Builder<B>> {

    private TranslationConfiguration translatorConfiguration;

    protected Builder() {}

    protected Builder(Translator translator) {
      this.translatorConfiguration = translator.translatorConfiguration;
    }

    public B translatorConfiguration(TranslationConfiguration.Builder translatorConfiguration) {
      Objects.requireNonNull(translatorConfiguration, "translatorConfiguration is null");
      this.translatorConfiguration = translatorConfiguration.build();
      return this.self();
    }

    public B translatorConfiguration(TranslationConfiguration translatorConfiguration) {
      Objects.requireNonNull(translatorConfiguration, "translatorConfiguration is null");
      this.translatorConfiguration = translatorConfiguration;
      return this.self();
    }

    protected abstract B self();

    public abstract Translator build();
  }
}
