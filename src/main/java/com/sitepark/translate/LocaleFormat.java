package com.sitepark.translate;

import com.ibm.icu.util.ULocale;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Spelling of a locale in the names of output files and directories. The provider's language code
 * (e.g. DeepL's {@code en-gb}) stays the internal identifier for API requests and caches; a {@code
 * LocaleFormat} is applied only when an output path is built.
 *
 * <table>
 *   <caption>Examples (DeepL codes)</caption>
 *   <tr><th>Format</th><th>{@code en-gb}</th><th>{@code zh-hans}</th><th>{@code es-419}</th></tr>
 *   <tr><td>{@link #PROVIDER}</td><td>{@code en-gb}</td><td>{@code zh-hans}</td>
 *       <td>{@code es-419}</td></tr>
 *   <tr><td>{@link #BCP47}</td><td>{@code en-GB}</td><td>{@code zh-Hans}</td>
 *       <td>{@code es-419}</td></tr>
 *   <tr><td>{@link #ICU}</td><td>{@code en_GB}</td><td>{@code zh_Hans}</td>
 *       <td>{@code es_419}</td></tr>
 * </table>
 */
public enum LocaleFormat {

  /**
   * The code exactly as delivered by the provider, e.g. {@code en-gb} for DeepL (lowercase). This
   * is the default and matches the behaviour before {@code LocaleFormat} existed.
   */
  PROVIDER {
    @Override
    public String format(String code) {
      return code;
    }
  },

  /** BCP 47 language tag, e.g. {@code en-GB} (i18next, HTML {@code lang}, HTTP). */
  BCP47 {
    @Override
    public String format(String code) {
      return toULocale(code).toLanguageTag();
    }
  },

  /** ICU locale ID, e.g. {@code en_GB} (Symfony, PHP intl, Java). */
  ICU {
    @Override
    public String format(String code) {
      return toULocale(code).toString();
    }
  };

  /**
   * Formats a provider language code.
   *
   * @throws IllegalArgumentException if {@code code} is not a valid language tag (only for {@link
   *     #BCP47} and {@link #ICU})
   */
  public abstract String format(String code);

  private static ULocale toULocale(String code) {
    ULocale locale = ULocale.forLanguageTag(normalize(code));
    if (locale.getLanguage().isEmpty()) {
      throw new IllegalArgumentException("'" + code + "' is not a valid language tag");
    }
    return locale;
  }

  public static LocaleFormat fromName(String name) {
    Objects.requireNonNull(name, "name is null");
    for (LocaleFormat format : values()) {
      if (format.name().equalsIgnoreCase(name)) {
        return format;
      }
    }
    throw new IllegalArgumentException(
        "Unknown locale format '"
            + name
            + "', allowed: "
            + Arrays.stream(values())
                .map(f -> f.name().toLowerCase(Locale.ROOT))
                .collect(Collectors.joining(", ")));
  }

  /**
   * Normalizes a language code for comparison: lowercase and {@code _} replaced by {@code -}, so
   * that {@code en_GB}, {@code en-GB} and {@code en-gb} are equal.
   */
  public static String normalize(String code) {
    Objects.requireNonNull(code, "code is null");
    return code.toLowerCase(Locale.ROOT).replace('_', '-');
  }
}
