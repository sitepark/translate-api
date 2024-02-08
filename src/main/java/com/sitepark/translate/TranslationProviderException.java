package com.sitepark.translate;

public class TranslationProviderException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public TranslationProviderException(String msg) {
    super(msg);
  }

  public TranslationProviderException(String msg, Throwable t) {
    super(msg, t);
  }
}
