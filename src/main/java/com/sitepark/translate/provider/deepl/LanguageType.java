package com.sitepark.translate.provider.deepl;

import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Locale;

public enum LanguageType {
  SOURCE,
  TARGET;

  @Override
  @JsonValue
  public String toString() {
    return name().toLowerCase(Locale.ENGLISH);
  }
}
