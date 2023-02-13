package com.sitepark.translate.provider.deepl;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.function.Supplier;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonBodyLanguagesHandler implements HttpResponse.BodyHandler<Supplier<List<TransportLanguage>>> {

	@Override
	public HttpResponse.BodySubscriber<Supplier<List<TransportLanguage>>> apply(
			HttpResponse.ResponseInfo responseInfo) {
		return asJSON();
	}

	public static HttpResponse.BodySubscriber<Supplier<List<TransportLanguage>>> asJSON() {
		HttpResponse.BodySubscriber<InputStream> upstream = HttpResponse.BodySubscribers.ofInputStream();

		return HttpResponse.BodySubscribers.mapping(upstream, inputStream -> toSupplierOfType(inputStream));
	}

	public static Supplier<List<TransportLanguage>> toSupplierOfType(InputStream inputStream) {
		return () -> {
			try (InputStream stream = inputStream) {
				ObjectMapper objectMapper = new ObjectMapper();
				return objectMapper.readValue(
						stream,
						new TypeReference<List<TransportLanguage>>() { });
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		};
	}
}
