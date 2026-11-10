package com.sitepark.translate.translator.entity;

import com.ibm.icu.text.MessagePattern;
import com.ibm.icu.text.MessagePattern.Part;
import java.util.ArrayList;
import java.util.List;

/**
 * Placeholder protection for ICU MessageFormat strings.
 *
 * <p>The hand-rolled {@link Scanner} only recognizes {@code {...}} as an opaque placeholder when it
 * contains a bare identifier (e.g. {@code {name}}). It mishandles real ICU MessageFormat syntax
 * such as {@code {value, number, ::.#}} (typed argument) or {@code {count, plural, one {Kategorie}
 * other {Kategorien}}} (nested translatable submessages). This encoder uses ICU4J's {@link
 * MessagePattern} to parse the message, then protects every structural part (argument names,
 * {@code plural}/{@code select} keywords, selectors, braces, {@code #}) by wrapping it in {@code
 * <x>...</x>} tags while leaving only the human-language literal text translatable.
 *
 * <p>The output uses the same {@code <x>} wrapper and XML escaping as {@link Encoder#encodeXml} so
 * it is decoded by the existing {@link Decoder#decodeXml} and sent to DeepL with {@code
 * tag_handling=xml} / {@code ignore_tags=x}.
 */
public final class IcuMessageEncoder {

  private IcuMessageEncoder() {}

  /**
   * Returns {@code true} if the text parses as an ICU MessageFormat pattern that contains at least
   * one argument. Plain text without arguments (e.g. {@code "Hello World"}) returns {@code false}
   * so it stays on the legacy path.
   */
  public static boolean isIcu(String text) {
    MessagePattern pattern = parse(text);
    if (pattern == null) {
      return false;
    }
    int count = pattern.countParts();
    for (int i = 0; i < count; i++) {
      if (pattern.getPart(i).getType() == Part.Type.ARG_START) {
        return true;
      }
    }
    return false;
  }

  /**
   * Wraps all ICU structure in {@code <x>...</x>} and XML-escapes the remaining literal text.
   * Callers must ensure {@link #isIcu(String)} is {@code true}; otherwise the text is returned
   * unchanged.
   */
  public static String encodeXml(String text) {
    MessagePattern pattern = parse(text);
    if (pattern == null) {
      return text;
    }

    List<int[]> literals = new ArrayList<>();
    collectMessageLiterals(pattern, 0, literals);

    StringBuilder out = new StringBuilder(text.length() + 16);
    int pos = 0;
    for (int[] range : literals) {
      if (range[0] > pos) {
        out.append("<x>").append(text, pos, range[0]).append("</x>");
      }
      appendEscaped(out, text, range[0], range[1]);
      pos = range[1];
    }
    if (pos < text.length()) {
      out.append("<x>").append(text, pos, text.length()).append("</x>");
    }
    return out.toString();
  }

  @SuppressWarnings("PMD.AvoidCatchingGenericException")
  private static MessagePattern parse(String text) {
    try {
      return new MessagePattern(text);
    } catch (RuntimeException e) {
      // Not a valid ICU MessageFormat pattern (e.g. unbalanced braces).
      return null;
    }
  }

  /**
   * Collects the character ranges of translatable literal text within the message starting at
   * {@code msgStart}. Mirrors ICU's own {@code MessageFormat} literal-text extraction (the text
   * between structural parts), and additionally recurses into every nested submessage of an
   * argument so that {@code plural}/{@code select} branch texts are captured too.
   */
  private static void collectMessageLiterals(
      MessagePattern pattern, int msgStart, List<int[]> literals) {
    int prevIndex = pattern.getPart(msgStart).getLimit();
    int i = msgStart + 1;
    while (true) {
      Part part = pattern.getPart(i);
      Part.Type type = part.getType();
      int index = part.getIndex();
      if (index > prevIndex) {
        literals.add(new int[] {prevIndex, index});
      }
      if (type == Part.Type.MSG_LIMIT) {
        return;
      }
      if (type == Part.Type.ARG_START) {
        int argLimit = pattern.getLimitPartIndex(i);
        collectSubmessageLiterals(pattern, i, argLimit, literals);
        prevIndex = pattern.getPart(argLimit).getLimit();
        i = argLimit + 1;
      } else {
        // SKIP_SYNTAX (apostrophe quoting) and REPLACE_NUMBER (#) are structure, not literal text.
        prevIndex = part.getLimit();
        i++;
      }
    }
  }

  /** Recurses into every submessage (plural/select branch) nested inside a single argument. */
  private static void collectSubmessageLiterals(
      MessagePattern pattern, int argStart, int argLimit, List<int[]> literals) {
    int j = argStart + 1;
    while (j < argLimit) {
      if (pattern.getPart(j).getType() == Part.Type.MSG_START) {
        collectMessageLiterals(pattern, j, literals);
        j = pattern.getLimitPartIndex(j) + 1;
      } else {
        j++;
      }
    }
  }

  private static void appendEscaped(StringBuilder out, String text, int start, int end) {
    for (int i = start; i < end; i++) {
      char c = text.charAt(i);
      switch (c) {
        case '&':
          out.append("&amp;");
          break;
        case '<':
          out.append("&lt;");
          break;
        case '>':
          out.append("&gt;");
          break;
        default:
          out.append(c);
      }
    }
  }
}
