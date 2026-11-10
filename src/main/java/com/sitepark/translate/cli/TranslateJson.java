package com.sitepark.translate.cli;

import com.sitepark.translate.SupportedProvider;
import com.sitepark.translate.TranslationConfiguration;
import com.sitepark.translate.translator.JsonFileListTranslator;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

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

    this.jsonFileListTranslator =
        JsonFileListTranslator.builder()
            .dir(args.getDir())
            .output(args.getOutput())
            .sourceLang(args.getSourceLang())
            .translatorConfiguration(translatorConfiguration)
            .logger(new ConsoleLogger(true))
            .build();
  }

  protected void run() throws IOException {
    assert this.jsonFileListTranslator != null : "jsonFileListTranslator is null";
    this.jsonFileListTranslator.translate(this.providerType, this.targetLanguages);
  }

  private void usage() {
    CommandArguments.printUsage(COMMAND_NAME);
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
