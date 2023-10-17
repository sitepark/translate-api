package com.sitepark.translate.provider.deepl;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GlossaryListResponse {
	@JsonProperty
	public List<GlossaryResponse> glossaries;
}
