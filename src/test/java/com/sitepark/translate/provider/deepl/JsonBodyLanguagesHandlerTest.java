package com.sitepark.translate.provider.deepl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;

@SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
class JsonBodyLanguagesHandlerTest {

  @Test
  void test() {
    String json =
        "[{"
            + "\"language\":\"de\","
            + "\"name\":\"deutsch\","
            + "\"supports_formality\":true"
            + "}]";
    ByteArrayInputStream in = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));

    Supplier<List<TransportLanguage>> supplier = JsonBodyLanguagesHandler.toSupplierOfType(in);

    List<TransportLanguage> res = supplier.get();

    TransportLanguage transportLanguage = res.get(0);

    assertEquals("de", transportLanguage.getLanguage(), "unexpected language");
    assertEquals("deutsch", transportLanguage.getName(), "unexpected name");
    assertTrue(transportLanguage.isSupportsFormality(), "expect supports formality");
  }
}
