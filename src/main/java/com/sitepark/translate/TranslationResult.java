package com.sitepark.translate;

import java.util.Arrays;
import java.util.Objects;

public final class TranslationResult {

  private final TranslationRequest request;

  private final String[] text;

  private final TranslationResultStatistic statistic;

  private TranslationResult(Builder builder) {
    this.request = builder.request;
    this.text = builder.text;
    this.statistic = builder.statistic;
  }

  public TranslationRequest getRequest() {
    return this.request;
  }

  public String[] getText() {
    return Arrays.copyOf(this.text, this.text.length);
  }

  public TranslationResultStatistic getStatistic() {
    return this.statistic;
  }

  public static Builder builder() {
    return new Builder();
  }

  public Builder toBuilder() {
    return new Builder(this);
  }

  public static final class Builder {

    private TranslationRequest request;

    private String[] text;

    private TranslationResultStatistic statistic;

    private Builder() {}

    private Builder(TranslationResult result) {
      this.request = result.request;
      this.text = result.text;
      this.statistic = result.statistic;
    }

    public Builder request(TranslationRequest request) {
      Objects.requireNonNull(request, "request is null");
      this.request = request;
      return this;
    }

    public Builder text(String... text) {
      Objects.requireNonNull(text, "text is null");
      for (String s : text) {
        Objects.requireNonNull(s, "value in text is null");
      }
      this.text = Arrays.copyOf(text, text.length);
      return this;
    }

    public Builder statistic(TranslationResultStatistic statistic) {
      Objects.requireNonNull(statistic, "statistic is null");
      this.statistic = statistic;
      return this;
    }

    public TranslationResult build() {

      if (this.request == null) {
        throw new IllegalStateException("request is not set");
      }
      if (this.text == null) {
        throw new IllegalStateException("text is not set");
      }
      if (this.statistic == null) {
        throw new IllegalStateException("statistic is not set");
      }

      return new TranslationResult(this);
    }
  }
}
