package com.sitepark.translate.provider.deepl;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings("UWF_UNWRITTEN_FIELD")
public class TransportLanguage {

  private String language;

  private String name;

  private boolean supportsFormality;

  public TransportLanguage() {}

  public TransportLanguage(String language, String name, boolean supportsFormality) {
    this.language = language;
    this.name = name;
    this.supportsFormality = supportsFormality;
  }

  public String getLanguage() {
    return this.language;
  }

  public String getName() {
    return this.name;
  }

  @JsonProperty("supports_formality")
  public boolean isSupportsFormality() {
    return this.supportsFormality;
  }
}
