package com.sitepark.translate.cli;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.sitepark.translate.SupportedProvider;
import com.sitepark.translate.TranslationProviderConfiguration;
import com.sitepark.translate.provider.deepl.DeeplTranslationProviderConfiguration;
import com.sitepark.translate.provider.libretranslate.LibreTranslateTranslationProviderConfiguration;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Arguments of the directory based translate commands ({@code translate-json}, {@code
 * translate-yaml}). They are taken from the command line or, if no arguments are given, from the
 * project configuration file {@value #PROJECT_CONFIG_FILE} in the working directory. Missing
 * credentials are read from the user configuration file {@code
 * ${XDG_CONFIG_HOME:-~/.config}/translate-api/config.yaml}.
 */
@SuppressWarnings("PMD")
public final class CommandArguments {

  public static final String PROJECT_CONFIG_FILE = "translate.yaml";

  private static final String DEEPL_KEY = "authKey";

  private static final String LIBRE_TRANSLATE_KEY = "apiKey";

  private final SupportedProvider providerType;

  private final TranslationProviderConfiguration providerConfiguration;

  private final Path dir;

  private final String sourceLang;

  private final Path output;

  private final List<String> targetLanguages;

  private CommandArguments(
      String url,
      Path dir,
      String sourceLang,
      Path output,
      List<String> targetLanguages,
      Path userConfigFile) {

    if (!Files.exists(dir.resolve(sourceLang))) {
      throw new IllegalArgumentException("source-dir " + dir.resolve(sourceLang) + " not exists");
    }

    URI uri = parseUri(url);
    this.providerType = SupportedProvider.ofScheme(uri.getScheme());
    try {
      this.providerConfiguration =
          createProviderConfiguration(this.providerType, uri, url, userConfigFile);
    } catch (URISyntaxException e) {
      throw new IllegalArgumentException("url " + url + " invalid: " + e.getMessage(), e);
    }
    this.dir = dir;
    this.sourceLang = sourceLang;
    this.output = output;
    this.targetLanguages =
        targetLanguages == null || targetLanguages.isEmpty()
            ? null
            : Collections.unmodifiableList(new ArrayList<>(targetLanguages));
  }

  public static CommandArguments parse(String... arguments) {
    return parse(Path.of("").toAbsolutePath(), defaultUserConfigFile(), arguments);
  }

  public static CommandArguments parse(Path workDir, Path userConfigFile, String... arguments) {
    if (arguments.length == 0) {
      return fromProjectConfig(workDir.resolve(PROJECT_CONFIG_FILE), userConfigFile);
    }
    return fromArguments(workDir, userConfigFile, arguments);
  }

  private static CommandArguments fromArguments(
      Path workDir, Path userConfigFile, String... arguments) {
    if (arguments.length < 4) {
      throw new IllegalArgumentException(
          "<url>, <dir>, <base-lang> <output-dir> [target-language]... expected");
    }
    List<String> targets = new ArrayList<>();
    for (int i = 4; i < arguments.length; i++) {
      targets.add(arguments[i]);
    }
    return new CommandArguments(
        arguments[0],
        workDir.resolve(arguments[1]).normalize(),
        arguments[2],
        workDir.resolve(arguments[3]).normalize(),
        targets,
        userConfigFile);
  }

  private static CommandArguments fromProjectConfig(Path configFile, Path userConfigFile) {
    if (!Files.exists(configFile)) {
      throw new IllegalArgumentException(
          "no arguments given and no " + PROJECT_CONFIG_FILE + " found: " + configFile);
    }
    JsonNode config = readYaml(configFile);
    Path baseDir = configFile.toAbsolutePath().getParent();

    List<String> targets = new ArrayList<>();
    JsonNode targetNode = config.path("targetLanguages");
    if (targetNode.isArray()) {
      for (JsonNode n : targetNode) {
        if (!n.isTextual()) {
          // YAML 1.1 reads e.g. no/yes/on/off as booleans
          throw new IllegalArgumentException(
              configFile
                  + ": targetLanguages entry '"
                  + n.asText()
                  + "' is not a string, put it in quotes");
        }
        targets.add(n.asText());
      }
    } else if (!targetNode.isMissingNode() && !targetNode.isNull()) {
      throw new IllegalArgumentException(
          configFile + ": targetLanguages must be a list of language codes");
    }

    return new CommandArguments(
        requiredText(config, "url", configFile),
        baseDir.resolve(requiredText(config, "dir", configFile)).normalize(),
        requiredText(config, "sourceLang", configFile),
        baseDir.resolve(requiredText(config, "output", configFile)).normalize(),
        targets,
        userConfigFile);
  }

  static Path defaultUserConfigFile() {
    String xdgConfigHome = System.getenv("XDG_CONFIG_HOME");
    Path configHome =
        xdgConfigHome == null || xdgConfigHome.isBlank()
            ? Path.of(System.getProperty("user.home"), ".config")
            : Path.of(xdgConfigHome);
    return configHome.resolve("translate-api").resolve("config.yaml");
  }

  private static String requiredText(JsonNode config, String field, Path configFile) {
    JsonNode node = config.get(field);
    if (node == null || node.isNull() || node.asText().isBlank()) {
      throw new IllegalArgumentException(configFile + ": missing field '" + field + "'");
    }
    return node.asText();
  }

  private static JsonNode readYaml(Path file) {
    try {
      JsonNode node = new YAMLMapper().readTree(file.toFile());
      return node == null ? new YAMLMapper().createObjectNode() : node;
    } catch (IOException e) {
      throw new IllegalArgumentException("unable to read " + file + ": " + e.getMessage(), e);
    }
  }

  private static URI parseUri(String url) {
    try {
      URI uri = new URI(url);
      if (uri.getScheme() == null) {
        throw new IllegalArgumentException(
            "url " + url + " invalid: missing provider scheme (e.g. deepl:https://...)");
      }
      return uri;
    } catch (URISyntaxException e) {
      throw new IllegalArgumentException("url " + url + " invalid: " + e.getMessage(), e);
    }
  }

  private static TranslationProviderConfiguration createProviderConfiguration(
      SupportedProvider providerType, URI uri, String url, Path userConfigFile)
      throws URISyntaxException {

    String providerUrl = uri.getSchemeSpecificPart();
    Map<String, String> params;
    int paramStart = providerUrl.indexOf('?');
    if (paramStart != -1) {
      params = splitQuery(providerUrl.substring(paramStart + 1));
      providerUrl = providerUrl.substring(0, paramStart);
    } else {
      params = Collections.emptyMap();
    }

    if (providerType == SupportedProvider.LIBRE_TRANSLATE) {
      LibreTranslateTranslationProviderConfiguration.Builder builder =
          LibreTranslateTranslationProviderConfiguration.builder().url(providerUrl);
      String apiKey = key(params, providerType, LIBRE_TRANSLATE_KEY, userConfigFile);
      if (apiKey != null) {
        builder.apiKey(apiKey);
      }
      return builder.build();
    } else if (providerType == SupportedProvider.DEEPL) {
      String authKey = key(params, providerType, DEEPL_KEY, userConfigFile);
      if (authKey == null) {
        throw new IllegalArgumentException(
            "no DeepL authKey found in url "
                + url
                + " or in "
                + userConfigFile
                + " (deepl.authKey)");
      }
      return DeeplTranslationProviderConfiguration.builder()
          .url(providerUrl)
          .authKey(authKey)
          .build();
    }
    throw new IllegalArgumentException("Unsupported provider " + providerType);
  }

  private static String key(
      Map<String, String> params,
      SupportedProvider providerType,
      String keyName,
      Path userConfigFile) {
    String key = params.get(keyName);
    if (key != null && !key.isBlank()) {
      return key;
    }
    if (userConfigFile == null || !Files.exists(userConfigFile)) {
      return null;
    }
    JsonNode node = readYaml(userConfigFile).path(providerType.getSchema()).path(keyName);
    return node.isMissingNode() || node.isNull() || node.asText().isBlank() ? null : node.asText();
  }

  private static Map<String, String> splitQuery(String query) {
    Map<String, String> queryPairs = new LinkedHashMap<>();
    for (String pair : query.split("&")) {
      if (pair.isEmpty()) {
        continue;
      }
      int idx = pair.indexOf('=');
      String name = idx == -1 ? pair : pair.substring(0, idx);
      String value = idx == -1 ? "" : pair.substring(idx + 1);
      queryPairs.put(
          URLDecoder.decode(name, StandardCharsets.UTF_8),
          URLDecoder.decode(value, StandardCharsets.UTF_8));
    }
    return queryPairs;
  }

  public SupportedProvider getProviderType() {
    return this.providerType;
  }

  public TranslationProviderConfiguration getProviderConfiguration() {
    return this.providerConfiguration;
  }

  public Path getDir() {
    return this.dir;
  }

  public String getSourceLang() {
    return this.sourceLang;
  }

  public Path getOutput() {
    return this.output;
  }

  /**
   * @return the target languages or {@code null} if all languages supported by the provider are
   *     to be translated
   */
  public List<String> getTargetLanguages() {
    return this.targetLanguages;
  }

  static void printUsage(String command) {
    System.out.println(
        "translate " + command + " <url> <dir> <source-lang> <output-dir> [target-lang]...");
    System.out.println("translate " + command);
    System.out.println("");
    System.out.println(
        "<url>                   URL to translation server: "
            + "<provider-scheme>:http[s]://host/[?params]");
    System.out.println("<dir>                   directory in which language specific subfolders");
    System.out.println("                        \"de\", \"en\", ... are contained.");
    System.out.println("<source-lang>           Language to be translated. This must also be ");
    System.out.println("                        as a directory in <dir>.");
    System.out.println("<output-dir>            directory in which the translations are written");
    System.out.println("[target-lang]...        languages to translate into");
    System.out.println("                        (default: all languages of the provider)");
    System.out.println("");
    System.out.println("Without arguments the values are read from ./" + PROJECT_CONFIG_FILE + ":");
    System.out.println("  url: deepl:https://api.deepl.com");
    System.out.println("  dir: src/lang");
    System.out.println("  sourceLang: de");
    System.out.println("  output: src/lang");
    System.out.println("  targetLanguages: [en, fr]   # optional");
    System.out.println("");
    System.out.println("If the url contains no key, it is read from " + defaultUserConfigFile());
    System.out.println("  deepl:");
    System.out.println("    authKey: <key>");
    System.out.println("  libretranslate:");
    System.out.println("    apiKey: <key>");
    System.out.println("");
    System.out.println("Supported provider-scheme:");
    for (String provider : SupportedProvider.scheme()) {
      System.out.println("  " + provider);
    }
  }
}
