package com.sitepark.translate.provider.deepl;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;

@SuppressWarnings("PMD.DataClass")
public class GlossaryResponse {
  @JsonProperty("glossary_id")
  public String glossaryId;

  @JsonProperty("name")
  public String name;

  @JsonProperty("ready")
  public boolean ready;

  @JsonProperty("source_lang")
  public String sourceLang;

  @JsonProperty("target_lang")
  public String targetLang;

  @JsonProperty("creation_time")
  public OffsetDateTime creationTime;

  @JsonProperty("entry_count")
  public int entryCount;

  public GlossaryResponse() {}
}
