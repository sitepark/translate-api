package com.sitepark.translate.translator;

import com.sitepark.translate.LocaleFormat;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Resolves and filters target languages of the file translators. Codes are compared normalized
 * (see {@link LocaleFormat#normalize(String)}), so {@code en_GB}, {@code en-GB} and {@code en-gb}
 * select the same target.
 */
final class TargetLanguages {

  private static final String EN_US = "en-us";

  private TargetLanguages() {}

  /**
   * Returns the target codes to translate into. Explicitly given targets are mapped to the
   * provider's spelling of the code if the provider supports them, otherwise they are normalized.
   */
  static List<String> resolve(List<String> explicitTargets, List<String> supportedTargets) {
    if (explicitTargets == null) {
      return supportedTargets;
    }
    return explicitTargets.stream()
        .map(target -> resolve(target, supportedTargets))
        .collect(Collectors.toList());
  }

  private static String resolve(String target, List<String> supportedTargets) {
    String normalized = LocaleFormat.normalize(target);
    return supportedTargets.stream()
        .filter(supported -> LocaleFormat.normalize(supported).equals(normalized))
        .findFirst()
        .orElse(normalized);
  }

  /**
   * Returns {@code true} if {@code target} is not the source language and is contained in {@code
   * filter} (an empty filter allows every target). {@code filter} must contain normalized codes.
   */
  static boolean isWanted(String sourceLang, Set<String> filter, String target) {
    String normalized = LocaleFormat.normalize(target);
    if (LocaleFormat.normalize(sourceLang).equals(normalized)) {
      return false;
    }
    return filter.isEmpty() || filter.contains(normalized);
  }

  static boolean isEnUs(String target) {
    return EN_US.equals(LocaleFormat.normalize(target));
  }
}
