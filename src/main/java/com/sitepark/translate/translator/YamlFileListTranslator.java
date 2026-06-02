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
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Translates YAML files that are organized in language subdirectories (e.g.
 * {@code dir/de/a.yaml} → {@code output/en/a.yaml}). Both {@code .yaml} and {@code .yml}
 * extensions are supported; each output file uses the same extension as its source file.
 *
 * <p>Note: YAML comments in source files are not preserved in the translated output, because
 * Jackson's tree model does not retain comments.
 */
@SuppressWarnings({"PMD.GuardLogStatement", "PMD.TooManyMethods", "PMD.SystemPrintln"})
public final class YamlFileListTranslator extends Translator {

  private final Path dir;

  private final Path output;

  private final String sourceLang;

  private final Set<String> targetLangList;

  private final Logger logger;

  private Path sourceDir;

  private List<YamlFile> yamlFiles;

  private List<TranslatableTextNode> translatableTextNodeList;

  private TranslatableTextNodeCollectorExcludes excludes;

  private YamlFileListTranslator(Builder builder) {
    super(builder);
    this.dir = builder.dir;
    this.output = builder.output;
    this.sourceLang = builder.sourceLang;
    this.targetLangList = builder.targetLangList;
    this.logger = builder.logger;
  }

  public static Builder builder() {
    return new Builder();
  }

  public void translate(SupportedProvider provider) throws IOException {
    this.translate(provider, (List<String>) null);
  }

  public void translate(SupportedProvider provider, List<String> targets) throws IOException {

    this.sourceDir = this.dir.resolve(this.sourceLang);

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
    }

    this.copyToGeneralEn();

    long totalDuration = (System.currentTimeMillis() - tt) / 1000;
    this.logger.info("translated in " + totalDuration + " seconds.");
  }

  private void copyToGeneralEn() {
    Path enUS = this.getOutputDir("en-us");
    if (!Files.isDirectory(enUS)) {
      System.out.println("not found " + enUS);
      return;
    }

    Path en = this.getOutputDir("en");
    System.out.println("copy " + enUS + " -> " + en);
    this.copy(enUS, "en");
  }

  private void copy(Path src, String lang) {
    try {
      Path dest = this.getOutputDir(lang);
      Files.walk(src)
          .forEach(
              source -> {
                if (source.equals(src)) {
                  try {
                    Files.createDirectories(dest);
                  } catch (IOException e) {
                    throw new UncheckedIOException(e);
                  }
                  return;
                }
                int prefixLength = src.toString().length() + 1;
                Path destination = dest.resolve(source.toString().substring(prefixLength));
                if (Files.isDirectory(destination)) {
                  return;
                }
                try {
                  Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                  throw new UncheckedIOException(e);
                }
              });
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private void write(String lang) throws IOException {

    YAMLMapper mapper = new YAMLMapper();

    for (YamlFile yamlFile : this.yamlFiles) {
      Path translatedFile = this.getOutputDir(lang).resolve(yamlFile.sourceFile);
      Path parent = translatedFile.getParent();
      if (parent != null && !Files.exists(parent)) {
        Files.createDirectories(parent);
      }
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
    try (Stream<Path> stream = Files.walk(this.sourceDir)) {
      this.yamlFiles =
          stream
              .filter(Files::isRegularFile)
              .filter(p -> p.toString().endsWith(".yaml") || p.toString().endsWith(".yml"))
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

  @SuppressWarnings("PMD.AvoidCatchingGenericException")
  private void translate(SupportedProvider provider, String targetLang) {

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

    if (this.sourceDir == null) {
      throw new IllegalStateException("sourceDir is not set");
    }

    Path path = file.subpath(this.sourceDir.getNameCount(), file.getNameCount());
    JsonNode node = this.parseYaml(file);
    return new YamlFile(path, node);
  }

  @SuppressWarnings("PMD.AvoidCatchingGenericException")
  private JsonNode parseYaml(Path file) {
    try {
      YAMLMapper mapper = new YAMLMapper();
      return mapper.readTree(file.toFile());
    } catch (Exception e) {
      throw new TranslatorException(e.getMessage(), e);
    }
  }

  @SuppressWarnings("PMD.AvoidCatchingGenericException")
  private TranslationFileCache createTranslationCache(String targetLang) {

    Path cacheFile = this.getOutputDir(targetLang).resolve(".translation-cache-file");

    TranslationFileCache cache = new TranslationFileCache(cacheFile);
    try {
      cache.load();
    } catch (Exception e) {
      this.logger.error("Unalbe to load cache " + cacheFile + ": " + e.getMessage(), e);
    }
    return cache;
  }

  private Path getOutputDir(String lang) {
    return this.output.resolve(lang);
  }

  public static interface Logger {
    void info(String msg);

    void error(String msg, Throwable t);
  }

  public static final class YamlFile {
    public final Path sourceFile;
    public final JsonNode node;
    public final String key;

    private YamlFile(Path sourceFile, JsonNode node) {
      this.sourceFile = sourceFile;
      this.node = node;

      String path = sourceFile.toString();
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
    public YamlFileListTranslator build() {
      assert dir != null : "dir is null";
      assert output != null : "output is null";
      assert sourceLang != null : "sourceLang is null";
      if (this.logger == null) {
        this.logger = new NullLogger();
      }
      return new YamlFileListTranslator(this);
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
