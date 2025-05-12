package com.sitepark.translate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

@SuppressWarnings({"PMD.UnitTestContainsTooManyAsserts"})
class TranslationProviderExceptionTest {

  @Test
  void test() {
    Exception cause = new Exception();
    Exception exception =
        assertThrows(
            TranslationProviderException.class,
            () -> {
              throw new TranslationProviderException("test", cause);
            });

    assertSame(cause, exception.getCause(), "same cause exception expected");
    assertEquals(exception.getMessage(), "test", "'test' message expected");
  }
}
