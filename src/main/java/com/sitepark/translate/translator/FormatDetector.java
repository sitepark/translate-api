package com.sitepark.translate.translator;

import com.sitepark.translate.Format;
import com.sitepark.translate.translator.entity.Encoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class FormatDetector {

  private static final Pattern HTML_DETECTOR_PATTERN = Pattern.compile("<[^\\s>]*>|&[a-zA-Z]+;");

  private FormatDetector() {}

  public static boolean containsHtml(String text) {
    Matcher matcher = HTML_DETECTOR_PATTERN.matcher(text);
    return matcher.find();
  }

  public static boolean containsPlaceholder(String text) {
    return Encoder.hasPlaceholder(text);
  }

  public static Format detect(String text) {
    if (FormatDetector.containsHtml(text)) {
      return Format.HTML;
    } else if (FormatDetector.containsPlaceholder(text)) {
      return Format.XML;
    } else {
      return Format.TEXT;
    }
  }
}
