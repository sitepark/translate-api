package com.sitepark.translate.provider.libretranslate;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Arrays;

@SuppressFBWarnings({"UWF_UNWRITTEN_FIELD", "NP_UNWRITTEN_FIELD"})
public class TransportResponse {

  private String[] translatedText;

  public String[] getTranslatedText() {
    return Arrays.copyOf(this.translatedText, this.translatedText.length);
  }
}
