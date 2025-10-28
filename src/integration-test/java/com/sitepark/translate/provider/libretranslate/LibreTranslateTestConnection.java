package com.sitepark.translate.provider.libretranslate;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class LibreTranslateTestConnection {

	private final String url;

	private final String apiKey;

	private LibreTranslateTestConnection(String url, String apiKey) {
		this.url = url;
		this.apiKey = apiKey;
	}

	public static LibreTranslateTestConnection get() throws IOException {

		Properties props = new Properties();
		try (
			InputStream is = LibreTranslateTestConnection.class.getResourceAsStream("/libreTranslate.local.properties")
		) {
			props.load(is);
		}

		if (!props.containsKey("url")) {
			throw new IOException("url missing in deepl.local.properties");
		}
		return new LibreTranslateTestConnection(props.getProperty("url"), props.getProperty("apiKey"));
	}

	public String getUrl() {
		return this.url;
	}

	public String getApiKey() {
		return this.apiKey;
	}
}
