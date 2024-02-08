package com.sitepark.translate.provider.deepl;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings("UWF_UNWRITTEN_FIELD")
public class TransportTranslation {

  private String detectedSourceLanguage;

  private String text;

  @JsonProperty("detected_source_language")
  public String getDetectedSourceLanguage() {
    return this.detectedSourceLanguage;
  }

  public String getText() {
    return this.text;
  }
}
