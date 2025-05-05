package com.sitepark.translate.translator;

import com.sitepark.translate.Format;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@SuppressWarnings("AvoidCatchingGenericException")
public class TranslatableText {

  private final String sourceText;
  private final Format format;
  private String sourceTextHash;
  private String targetText;

  public TranslatableText(String sourceText) {
    this.sourceText = sourceText;
    this.format = FormatDetector.detect(sourceText);
  }

  public TranslatableText(String sourceText, Format format) {
    this.sourceText = sourceText;
    this.format = format;
  }

  @SuppressWarnings("PMD.AvoidCatchingGenericException")
  public static String toHash(String sourceText) {

    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] hash = digest.digest(sourceText.getBytes(StandardCharsets.UTF_8));

      int singleChar = 1;
      StringBuilder hexString = new StringBuilder(2 * hash.length);
      for (byte b : hash) {
        String hex = Integer.toHexString(0xff & b);
        if (hex.length() == singleChar) {
          hexString.append('0');
        }
        hexString.append(hex);
      }
      return hexString.toString();
    } catch (Exception e) {
      throw new TranslatorException(e.getMessage(), e);
    }
  }

  public Format getFormat() {
    return this.format;
  }

  public String getSourceText() {
    return this.sourceText;
  }

  public String getTargetText() {
    return this.targetText;
  }

  public void setTargetText(String targetText) {
    this.targetText = targetText;
  }

  public String toHash() {

    if (this.sourceTextHash != null) {
      return this.sourceTextHash;
    }
    if (this.sourceText == null) {
      return null;
    }

    this.sourceTextHash = TranslatableText.toHash(this.sourceText);
    return this.sourceTextHash;
  }
}
