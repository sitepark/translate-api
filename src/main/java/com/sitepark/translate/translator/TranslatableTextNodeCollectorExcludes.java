package com.sitepark.translate.translator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public final class TranslatableTextNodeCollectorExcludes {
  private final Set<String> excludes;

  private TranslatableTextNodeCollectorExcludes(Set<String> excludes) {
    this.excludes = excludes;
  }

  /**
   * Read a file where each line is a key to exclude from translation.
   *
   * @param path
   * @throws IOException
   */
  public static TranslatableTextNodeCollectorExcludes of(Path path) throws IOException {
    Set<String> excludes = new HashSet<>(Files.readAllLines(path));
    return new TranslatableTextNodeCollectorExcludes(excludes);
  }

  /** Builds excludes from a set of keys (one key per entry), primarily for testing. */
  public static TranslatableTextNodeCollectorExcludes of(Set<String> excludes) {
    return new TranslatableTextNodeCollectorExcludes(new HashSet<>(excludes));
  }

  public boolean contains(String key) {
    return this.excludes.contains(key);
  }
}
