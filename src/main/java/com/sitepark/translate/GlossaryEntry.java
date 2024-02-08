package com.sitepark.translate;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.util.Objects;

@JsonDeserialize(builder = GlossaryEntry.Builder.class)
public final class GlossaryEntry {

  private final String source;

  private final String target;

  private GlossaryEntry(Builder builder) {
    this.source = builder.source;
    this.target = builder.target;
  }

  public String getSource() {
    return this.source;
  }

  public String getTarget() {
    return this.target;
  }

  public static Builder builder() {
    return new Builder();
  }

  public Builder toBuilder() {
    return new Builder(this);
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
    if (!(o instanceof GlossaryEntry)) {
      return false;
    }

    GlossaryEntry entry = (GlossaryEntry) o;
    return Objects.equals(entry.getSource(), this.source)
        && Objects.equals(entry.getTarget(), this.target);
  }

  @Override
  public String toString() {
    return this.source + ": " + this.target;
  }

  @JsonPOJOBuilder(withPrefix = "", buildMethodName = "build")
  public static final class Builder {

    private String source;

    private String target;

    private Builder() {}

    private Builder(GlossaryEntry glossarEntry) {
      this.source = glossarEntry.source;
      this.target = glossarEntry.target;
    }

    public Builder source(String source) {
      Objects.requireNonNull(source, "source is null");
      this.requireNotBlank(source, "source is blank");
      this.source = source;
      return this;
    }

    public Builder target(String target) {
      Objects.requireNonNull(target, "target is null");
      this.requireNotBlank(target, "target is blank");
      this.target = target;
      return this;
    }

    public GlossaryEntry build() {

      if (this.source == null) {
        throw new IllegalStateException("source not set");
      }
      if (this.target == null) {
        throw new IllegalStateException("target not set");
      }

      return new GlossaryEntry(this);
    }

    private void requireNotBlank(String s, String message) {
      if (s.isBlank()) {
        throw new IllegalArgumentException(message);
      }
    }
  }
}
