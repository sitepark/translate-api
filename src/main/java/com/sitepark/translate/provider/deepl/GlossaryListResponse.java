package com.sitepark.translate.provider.deepl;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class GlossaryListResponse {
  @JsonProperty public List<GlossaryResponse> glossaries;
}
