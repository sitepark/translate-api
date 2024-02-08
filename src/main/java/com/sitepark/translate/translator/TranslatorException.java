package com.sitepark.translate.translator;

public class TranslatorException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public TranslatorException(String msg, Throwable t) {
    super(msg, t);
  }
}
