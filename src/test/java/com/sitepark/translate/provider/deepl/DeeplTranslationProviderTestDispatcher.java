package com.sitepark.translate.provider.deepl;

import java.net.HttpURLConnection;

import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;

public class DeeplTranslationProviderTestDispatcher extends Dispatcher {

	@Override
	public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
		if (request.getMethod().equals("GET")) {
			return this.dispatchGet(request);
		} else if (request.getMethod().equals("POST")) {
			return this.dispatchPost(request);
		} else if (request.getMethod().equals("DELETE")) {
			return this.dispatchDelete(request);
		} else {
			throw new InterruptedException("Unsupported method: " + request.getMethod());
		}
	}

	private MockResponse dispatchGet(RecordedRequest request) throws InterruptedException {
		switch (request.getPath()) {
			case "/glossaries":
				return new MockResponse().setResponseCode(HttpURLConnection.HTTP_OK).setBody("{\"glossaries\": [{\n"
						+ "  \"glossary_id\": \"ee0c28af-e9cd-4b59-9199-f114ebc0d602\",\n"
						+ "  \"name\": \"de - en\",\n"
						+ "  \"ready\": true,\n"
						+ "  \"source_lang\": \"de\",\n"
						+ "  \"target_lang\": \"en\",\n"
						+ "  \"creation_time\": \"2023-10-02T14:07:01.218419Z\",\n"
						+ "  \"entry_count\": 2\n"
						+ "}]}");
			case "/glossaries/ee0c28af-e9cd-4b59-9199-f114ebc0d602":
				return new MockResponse().setResponseCode(HttpURLConnection.HTTP_OK).setBody("{\n"
						+ "  \"glossary_id\": \"ee0c28af-e9cd-4b59-9199-f114ebc0d602\",\n"
						+ "  \"name\": \"de - en\",\n"
						+ "  \"ready\": true,\n"
						+ "  \"source_lang\": \"de\",\n"
						+ "  \"target_lang\": \"en\",\n"
						+ "  \"creation_time\": \"2023-10-02T14:07:01.218419Z\",\n"
						+ "  \"entry_count\": 2\n"
						+ "}");
			case "/glossaries/ee0c28af-e9cd-4b59-9199-f114ebc0d602/entries":
				return new MockResponse().setResponseCode(HttpURLConnection.HTTP_OK).setBody("Foo\tBar\nHallo\tHey\n");
			case "/languages?type=source":
			case "/languages?type=target":
				return new MockResponse().setResponseCode(HttpURLConnection.HTTP_OK).setBody("[{\n"
						+ "    \"language\": \"DE\",\n"
						+ "    \"name\": \"German\",\n"
						+ "    \"supports_formality\": true\n"
						+ "  },{\n"
						+ "    \"language\": \"EN-US\",\n"
						+ "    \"name\": \"English (American)\",\n"
						+ "    \"supports_formality\": false\n"
						+ "  }]");
			default:
				return new MockResponse().setResponseCode(HttpURLConnection.HTTP_NOT_FOUND);
		}
	}

	private MockResponse dispatchPost(RecordedRequest request) throws InterruptedException {
		switch (request.getPath()) {
			case "/translate":
				return new MockResponse().setResponseCode(HttpURLConnection.HTTP_OK).setBody("{\n"
						+ "  \"translations\": [\n"
						+ "    {\n"
						+ "      \"detected_source_language\": \"DE\",\n"
						+ "      \"text\": \"Hello\"\n"
						+ "    },\n"
						+ "    {\n"
						+ "      \"detected_source_language\": \"DE\",\n"
						+ "      \"text\": \"World\"\n"
						+ "    }\n"
						+ "  ]\n"
						+ "}");
			case "/glossaries":
				return new MockResponse().setResponseCode(HttpURLConnection.HTTP_OK).setBody("{\n"
						+ "  \"glossary_id\": \"ee0c28af-e9cd-4b59-9199-f114ebc0d602\",\n"
						+ "  \"name\": \"de - en\",\n"
						+ "  \"ready\": true,\n"
						+ "  \"source_lang\": \"en\",\n"
						+ "  \"target_lang\": \"de\",\n"
						+ "  \"creation_time\": \"2023-10-04T09:15:55.805876Z\",\n"
						+ "  \"entry_count\": 2\n"
						+ "}");
			default:
				return new MockResponse().setResponseCode(HttpURLConnection.HTTP_NOT_FOUND);
		}
	}

	private MockResponse dispatchDelete(RecordedRequest request) throws InterruptedException {
		switch (request.getPath()) {
			case "/glossaries/ee0c28af-e9cd-4b59-9199-f114ebc0d602":
				return new MockResponse().setResponseCode(HttpURLConnection.HTTP_NO_CONTENT);
			default:
				return new MockResponse().setResponseCode(HttpURLConnection.HTTP_NOT_FOUND);
		}
	}

}
