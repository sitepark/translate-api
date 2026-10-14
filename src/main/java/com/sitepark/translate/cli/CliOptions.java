package com.sitepark.translate.cli;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Separates {@code --name=value} options from the positional arguments of a command. Options may
 * appear at any position.
 */
final class CliOptions {

  static final String OUTPUT_LOCALE_FORMAT = "output-locale-format";

  private static final String PREFIX = "--";

  private final Map<String, String> options;

  private final List<String> positionalArguments;

  private CliOptions(Map<String, String> options, List<String> positionalArguments) {
    this.options = options;
    this.positionalArguments = positionalArguments;
  }

  /**
   * Parses {@code arguments}.
   *
   * @throws IllegalArgumentException if an option is not in {@code allowed}, has no value or is
   *     given more than once
   */
  @SuppressWarnings("PMD.UseConcurrentHashMap")
  static CliOptions parse(Set<String> allowed, String... arguments) {
    Map<String, String> options = new LinkedHashMap<>();
    List<String> positional = new ArrayList<>();
    for (String argument : arguments) {
      if (!argument.startsWith(PREFIX)) {
        positional.add(argument);
        continue;
      }
      int eq = argument.indexOf('=');
      if (eq == -1) {
        throw new IllegalArgumentException(
            "option " + argument + " requires a value: " + argument + "=<value>");
      }
      String name = argument.substring(PREFIX.length(), eq);
      if (!allowed.contains(name)) {
        throw new IllegalArgumentException("unknown option " + PREFIX + name);
      }
      if (options.putIfAbsent(name, argument.substring(eq + 1)) != null) {
        throw new IllegalArgumentException("option " + PREFIX + name + " given more than once");
      }
    }
    return new CliOptions(Collections.unmodifiableMap(options), List.copyOf(positional));
  }

  Optional<String> get(String name) {
    return Optional.ofNullable(this.options.get(name));
  }

  String[] positional() {
    return this.positionalArguments.toArray(new String[0]);
  }
}
