package com.sitepark.translate.cli;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

import com.sitepark.translate.SupportedProvider;
import com.sitepark.translate.provider.deepl.DeeplTranslationProviderConfiguration;
import com.sitepark.translate.provider.libretranslate.LibreTranslateTranslationProviderConfiguration;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class CommandArgumentsTest {

  private static final String PROJECT_CONFIG =
      """
      url: deepl:https://dummy
      dir: src/lang
      sourceLang: de
      output: out
      targetLanguages:
        - en
        - nl
      """;

  private static final String USER_CONFIG =
      """
      deepl:
        authKey: central
      libretranslate:
        apiKey: central
      """;

  @TempDir Path workDir;

  private Path userConfig;

  @BeforeEach
  void setUp() throws IOException {
    Files.createDirectories(this.workDir.resolve("src/lang/de"));
    this.userConfig = this.workDir.resolve("user/config.yaml");
  }

  @Test
  void testArgumentsResolveDirRelativeToWorkDir() {
    CommandArguments args = this.parseArguments("deepl:https://dummy?authKey=abc");

    assertThat(args.getDirectories().get(0).getDir())
        .as("dir argument should be resolved against the working directory")
        .isEqualTo(this.workDir.resolve("src/lang"));
  }

  @Test
  void testArgumentsWithoutTargetLanguagesTranslateIntoAllLanguages() {
    CommandArguments args = this.parseArguments("deepl:https://dummy?authKey=abc");

    assertThat(args.getTargetLanguages())
        .as("without target languages all provider languages should be used (null)")
        .isNull();
  }

  @Test
  void testArgumentsWithTargetLanguages() {
    CommandArguments args =
        this.parseArguments("deepl:https://dummy?authKey=abc", "src/lang", "de", "out", "en", "fr");

    assertThat(args.getTargetLanguages())
        .as("additional arguments should be used as target languages")
        .containsExactly("en", "fr");
  }

  @Test
  void testDeeplUrlWithoutQuery() {
    CommandArguments args = this.parseArguments("deepl:https://dummy?authKey=abc");

    assertThat(deepl(args).getUri())
        .as("provider url should not contain the query")
        .hasToString("https://dummy");
  }

  @Test
  void testKeyFromUrl() {
    CommandArguments args = this.parseArguments("deepl:https://dummy?authKey=abc");

    assertThat(deepl(args).getAuthKey()).as("authKey should be taken from url").isEqualTo("abc");
  }

  @Test
  void testKeyInUrlWinsOverUserConfig() throws IOException {
    this.writeUserConfig();

    CommandArguments args = this.parseArguments("deepl:https://dummy?authKey=abc");

    assertThat(deepl(args).getAuthKey())
        .as("authKey from url should take precedence over the user config")
        .isEqualTo("abc");
  }

  @Test
  void testDeeplKeyFromUserConfig() throws IOException {
    this.writeUserConfig();

    CommandArguments args = this.parseArguments("deepl:https://dummy");

    assertThat(deepl(args).getAuthKey())
        .as("authKey should be read from the user config if missing in url")
        .isEqualTo("central");
  }

  @Test
  void testLibreTranslateKeyFromUserConfig() throws IOException {
    this.writeUserConfig();

    CommandArguments args = this.parseArguments("libretranslate:https://dummy");

    assertThat(
            ((LibreTranslateTranslationProviderConfiguration) args.getProviderConfiguration())
                .getApiKey())
        .as("apiKey should be read from the user config if missing in url")
        .contains("central");
  }

  @Test
  void testDeeplWithoutKeyIsRejected() {
    assertThatThrownBy(() -> this.parseArguments("deepl:https://dummy"))
        .as("DeepL without authKey in url and user config should be rejected")
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("authKey");
  }

  @Test
  void testMissingSourceDirIsRejected() {
    assertThatThrownBy(
            () -> this.parseArguments("deepl:https://dummy?authKey=abc", "src/lang", "fr", "out"))
        .as("missing source language directory should be rejected")
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("source-dir");
  }

  @Test
  void testProjectConfigIsUsedWithoutArguments() throws IOException {
    this.writeProjectConfig(PROJECT_CONFIG);
    this.writeUserConfig();

    CommandArguments args = CommandArguments.parse(this.workDir, this.userConfig);

    assertThat(
            List.of(
                args.getProviderType(),
                deepl(args).getAuthKey(),
                args.getDirectories().get(0).getDir(),
                args.getSourceLang(),
                args.getDirectories().get(0).getOutput(),
                args.getTargetLanguages()))
        .as("all values should be taken from translate.yaml and the key from the user config")
        .containsExactly(
            SupportedProvider.DEEPL,
            "central",
            this.workDir.resolve("src/lang"),
            "de",
            this.workDir.resolve("out"),
            List.of("en", "nl"));
  }

  @Test
  void testProjectConfigWithoutTargetLanguagesTranslatesIntoAllLanguages() throws IOException {
    this.writeProjectConfig(
        """
        url: deepl:https://dummy?authKey=abc
        dir: src/lang
        sourceLang: de
        output: out
        """);

    CommandArguments args = CommandArguments.parse(this.workDir, this.userConfig);

    assertThat(args.getTargetLanguages())
        .as("without targetLanguages all provider languages should be used (null)")
        .isNull();
  }

  @Test
  void testProjectConfigWithMissingFieldIsRejected() throws IOException {
    this.writeProjectConfig(
        """
        url: deepl:https://dummy?authKey=abc
        dir: src/lang
        output: out
        """);

    assertThatThrownBy(() -> CommandArguments.parse(this.workDir, this.userConfig))
        .as("translate.yaml without sourceLang should be rejected")
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("sourceLang");
  }

  @Test
  void testProjectConfigWithNonStringTargetLanguageIsRejected() throws IOException {
    this.writeProjectConfig(
        """
        url: deepl:https://dummy?authKey=abc
        dir: src/lang
        sourceLang: de
        output: out
        targetLanguages: [en, no]
        """);

    assertThatThrownBy(() -> CommandArguments.parse(this.workDir, this.userConfig))
        .as("unquoted 'no' is read as boolean by YAML and should be rejected")
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("not a string");
  }

  @Test
  void testProjectConfigWithDirectories() throws IOException {
    Files.createDirectories(this.workDir.resolve("module/lang/de"));
    this.writeProjectConfig(
        """
        url: deepl:https://dummy?authKey=abc
        sourceLang: de
        directories:
          - dir: src/lang
            output: out
          - dir: module/lang
        """);

    CommandArguments args = CommandArguments.parse(this.workDir, this.userConfig);

    assertThat(args.getDirectories())
        .as("each entry should be resolved against translate.yaml, output defaults to dir")
        .extracting(CommandArguments.Directory::getDir, CommandArguments.Directory::getOutput)
        .containsExactly(
            tuple(this.workDir.resolve("src/lang"), this.workDir.resolve("out")),
            tuple(this.workDir.resolve("module/lang"), this.workDir.resolve("module/lang")));
  }

  @Test
  void testProjectConfigWithDirectoriesEntryWithoutDirIsRejected() throws IOException {
    this.writeProjectConfig(
        """
        url: deepl:https://dummy?authKey=abc
        sourceLang: de
        directories:
          - output: out
        """);

    assertThatThrownBy(() -> CommandArguments.parse(this.workDir, this.userConfig))
        .as("directories entry without dir should be rejected")
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("'dir'");
  }

  @Test
  void testProjectConfigWithEmptyDirectoriesIsRejected() throws IOException {
    this.writeProjectConfig(
        """
        url: deepl:https://dummy?authKey=abc
        sourceLang: de
        directories: []
        """);

    assertThatThrownBy(() -> CommandArguments.parse(this.workDir, this.userConfig))
        .as("empty directories should be rejected")
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("non-empty list");
  }

  @Test
  void testProjectConfigWithDirectoriesAndDirIsRejected() throws IOException {
    this.writeProjectConfig(
        """
        url: deepl:https://dummy?authKey=abc
        sourceLang: de
        dir: src/lang
        directories:
          - dir: src/lang
        """);

    assertThatThrownBy(() -> CommandArguments.parse(this.workDir, this.userConfig))
        .as("directories together with dir should be rejected")
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("not both");
  }

  @Test
  void testProjectConfigWithMissingSourceDirInDirectoriesIsRejected() throws IOException {
    this.writeProjectConfig(
        """
        url: deepl:https://dummy?authKey=abc
        sourceLang: de
        directories:
          - dir: src/lang
          - dir: missing/lang
        """);

    assertThatThrownBy(() -> CommandArguments.parse(this.workDir, this.userConfig))
        .as("a directory without source language folder should be rejected")
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining(this.workDir.resolve("missing/lang/de").toString());
  }

  @Test
  void testNoArgumentsAndNoProjectConfigIsRejected() {
    assertThatThrownBy(() -> CommandArguments.parse(this.workDir, this.userConfig))
        .as("call without arguments and without translate.yaml should be rejected")
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining(CommandArguments.PROJECT_CONFIG_FILE);
  }

  private CommandArguments parseArguments(String url) {
    return this.parseArguments(url, "src/lang", "de", "out");
  }

  private CommandArguments parseArguments(String... arguments) {
    return CommandArguments.parse(this.workDir, this.userConfig, arguments);
  }

  private void writeProjectConfig(String content) throws IOException {
    Files.writeString(this.workDir.resolve(CommandArguments.PROJECT_CONFIG_FILE), content);
  }

  private void writeUserConfig() throws IOException {
    Files.createDirectories(this.userConfig.getParent());
    Files.writeString(this.userConfig, USER_CONFIG);
  }

  private static DeeplTranslationProviderConfiguration deepl(CommandArguments args) {
    return (DeeplTranslationProviderConfiguration) args.getProviderConfiguration();
  }
}
