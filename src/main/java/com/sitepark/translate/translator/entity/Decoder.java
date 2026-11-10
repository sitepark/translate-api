package com.sitepark.translate.translator.entity;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Decoder {

  // DOTALL so a placeholder that spans newlines (e.g. a multi-line ICU MessageFormat string from a
  // folded YAML scalar) is still matched and unwrapped.
  private static final Pattern HTML_ENTITY_PATTERN =
      Pattern.compile(
          "(<span data-encoded-entity=\"true\" translate=\"no\">)(.*?)(</span>)", Pattern.DOTALL);

  private static final Pattern XML_ENTITY_PATTERN = Pattern.compile("<x>(.*?)</x>", Pattern.DOTALL);

  private Decoder() {}

  public static String[] decode(String... text) {
    return Arrays.stream(text).map(Decoder::decode).toArray(String[]::new);
  }

  public static String decode(String text) {
    Matcher matcher = HTML_ENTITY_PATTERN.matcher(text);
    int start = 0;
    StringBuilder decoded = new StringBuilder();
    while (matcher.find()) {
      decoded.append(text.substring(start, matcher.start(1))).append(matcher.group(2));
      start = matcher.end();
    }
    decoded.append(text.substring(start, text.length()));
    return decoded.toString();
  }

  public static String[] decodeXml(String... text) {
    return Arrays.stream(text).map(Decoder::decodeXml).toArray(String[]::new);
  }

  public static String decodeXml(String text) {
    Matcher m = XML_ENTITY_PATTERN.matcher(text);
    StringBuffer sb = new StringBuffer();
    while (m.find()) {
      m.appendReplacement(sb, Matcher.quoteReplacement(m.group(1)));
    }
    m.appendTail(sb);
    return sb.toString().replace("&amp;", "&").replace("&lt;", "<").replace("&gt;", ">");
  }
}
