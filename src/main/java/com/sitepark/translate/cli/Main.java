package com.sitepark.translate.cli;

@SuppressWarnings("PMD.SystemPrintln")
public final class Main {

  private Main() {}

  public static void main(String... argv) {

    if (argv.length == 0) {
      System.out.println("missing command");
      return;
    }

    String command = argv[0];
    String[] arguments = new String[argv.length - 1];
    System.arraycopy(argv, 1, arguments, 0, argv.length - 1);

    if (TranslateJson.COMMAND_NAME.equals(command)) {
      TranslateJson.execute(arguments);
    } else {
      System.err.println("unknown command: " + command);
    }
  }
}
