package com.sitepark.translate.cli;

import com.sitepark.translate.SupportedProvider;
import com.sitepark.translate.TranslationConfiguration;
import com.sitepark.translate.TranslationProviderConfiguration;
import com.sitepark.translate.provider.deepl.DeeplTranslationProviderConfiguration;
import com.sitepark.translate.provider.libretranslate.LibreTranslateTranslationProviderConfiguration;
import com.sitepark.translate.translator.JsonFileListTranslator;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("PMD")
public class TranslateJson {

  private JsonFileListTranslator jsonFileListTranslator;

  private SupportedProvider providerType;

  private List<String> targetLanguages;

  public static final String COMMAND_NAME = "translate-json";

  public static void execute(String... arguments) {

    TranslateJson command = new TranslateJson();

    try {
      command.parseArguments(arguments);
      command.run();
    } catch (Exception e) {
      System.err.println("ERROR: " + e.getMessage());
      e.printStackTrace();
      command.usage();
    }
  }

  protected JsonFileListTranslator getTranslator() {
    return this.jsonFileListTranslator;
  }

  protected void parseArguments(String... arguments) {

    if (arguments.length < 4) {
      throw new IllegalArgumentException(
          "<url>, <dir>, <base-lang> <output-dir> [target-language]... expected");
    }

    String url = arguments[0];
    Path dir = Paths.get(arguments[1]).toAbsolutePath();
    String sourceLang = arguments[2];
    Path output = Paths.get(arguments[3]).toAbsolutePath();
    Path sourceDir = dir.resolve(sourceLang);
    if (!Files.exists(sourceDir)) {
      throw new IllegalArgumentException("source-dir " + sourceDir + " not exitst");
    }

    if (arguments.length > 4) {
      this.targetLanguages = new ArrayList<>();
      for (int i = 4; i < arguments.length; i++) {
        this.targetLanguages.add(arguments[i]);
      }
    }

    TranslationConfiguration translatorConfiguration =
        TranslationConfiguration.builder()
            .encodePlaceholder(true)
            .translationProviderConfiguration(this.createTranslationProviderConfigurationByUrl(url))
            .build();

    this.jsonFileListTranslator =
        JsonFileListTranslator.builder()
            .dir(dir)
            .output(output)
            .sourceLang(sourceLang)
            .translatorConfiguration(translatorConfiguration)
            .logger(new ConsoleLogger(true))
            .build();
  }

  protected TranslationProviderConfiguration createTranslationProviderConfigurationByUrl(String s) {
    try {
      URI url = new URI(s);

      this.providerType = SupportedProvider.ofScheme(url.getScheme());

      Map<String, String> params = this.splitQuery(new URI(url.getSchemeSpecificPart()));

      String providerUrl = url.getSchemeSpecificPart();
      int paramStart = providerUrl.indexOf('?');
      if (paramStart != -1) {
        providerUrl = providerUrl.substring(0, paramStart);
      }

      if (this.providerType == SupportedProvider.LIBRE_TRANSLATE) {
        LibreTranslateTranslationProviderConfiguration.Builder builder =
            LibreTranslateTranslationProviderConfiguration.builder().url(providerUrl);
        if (params.containsKey("apiKey")) {
          builder.apiKey(params.get("apiKey"));
        }
        return builder.build();
      } else if (this.providerType == SupportedProvider.DEEPL) {
        return DeeplTranslationProviderConfiguration.builder()
            .url(providerUrl)
            .authKey(params.get("authKey"))
            .build();
      } else {
        throw new IllegalArgumentException("Unsupported provider " + this.providerType);
      }

    } catch (URISyntaxException | UnsupportedEncodingException e) {
      throw new IllegalArgumentException("url " + s + " invalid: " + e.getMessage(), e);
    }
  }

  private Map<String, String> splitQuery(URI url) throws UnsupportedEncodingException {
    String query = url.getQuery();
    if (query == null) {
      return Collections.emptyMap();
    }
    Map<String, String> queryPairs = new LinkedHashMap<>();
    String[] pairs = query.split("&");
    for (String pair : pairs) {
      int idx = pair.indexOf("=");
      queryPairs.put(
          URLDecoder.decode(pair.substring(0, idx), "UTF-8"),
          URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
    }
    return queryPairs;
  }

  protected void run() throws IOException {
    assert this.jsonFileListTranslator != null : "jsonFileListTranslator is null";
    this.jsonFileListTranslator.translate(this.providerType, this.targetLanguages);
  }

  private void usage() {
    System.out.println(
        "translate translate-json <libre-translate-url> <dir> <source-lang> <output-dir>");
    System.out.println("");
    System.out.println(
        "<url>                   URL to translation server: "
            + "<provider-scheme>:http[s]://host/[?params]");
    System.out.println("<dir>                   directory in which language specific subfolders");
    System.out.println("                        \"de\", \"en\", ... are contained.");
    System.out.println("<source-lang>           Language to be translated. This must also be ");
    System.out.println("                        as a directory in <dir>.");
    System.out.println("<output-dir>            directory in which the translations are written");
    System.out.println("");
    System.out.println("Supported provider-scheme:");
    for (String provider : SupportedProvider.scheme()) {
      System.out.println("  " + provider);
    }
  }

  private static class ConsoleLogger implements JsonFileListTranslator.Logger {

    private final boolean printStackTrace;

    private ConsoleLogger(boolean printStackTrace) {
      this.printStackTrace = printStackTrace;
    }

    @Override
    public void info(String msg) {
      System.out.println(msg);
    }

    @Override
    public void error(String msg, Throwable t) {
      System.err.println(msg);
      if (this.printStackTrace) {
        t.printStackTrace();
      }
    }
  }
}
