package com.sitepark.translate.translator; // NOPMD by veltrup on 24.05.23, 15:37

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.sitepark.translate.Language;
import com.sitepark.translate.SupportedLanguages;
import com.sitepark.translate.SupportedProvider;
import com.sitepark.translate.TranslationLanguage;
import com.sitepark.translate.TranslationParameter;
import com.sitepark.translate.TranslationProvider;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Translates YAML files that use a language suffix in their filename (e.g. {@code a.de.yaml} →
 * {@code a.en.yaml}). Both {@code .yaml} and {@code .yml} extensions are supported; the output
 * file uses the same extension as the source file.
 *
 * <p>Note: YAML comments in source files are not preserved in the translated output, because
 * Jackson's tree model does not retain comments.
 */
@SuppressWarnings({
  "PMD.GuardLogStatement",
  "PMD.TooManyMethods",
  "PMD.AvoidCatchingGenericException"
})
public final class YamlFileTranslator extends Translator {

  private static final String EN_US = "en-us";
  private final Path dir;
  private final Path output;
  private final String sourceLang;
  private final String sourceSuffixYaml;
  private final String sourceSuffixYml;
  private final Set<String> targetLangList;
  private final Logger logger;
  private List<YamlFile> yamlFiles;
  private List<TranslatableTextNode> translatableTextNodeList;
  private TranslatableTextNodeCollectorExcludes excludes;

  private YamlFileTranslator(Builder builder) {
    super(builder);
    this.dir = builder.dir;
    this.output = builder.output;
    this.sourceLang = builder.sourceLang;
    this.targetLangList = builder.targetLangList;
    this.logger = builder.logger;
    this.sourceSuffixYaml = "." + this.sourceLang + ".yaml";
    this.sourceSuffixYml = "." + this.sourceLang + ".yml";
  }

  public static Builder builder() {
    return new Builder();
  }

  public void translate(SupportedProvider provider) throws IOException {
    this.translate(provider, (List<String>) null);
  }

  public void translate(SupportedProvider provider, List<String> targets) throws IOException {

    Language sourceLanguage = this.getSourceLanguage(provider);
    this.loadYamlFiles();
    this.loadExcludeFile();
    this.collectTranslatableText();

    long tt = System.currentTimeMillis();

    List<String> targetLanguageList;
    if (targets != null) {
      targetLanguageList = targets;
    } else {
      targetLanguageList = sourceLanguage.getTargets();
    }

    for (String targetLanguage : targetLanguageList) {
      if (this.sourceLang.equals(targetLanguage)) {
        continue;
      }
      if (!this.targetLangList.isEmpty() && !this.targetLangList.contains(targetLanguage)) {
        continue;
      }
      long t = System.currentTimeMillis();

      this.logger.info("translate " + this.sourceLang + " -> " + targetLanguage);
      this.translate(provider, targetLanguage);
      long duration = (System.currentTimeMillis() - t) / 1000;
      this.logger.info(
          "translated "
              + this.sourceLang
              + " -> "
              + targetLanguage
              + " in "
              + duration
              + " seconds.");
      this.write(targetLanguage);
      if (EN_US.equals(targetLanguage)) {
        this.write("en");
      }
    }

    long totalDuration = (System.currentTimeMillis() - tt) / 1000;
    this.logger.info("translated in " + totalDuration + " seconds.");
  }

  private void write(String lang) throws IOException {

    YAMLMapper mapper = new YAMLMapper();

    for (YamlFile yamlFile : this.yamlFiles) {
      Path parent = yamlFile.sourceFile.getParent();
      if (parent == null) {
        parent = this.dir;
      }
      if (parent == null) {
        throw new IllegalStateException("parent is null");
      }

      Path filenamePath = yamlFile.sourceFile.getFileName();
      if (filenamePath == null) {
        throw new IllegalStateException(yamlFile.sourceFile + " as no filename");
      }
      String filename = filenamePath.toString();
      String sourceSuffix =
          filename.endsWith(this.sourceSuffixYaml) ? this.sourceSuffixYaml : this.sourceSuffixYml;
      String basename = filename.substring(0, filename.length() - sourceSuffix.length());
      if (!Files.exists(parent)) {
        Files.createDirectories(parent);
      }
      Path translatedFile = parent.resolve(basename + "." + lang + "." + yamlFile.extension);
      mapper.writerWithDefaultPrettyPrinter().writeValue(translatedFile.toFile(), yamlFile.node);
    }
  }

  private void collectTranslatableText() {
    this.translatableTextNodeList =
        this.yamlFiles.stream()
            .flatMap(
                yaml -> {
                  TranslatableTextNodeCollector collector =
                      new TranslatableTextNodeCollector(yaml.key);
                  return collector.excludes(this.excludes).collect(yaml.node).stream();
                })
            .collect(Collectors.toList());
  }

  private void loadExcludeFile() throws IOException {
    Path excludeFile = this.dir.resolve(this.sourceLang + ".excludes");
    if (Files.exists(excludeFile)) {
      this.excludes = TranslatableTextNodeCollectorExcludes.of(excludeFile);
    }
  }

  private void loadYamlFiles() throws IOException {
    try (Stream<Path> stream = Files.walk(this.dir)) {
      this.yamlFiles =
          stream
              .filter(Files::isRegularFile)
              .filter(
                  p ->
                      p.toString().endsWith(this.sourceSuffixYaml)
                          || p.toString().endsWith(this.sourceSuffixYml))
              .map(this::createYamlFile)
              .collect(Collectors.toList());
    }
  }

  private Language getSourceLanguage(SupportedProvider provider) {
    TranslationProvider translator = this.createTransporter(provider);
    SupportedLanguages supportedLanguages = translator.getSupportedLanguages();
    Optional<Language> sourceLanguage = supportedLanguages.getSourceLanguage(this.sourceLang);
    return sourceLanguage.orElseThrow(
        () ->
            new IllegalArgumentException("Unsupported source language '" + this.sourceLang + "'"));
  }

  private void translate(SupportedProvider provider, String targetLang) throws IOException {

    TranslationFileCache cache = this.createTranslationCache(targetLang);

    TranslatableTextListTranslator translator =
        TranslatableTextListTranslator.builder()
            .translatorConfiguration(
                this.getTranslatorConfiguration().toBuilder().translationCache(cache))
            .build();

    TranslationLanguage language =
        TranslationLanguage.builder().source(this.sourceLang).target(targetLang).build();

    TranslationParameter parameter =
        TranslationParameter.builder().providerType(provider).language(language).build();

    translator.translate(parameter, this.translatableTextNodeList);

    try {
      cache.store();
    } catch (Exception e) {
      this.logger.error("Unable to store cache " + cache.getFile() + ": " + e.getMessage(), e);
    }
  }

  private YamlFile createYamlFile(Path file) {

    if (this.dir == null) {
      throw new IllegalStateException("dir is not set");
    }

    Path path = file.subpath(this.dir.getNameCount(), file.getNameCount());
    JsonNode node = this.parseYaml(file);
    return new YamlFile(path, node, this.sourceSuffixYaml);
  }

  private JsonNode parseYaml(Path file) {
    try {
      YAMLMapper mapper = new YAMLMapper();
      return mapper.readTree(file.toFile());
    } catch (Exception e) {
      throw new TranslatorException(e.getMessage(), e);
    }
  }

  private TranslationFileCache createTranslationCache(String targetLang) throws IOException {

    Path cacheFile = this.output.resolve(".translation-cache").resolve(targetLang);
    Path parent = cacheFile.getParent();
    if (parent == null) {
      throw new IllegalStateException(cacheFile + " has no parent directory");
    }
    Files.createDirectories(parent);

    TranslationFileCache cache = new TranslationFileCache(cacheFile);
    try {
      cache.load();
    } catch (Exception e) {
      this.logger.error("Unalbe to load cache " + cacheFile + ": " + e.getMessage(), e);
    }
    return cache;
  }

  public static interface Logger {
    void info(String msg);

    void error(String msg, Throwable t);
  }

  @SuppressWarnings("PMD.DataClass")
  public static final class YamlFile {
    public final Path sourceFile;
    public final JsonNode node;
    public final String key;
    public final String extension;

    private YamlFile(Path sourceFile, JsonNode node, String sourceSuffixYaml) {
      this.sourceFile = sourceFile;
      this.node = node;

      String path = sourceFile.toString();
      this.extension = path.endsWith(sourceSuffixYaml) ? "yaml" : "yml";
      String fileWithoutSuffix = path.substring(0, path.lastIndexOf('.'));
      this.key = fileWithoutSuffix.replace('/', '.');
    }
  }

  public static final class Builder extends Translator.Builder<Builder> {

    private final Set<String> targetLangList = new HashSet<>();
    private Path dir;
    private Path output;
    private String sourceLang;
    private Logger logger;

    private Builder() {}

    public Builder dir(Path dir) {
      Objects.requireNonNull(dir, "dir is null");
      this.dir = dir.toAbsolutePath();
      return this;
    }

    public Builder output(Path output) {
      Objects.requireNonNull(output, "output is null");
      this.output = output.toAbsolutePath();
      return this;
    }

    public Builder sourceLang(String sourceLang) {
      Objects.requireNonNull(sourceLang, "sourceLang is null");
      this.sourceLang = sourceLang;
      return this;
    }

    public Builder targetLangList(String... targetLangList) {
      Objects.requireNonNull(targetLangList, "targetLangList is null");
      this.targetLangList.addAll(Arrays.asList(targetLangList));
      return this;
    }

    public Builder targetLangList(Set<String> targetLangList) {
      Objects.requireNonNull(targetLangList, "targetLangList is null");
      this.targetLangList.addAll(targetLangList);
      return this;
    }

    public Builder logger(Logger logger) {
      Objects.requireNonNull(logger, "logger is null");
      this.logger = logger;
      return this;
    }

    @Override
    protected Builder self() {
      return this;
    }

    @Override
    public YamlFileTranslator build() {
      assert dir != null : "dir is null";
      if (output == null) {
        output = dir;
      }
      assert sourceLang != null : "sourceLang is null";
      if (this.logger == null) {
        this.logger = new NullLogger();
      }
      return new YamlFileTranslator(this);
    }
  }

  private static final class NullLogger implements Logger {
    @Override
    public void info(String msg) {
      // null logger
    }

    @Override
    public void error(String msg, Throwable t) {
      // null logger
    }
  }
}
