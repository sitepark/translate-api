package com.sitepark.translate.provider.deepl;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.sitepark.translate.Format;
import com.sitepark.translate.Language;
import com.sitepark.translate.SupportedLanguages;
import com.sitepark.translate.TranslationConfiguration;
import com.sitepark.translate.TranslationEvent;
import com.sitepark.translate.TranslationLanguage;
import com.sitepark.translate.TranslationProvider;
import com.sitepark.translate.TranslationProviderException;
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

	public String[] translate(TranslationLanguage language, final String... sourceText) {

		String[] encodedSourceText = this.encodePlacerholder(sourceText);

		URI uri = this.buildUri("/translate");


		List<String[]> params = new ArrayList<>();
		params.add(new String[] {"source_lang", language.getSource()});
		params.add(new String[] {"target_lang", language.getTarget()});
		params.add(new String[] {"tag_handling", Format.HTML.toString().toLowerCase()});
		for (String text : encodedSourceText) {
			params.add(new String[] {"text", text});
		}

		HttpRequest request = HttpRequest.newBuilder(uri)
				.header("Authorization", "DeepL-Auth-Key " + this.getProviderConfiguration().getAuthKey())
				.header("Accept", "application/json")
				.header("Content-Type", "application/x-www-form-urlencoded")
				.POST(this.buildBody(params))
				.build();

		HttpClient client = this.createHttpClient();

		try {

			long start = System.currentTimeMillis();

			TransportResponse response = client
					.send(request, new JsonBodyHandler<>(TransportResponse.class))
					.body()
					.get();

			String[] translated = response.getTranslations();

			this.translatorConfiguration.getTranslationListener().ifPresent(listener -> {
				listener.translated(TranslationEvent.builder()
						.translationTime(System.currentTimeMillis() - start)
						.translationLanguage(language)
						.chunks(encodedSourceText.length)
						.sourceBytes(this.byteCount(encodedSourceText))
						.targetBytes(this.byteCount(translated))
						.build());
			});

			return this.decodePlacerholder(translated);

		} catch (InterruptedException | IOException e) {
			throw new TranslationProviderException(e.getMessage(), e);
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
		return this.getLanguages("source");
	}

	private List<TransportLanguage> getTargetLanguages() {
		return this.getLanguages("source");
	}

	private List<TransportLanguage> getLanguages(String type) {

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
}
