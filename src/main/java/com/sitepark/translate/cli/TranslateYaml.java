package com.sitepark.translate.cli;

import com.sitepark.translate.SupportedProvider;
import com.sitepark.translate.TranslationConfiguration;
import com.sitepark.translate.translator.YamlFileListTranslator;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("PMD")
public class TranslateYaml {

  private List<YamlFileListTranslator> translators;

  private SupportedProvider providerType;

  private List<String> targetLanguages;

  public static final String COMMAND_NAME = "translate-yaml";

  public static void execute(String... arguments) {

    TranslateYaml command = new TranslateYaml();

    try {
      command.parseArguments(arguments);
      command.run();
    } catch (Exception e) {
      System.err.println("ERROR: " + e.getMessage());
      e.printStackTrace();
      command.usage();
    }
  }

  protected List<YamlFileListTranslator> getTranslators() {
    return this.translators;
  }

  protected void parseArguments(String... arguments) {
    this.init(CommandArguments.parse(arguments));
  }

  protected void parseArguments(Path workDir, Path userConfigFile, String... arguments) {
    this.init(CommandArguments.parse(workDir, userConfigFile, arguments));
  }

  private void init(CommandArguments args) {
    this.providerType = args.getProviderType();
    this.targetLanguages = args.getTargetLanguages();

    TranslationConfiguration translatorConfiguration =
        TranslationConfiguration.builder()
            .encodePlaceholder(true)
            .translationProviderConfiguration(args.getProviderConfiguration())
            .build();

    List<YamlFileListTranslator> list = new ArrayList<>();
    for (CommandArguments.Directory directory : args.getDirectories()) {
      list.add(
          YamlFileListTranslator.builder()
              .dir(directory.getDir())
              .output(directory.getOutput())
              .sourceLang(args.getSourceLang())
              .translatorConfiguration(translatorConfiguration)
              .logger(new ConsoleLogger(true))
              .build());
    }
    this.translators = List.copyOf(list);
  }

  protected void run() throws IOException {
    assert this.translators != null : "translators is null";
    for (YamlFileListTranslator translator : this.translators) {
      translator.translate(this.providerType, this.targetLanguages);
    }
  }

  private void usage() {
    CommandArguments.printUsage(COMMAND_NAME);
  }

  private static class ConsoleLogger implements YamlFileListTranslator.Logger {

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
