package com.sitepark.translate.provider.libretranslate;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.nio.charset.StandardCharsets;
import java.security.ProviderException;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sitepark.translate.Format;
import com.sitepark.translate.Glossary;
import com.sitepark.translate.SupportedLanguages;
import com.sitepark.translate.TranslationConfiguration;
import com.sitepark.translate.TranslationEvent;
import com.sitepark.translate.TranslationLanguage;
import com.sitepark.translate.TranslationProvider;
import com.sitepark.translate.provider.deepl.TransportResponse;
import com.sitepark.translate.translator.UnifiedSourceText;
import com.sitepark.translate.translator.entity.Decoder;
import com.sitepark.translate.translator.entity.Encoder;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings
@SuppressWarnings("PMD")
public class LibreTranslateTranslationProvider implements TranslationProvider {

	private final TranslationConfiguration translatorConfiguration;

	public LibreTranslateTranslationProvider(TranslationConfiguration translatorConfiguration) {
		this.translatorConfiguration = translatorConfiguration;
	}

	public String[] translate(Format format, TranslationLanguage language, final String... sourceText) {

		String[] encodedSourceText = this.encodePlacerholder(sourceText);

		UnifiedSourceText unifiedSourceText = new UnifiedSourceText(encodedSourceText);

		try {

			long start = System.currentTimeMillis();

			String[] translated = this.translationRequest(
					format,
					language,
					unifiedSourceText.getSourceText());

			this.translatorConfiguration.getTranslationListener().ifPresent(listener -> {
				listener.translated(TranslationEvent.builder()
						.translationTime(System.currentTimeMillis() - start)
						.translationLanguage(language)
						.chunks(encodedSourceText.length)
						.sourceBytes(this.byteCount(unifiedSourceText.getSourceText()))
						.targetBytes(this.byteCount(translated))
						.build());
			});

			String[] decodedTranslation = this.decodePlacerholder(translated);

			return unifiedSourceText.expandTranslation(decodedTranslation);

		} catch (InterruptedException | IOException e) {
			throw new ProviderException(e.getMessage(), e);
		}
	}

	protected String[] translationRequest(
			Format format,
			TranslationLanguage language,
			String... source)
			throws IOException, InterruptedException {

		URI uri = this.buildUri("/translate");

		TransportRequest req = TransportRequest.builder()
				.source(language.getSource())
				.target(language.getTarget())
				.format(format)
				.q(source)
				.build();

		HttpRequest request = HttpRequest.newBuilder(uri)
				.header("Content-Type", "application/json")
				.header("Accept", "application/json")
				.header("Origin", this.getProviderConfiguration().getUri().toString())
				.POST(this.toPostBody(req))
				.build();

		HttpClient client = this.createHttpClient();

		TransportResponse response = client
					.send(request, new JsonBodyHandler<>(TransportResponse.class))
					.body()
					.get();

		return response.getTranslations();
	}

	private String[] encodePlacerholder(String... q) {
		if (!this.translatorConfiguration.isEncodePlaceholder()) {
			return q;
		}

		return Encoder.encode(q);
	}

	private String[] decodePlacerholder(String... q) {

		if (!this.translatorConfiguration.isEncodePlaceholder()) {
			return q;
		}

		return Decoder.decode(q);
	}

	private int byteCount(String... array) {
		int count = 0;
		for (String s : array) {
			count += s.getBytes(StandardCharsets.UTF_8).length;
		}
		return count;
	}

	private BodyPublisher toPostBody(TransportRequest req) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			String reqJson = mapper.writeValueAsString(req);
			return HttpRequest.BodyPublishers.ofString(reqJson);
		} catch (Exception e) {
			throw new ProviderException(e.getMessage(), e);
		}
	}

	public SupportedLanguages getSupportedLanguages() {

		URI uri = this.buildUri("/languages");

		HttpRequest request = HttpRequest.newBuilder(uri)
				.header("Accept", "application/json").build();

		HttpClient client = this.createHttpClient();

		try {
			return client.send(request, new JsonBodyHandler<>(SupportedLanguages.class)).body().get();
		} catch (InterruptedException | IOException e) {
			throw new ProviderException(e.getMessage(), e);
		}
	}

	private LibreTranslateTranslationProviderConfiguration getProviderConfiguration() {
		return this.translatorConfiguration.getTranslationProviderConfiguration(
				LibreTranslateTranslationProviderConfiguration.class);
	}

	private URI buildUri(String path) {
		try {
			return new URI(this.getProviderConfiguration().getUri() + path);
		} catch (URISyntaxException e) {
			throw new ProviderException(e.getMessage(), e);
		}
	}

	private HttpClient createHttpClient() {
		HttpClient.Builder builder = HttpClient.newBuilder();
		if (this.getProviderConfiguration().getProxy().isPresent()) {
			builder.proxy(this.getProviderConfiguration().getProxy().get());
		}
		return builder.build();
	}

	@Override
	public Optional<Glossary> getGlossary(String id) {
		return Optional.empty();
	}

	@Override
	public Optional<String> getGlossaryId(String sourceLanguage, String targetLanguage) {
		return Optional.empty();
	}

	@Override
	public String recreate(Glossary glossar) {
		throw new UnsupportedOperationException("LibreTranslate not yet support a glossary.");
	}

	@Override
	public void removeGlossary(String id) {
	}
}
