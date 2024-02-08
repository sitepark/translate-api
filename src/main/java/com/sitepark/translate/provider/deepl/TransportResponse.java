package com.sitepark.translate.provider.deepl;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Arrays;

public class TransportResponse {

  @JsonProperty private TransportTranslation[] translations;

  public String[] getTranslations() {
    return Arrays.stream(this.translations)
        .map(translation -> translation.getText())
        .toArray(String[]::new);
  }
}
