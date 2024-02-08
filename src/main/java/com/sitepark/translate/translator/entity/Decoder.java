package com.sitepark.translate.translator.entity;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Decoder {

  private Decoder() {}

  public static String[] decode(String... text) {
    return Arrays.stream(text).map(Decoder::decode).toArray(String[]::new);
  }

  public static String decode(String text) {
    Pattern pattern =
        Pattern.compile("(<span data-encoded-entity=\"true\" translate=\"no\">)(.*?)(</span>)");
    Matcher matcher = pattern.matcher(text);
    int start = 0;
    StringBuilder decoded = new StringBuilder();
    while (matcher.find()) {
      decoded.append(text.substring(start, matcher.start(1))).append(matcher.group(2));
      start = matcher.end();
    }
    decoded.append(text.substring(start, text.length()));
    return decoded.toString();
  }
}
