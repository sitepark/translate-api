package com.sitepark.translate;

import java.util.Objects;

public final class TranslationLanguage {

  private final String source;

  private final String target;

  private TranslationLanguage(Builder builder) {
    this.source = builder.source;
    this.target = builder.target;
  }

  public String getSource() {
    return this.source;
  }

  public String getTarget() {
    return this.target;
  }

  @Override
  public int hashCode() {
    int hashCode = 0;
    if (this.source != null) {
      hashCode += this.source.hashCode();
    }
    if (this.target != null) {
      hashCode += this.target.hashCode();
    }
    return hashCode;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof TranslationLanguage)) {
      return false;
    }

    TranslationLanguage lang = (TranslationLanguage) o;
    return Objects.equals(lang.getSource(), this.source)
        && Objects.equals(lang.getTarget(), this.target);
  }

  @Override
  public String toString() {
    return this.source + " - " + this.target;
  }

  public static Builder builder() {
    return new Builder();
  }

  public Builder toBuilder() {
    return new Builder(this);
  }

  public static final class Builder {

    private String source;

    private String target;

    private Builder() {}

    private Builder(TranslationLanguage translationLanguage) {
      this.source = translationLanguage.source;
      this.target = translationLanguage.target;
    }

    public Builder source(String source) {
      Objects.requireNonNull(source, "source is null");
      this.source = source;
      return this;
    }

    public Builder target(String target) {
      Objects.requireNonNull(target, "target is null");
      this.target = target;
      return this;
    }

    public TranslationLanguage build() {
      if (this.source == null) {
        throw new IllegalStateException("source is not set");
      }
      if (this.target == null) {
        throw new IllegalStateException("target is not set");
      }
      return new TranslationLanguage(this);
    }
  }
}
