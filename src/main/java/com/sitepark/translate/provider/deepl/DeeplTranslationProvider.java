package com.sitepark.translate.provider.deepl;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sitepark.translate.Format;
import com.sitepark.translate.Glossary;
import com.sitepark.translate.GlossaryEntry;
import com.sitepark.translate.Language;
import com.sitepark.translate.SupportedLanguages;
import com.sitepark.translate.TranslationConfiguration;
import com.sitepark.translate.TranslationEvent;
import com.sitepark.translate.TranslationLanguage;
import com.sitepark.translate.TranslationProvider;
import com.sitepark.translate.TranslationProviderException;
import com.sitepark.translate.translator.UnifiedSourceText;
import com.sitepark.translate.translator.entity.Decoder;
import com.sitepark.translate.translator.entity.Encoder;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings
@SuppressWarnings("PMD")
public class DeeplTranslationProvider implements TranslationProvider {

	private final TranslationConfiguration translatorConfiguration;

	public DeeplTranslationProvider(TranslationConfiguration translatorConfiguration) {
		this.translatorConfiguration = translatorConfiguration;
	}

	public String[] translate(Format format, TranslationLanguage language, final String... sourceText) {

		String[] sourceTextToTranslate = sourceText;
		if (format == Format.HTML) {
			sourceTextToTranslate = this.encodePlacerholder(sourceText);
		}

		UnifiedSourceText unifiedSourceText = new UnifiedSourceText(sourceTextToTranslate);

		try {

			long start = System.currentTimeMillis();

			String[] translated = translationRequest(format, language, unifiedSourceText.getSourceText());

			this.translatorConfiguration.getTranslationListener().ifPresent(listener -> {
				listener.translated(TranslationEvent.builder()
						.translationTime(System.currentTimeMillis() - start)
						.translationLanguage(language)
						.chunks(sourceText.length)
						.sourceBytes(this.byteCount(unifiedSourceText.getSourceText()))
						.targetBytes(this.byteCount(translated))
						.build());
			});

			String[] decodedTranslation = this.decodePlacerholder(translated);

			return unifiedSourceText.expandTranslation(decodedTranslation);

		} catch (InterruptedException | IOException e) {
			throw new TranslationProviderException(e.getMessage(), e);
		}
	}

	protected String[] translationRequest(Format format, TranslationLanguage language, String... source)
			throws IOException, InterruptedException {

		URI uri = this.buildUri("/translate");

		List<String[]> params = new ArrayList<>();
		params.add(new String[] {"source_lang", language.getSource()});
		params.add(new String[] {"target_lang", language.getTarget()});
		if (format == Format.HTML) {
			params.add(new String[] {"tag_handling", Format.HTML.toString().toLowerCase()});
		}
		for (String text : source) {
			params.add(new String[] {"text", text});
		}

		HttpRequest request = HttpRequest.newBuilder(uri)
				.header("Authorization", "DeepL-Auth-Key " + this.getProviderConfiguration().getAuthKey())
				.header("Accept", "application/json")
				.header("Content-Type", "application/x-www-form-urlencoded")
				.POST(this.buildBody(params))
				.build();

		HttpClient client = this.createHttpClient();

		var response = client
				.send(request, new JsonBodyHandler<>(TransportResponse.class, ErrorResponse.class))
				.body();

		var result = response.get();

		if (result.isSuccess()) {
			return result.getSuccessValue().getTranslations();
		} else {
			throw result.getErrorValue().toException();
		}
	}

	private DeeplTranslationProviderConfiguration getProviderConfiguration() {
		return this.translatorConfiguration.getTranslationProviderConfiguration(
				DeeplTranslationProviderConfiguration.class);
	}


	private HttpRequest.BodyPublisher buildBody(List<String[]> params) {
		var builder = new StringBuilder();
		for (String[] param : params) {
			String name = param[0];
			String value = param[1];
			if (builder.length() > 0) {
				builder.append("&");
			}
			builder.append(URLEncoder.encode(name, StandardCharsets.UTF_8));
			builder.append("=");
			builder.append(URLEncoder.encode(value, StandardCharsets.UTF_8));
		}
		return HttpRequest.BodyPublishers.ofString(builder.toString());
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

	public SupportedLanguages getSupportedLanguages() {

		SupportedLanguages.Builder builder = SupportedLanguages.builder();

		List<TransportLanguage> sourceLanguageList = this.getSourceLanguages();
		List<TransportLanguage> targetLanguageList = this.getTargetLanguages();

		for (TransportLanguage sourceLanguage : sourceLanguageList) {
			Language language = Language.builder()
				.code(sourceLanguage.getLanguage().toLowerCase())
				.name(sourceLanguage.getName())
				.targets(targetLanguageList.stream()
						.map(l -> l.getLanguage().toLowerCase())
						.collect(Collectors.toList())
				)
				.build();
			builder.language(language);
		}

		return builder.build();
	}

	private List<TransportLanguage> getSourceLanguages() {
		return this.getLanguages(LanguageType.SOURCE);
	}

	private List<TransportLanguage> getTargetLanguages() {
		return this.getLanguages(LanguageType.TARGET);
	}

	protected List<TransportLanguage> getLanguages(LanguageType type) {

		URI uri = this.buildUri("/languages?type=" + type);

		HttpRequest request = HttpRequest.newBuilder(uri)
				.header("Authorization", "DeepL-Auth-Key " + this.getProviderConfiguration().getAuthKey())
				.header("Accept", "application/json")
				.build();

		HttpClient client = this.createHttpClient();

		try {
			return client.send(request, new JsonBodyLanguagesHandler()).body().get();
		} catch (InterruptedException | IOException e) {
			throw new TranslationProviderException(e.getMessage(), e);
		}
	}

	private URI buildUri(String path) {
		try {
			return new URI(this.getProviderConfiguration().getUri() + path);
		} catch (URISyntaxException e) {
			throw new TranslationProviderException(e.getMessage(), e);
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

		Optional<GlossaryResponse> response = this.getGlossaryResponse(id);
		if (response.isEmpty()) {
			return Optional.empty();
		}

		return Optional.of(Glossary.builder()
				.sourceLanguage(response.get().sourceLang)
				.targetLanguage(response.get().targetLang)
				.entryList(this.getGlossaryEntries(id))
				.build());
	}

	@Override
	public Optional<String> getGlossaryId(String sourceLanguage, String targetLanguage) {
		List<GlossaryResponse> glossaries = this.getGlossaries(sourceLanguage, targetLanguage);
		return glossaries.stream().map(res -> res.glossaryId).findFirst();
	}

	private List<GlossaryResponse> getGlossaries(String sourceLanguage, String targetLanguage) {

		List<GlossaryResponse> glossaries = new ArrayList<>();

		for (GlossaryResponse glossary : this.getGlossaries()) {
			if (!glossary.sourceLang.equals(sourceLanguage)) {
				continue;
			}
			if (!glossary.targetLang.equals(targetLanguage)) {
				continue;
			}
			glossaries.add(glossary);
		}
		return glossaries;
	}

	private List<GlossaryResponse> getGlossaries() {

		URI uri = this.buildUri("/glossaries");

		HttpRequest request = HttpRequest.newBuilder(uri)
				.header("Authorization", "DeepL-Auth-Key " + this.getProviderConfiguration().getAuthKey())
				.header("Accept", "application/json")
				.build();

		HttpClient client = this.createHttpClient();

		try {
			var response = client
					.send(request, new JsonBodyHandler<>(GlossaryListResponse.class, ErrorResponse.class))
					.body();

			var result = response.get();

			if (!result.isSuccess()) {
				throw result.getErrorValue().toException();
			}

			return result.getSuccessValue().glossaries;

		} catch (InterruptedException | IOException e) {
			throw new TranslationProviderException(e.getMessage(), e);
		}
	}

	private List<GlossaryEntry> getGlossaryEntries(String id) {

		URI uri = this.buildUri("/glossaries/" + id + "/entries");

		HttpRequest request = HttpRequest.newBuilder(uri)
				.header("Authorization", "DeepL-Auth-Key " + this.getProviderConfiguration().getAuthKey())
				.header("Accept", "text/tab-separated-values")
				.build();
		HttpClient client = this.createHttpClient();

		try {
			var response = client
					.send(request, new JsonBodyHandler<>(String.class, ErrorResponse.class))
					.body();

			var result = response.get();

			if (!result.isSuccess()) {
				throw result.getErrorValue().toException();
			}

			List<GlossaryEntry> entries = new ArrayList<>();

			for (String line : result.getSuccessValue().split("\n")) {
				String[] values = line.split("\t");
				String source = values[0];
				String target = values[1];

				entries.add(GlossaryEntry.builder()
						.source(source)
						.target(target)
						.build()
				);
			}

			return entries;

		} catch (InterruptedException | IOException e) {
			throw new TranslationProviderException(e.getMessage(), e);
		}
	}

	private Optional<GlossaryResponse> getGlossaryResponse(String id) {

		URI uri = this.buildUri("/glossaries/" + id);

		HttpRequest request = HttpRequest.newBuilder(uri)
				.header("Authorization", "DeepL-Auth-Key " + this.getProviderConfiguration().getAuthKey())
				.header("Accept", "application/json")
				.build();

		HttpClient client = this.createHttpClient();

		try {
			var response = client
					.send(request, new JsonBodyHandler<>(GlossaryResponse.class, ErrorResponse.class))
					.body();

			var result = response.get();

			if (result.isSuccess()) {
				return Optional.of(result.getSuccessValue());
			}

			if (result.getStatusCode() == 404) {
				return Optional.empty();
			}

			throw result.getErrorValue().toException();
		} catch (InterruptedException | IOException e) {
			throw new TranslationProviderException(e.getMessage(), e);
		}
	}

	@Override
	public void removeGlossary(String id) {

		URI uri = this.buildUri("/glossaries/" + id);

		HttpRequest request = HttpRequest.newBuilder(uri)
				.header("Authorization", "DeepL-Auth-Key " + this.getProviderConfiguration().getAuthKey())
				.header("Accept", "application/json")
				.DELETE()
				.build();

		HttpClient client = this.createHttpClient();

		try {
			var response = client
					.send(request, new JsonBodyHandler<>(String.class, ErrorResponse.class))
					.body();

			var result = response.get();

			if (!result.isSuccess()) {
				throw result.getErrorValue().toException();
			}

		} catch (InterruptedException | IOException e) {
			throw new TranslationProviderException(e.getMessage(), e);
		}
	}

	@Override
	public String recreate(Glossary glossary) {

		List<GlossaryResponse> glossaries = this.getGlossaries(
				glossary.getSourceLanguage(),
				glossary.getTargetLanguage());


		for (GlossaryResponse res : glossaries) {
			this.removeGlossary(res.glossaryId);
		}

		CreateGlossaryRequest req = CreateGlossaryRequest.build(glossary);

		URI uri = this.buildUri("/glossaries");

		HttpRequest request = HttpRequest.newBuilder(uri)
				.header("Authorization", "DeepL-Auth-Key " + this.getProviderConfiguration().getAuthKey())
				.header("Accept", "application/json")
				.header("Content-Type", "application/json")
				.POST(this.buildBody(req))
				.build();

		HttpClient client = this.createHttpClient();

		try {
			var response = client
					.send(request, new JsonBodyHandler<>(GlossaryResponse.class, ErrorResponse.class))
					.body();

			var result = response.get();

			if (!result.isSuccess()) {
				throw result.getErrorValue().toException();
			}

			return result.getSuccessValue().glossaryId;

		} catch (InterruptedException | IOException e) {
			throw new TranslationProviderException(e.getMessage(), e);
		}
	}

	private HttpRequest.BodyPublisher buildBody(Object o) {

		try {
			ObjectMapper objectMapper = new ObjectMapper();

			return HttpRequest.BodyPublishers.ofString(
				objectMapper.writeValueAsString(o)
			);
		} catch (JsonProcessingException e) {
			throw new TranslationProviderException(e.getMessage(), e);
		}
	}
}
