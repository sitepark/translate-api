package com.sitepark.translate;

import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Locale;

public enum Format {
  TEXT,
  HTML,
  AUTO;

  @Override
  @JsonValue
  public String toString() {
    return name().toLowerCase(Locale.ENGLISH);
  }
}
