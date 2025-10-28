package com.sitepark.translate.provider.deepl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class DeeplTestConnection {

	private final String url;

	private final String authKey;

	private DeeplTestConnection(String url, String authKey) {
		this.url = url;
		this.authKey = authKey;
	}

	public static DeeplTestConnection get() throws IOException {

		Properties props = new Properties();
		try (
			InputStream is = DeeplTestConnection.class.getResourceAsStream("/deepl.local.properties")
		) {
			props.load(is);
		}

		if (!props.containsKey("url")) {
			throw new IOException("url missing in deepl.local.properties");
		}
		if (!props.containsKey("authKey")) {
			throw new IOException("authKey missing in deepl.local.properties");
		}
		return new DeeplTestConnection(props.getProperty("url"), props.getProperty("authKey"));
	}

	public String getUrl() {
		return this.url;
	}

	public String getAuthKey() {
		return this.authKey;
	}
}
