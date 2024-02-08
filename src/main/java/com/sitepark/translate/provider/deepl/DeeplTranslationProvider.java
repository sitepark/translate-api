package com.sitepark.translate.provider.deepl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sitepark.translate.Format;
import com.sitepark.translate.Glossary;
import com.sitepark.translate.GlossaryEntry;
import com.sitepark.translate.Language;
import com.sitepark.translate.SupportedLanguages;
import com.sitepark.translate.TranslationConfiguration;
import com.sitepark.translate.TranslationLanguage;
import com.sitepark.translate.TranslationProvider;
import com.sitepark.translate.TranslationProviderException;
import com.sitepark.translate.TranslationRequest;
import com.sitepark.translate.TranslationResult;
import com.sitepark.translate.TranslationResultStatistic;
import com.sitepark.translate.translator.UnifiedSourceText;
import com.sitepark.translate.translator.entity.Decoder;
import com.sitepark.translate.translator.entity.Encoder;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
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

@SuppressFBWarnings
@SuppressWarnings("PMD")
public class DeeplTranslationProvider implements TranslationProvider {

  private final TranslationConfiguration translatorConfiguration;

  public DeeplTranslationProvider(TranslationConfiguration translatorConfiguration) {
    this.translatorConfiguration = translatorConfiguration;
  }

  public TranslationResult translate(TranslationRequest req) {

    String[] sourceTextToTranslate = req.getSourceText();
    if (req.getParameter().getFormat().isPresent()
        && req.getParameter().getFormat().get() == Format.HTML) {
      sourceTextToTranslate = this.encodePlacerholder(req.getSourceText());
    }

    UnifiedSourceText unifiedSourceText = new UnifiedSourceText(sourceTextToTranslate);

    TranslationRequest modifiedReq =
        req.toBuilder().sourceText(unifiedSourceText.getSourceText()).build();
    try {

      long start = System.currentTimeMillis();

      String[] translated = translationRequest(modifiedReq);

      String[] decodedTranslation = this.decodePlacerholder(translated);

      return TranslationResult.builder()
          .request(req)
          .text(unifiedSourceText.expandTranslation(decodedTranslation))
          .statistic(
              TranslationResultStatistic.builder()
                  .chunks(req.getSourceText().length)
                  .translationTime(System.currentTimeMillis() - start)
                  .sourceBytes(this.byteCount(unifiedSourceText.getSourceText()))
                  .targetBytes(this.byteCount(translated))
                  .build())
          .build();

    } catch (InterruptedException | IOException e) {
      throw new TranslationProviderException(e.getMessage(), e);
    }
  }

  protected String[] translationRequest(TranslationRequest req)
      throws IOException, InterruptedException {

    URI uri = this.buildUri("/translate");

    TranslationLanguage language = req.getParameter().getLanguage();

    List<String[]> params = new ArrayList<>();
    params.add(new String[] {"source_lang", language.getSource()});
    params.add(new String[] {"target_lang", language.getTarget()});
    if (req.getParameter().getFormat().isPresent()
        && req.getParameter().getFormat().get() == Format.HTML) {
      params.add(new String[] {"tag_handling", Format.HTML.toString().toLowerCase()});
    }
    for (String text : req.getSourceText()) {
      params.add(new String[] {"text", text});
    }
    req.getParameter()
        .getGlossaryId()
        .ifPresent(
            glossarId -> {
              params.add(new String[] {"glossary_id", glossarId});
            });

    HttpRequest request =
        HttpRequest.newBuilder(uri)
            .header(
                "Authorization", "DeepL-Auth-Key " + this.getProviderConfiguration().getAuthKey())
            .header("Accept", "application/json")
            .header("Content-Type", "application/x-www-form-urlencoded")
            .POST(this.buildBody(params))
            .build();

    HttpClient client = this.createHttpClient();

    var response =
        client
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
      Language language =
          Language.builder()
              .code(sourceLanguage.getLanguage().toLowerCase())
              .name(sourceLanguage.getName())
              .targets(
                  targetLanguageList.stream()
                      .map(l -> l.getLanguage().toLowerCase())
                      .collect(Collectors.toList()))
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

    HttpRequest request =
        HttpRequest.newBuilder(uri)
            .header(
                "Authorization", "DeepL-Auth-Key " + this.getProviderConfiguration().getAuthKey())
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

    return Optional.of(
        Glossary.builder()
            .name(response.get().name)
            .language(
                TranslationLanguage.builder()
                    .source(response.get().sourceLang)
                    .target(response.get().targetLang)
                    .build())
            .entryList(this.getGlossaryEntries(id))
            .build());
  }

  @Override
  public Optional<String> getGlossaryId(String name) {
    List<GlossaryResponse> glossaries = this.getGlossaries(name);
    return glossaries.stream().map(res -> res.glossaryId).findFirst();
  }

  private List<GlossaryResponse> getGlossaries(String name) {

    List<GlossaryResponse> glossaries = new ArrayList<>();

    for (GlossaryResponse glossary : this.getGlossaries()) {
      if (!glossary.name.equals(name)) {
        continue;
      }
      glossaries.add(glossary);
    }
    return glossaries;
  }

  private List<GlossaryResponse> getGlossaries() {

    URI uri = this.buildUri("/glossaries");

    HttpRequest request =
        HttpRequest.newBuilder(uri)
            .header(
                "Authorization", "DeepL-Auth-Key " + this.getProviderConfiguration().getAuthKey())
            .header("Accept", "application/json")
            .build();

    HttpClient client = this.createHttpClient();

    try {
      var response =
          client
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

    HttpRequest request =
        HttpRequest.newBuilder(uri)
            .header(
                "Authorization", "DeepL-Auth-Key " + this.getProviderConfiguration().getAuthKey())
            .header("Accept", "text/tab-separated-values")
            .build();
    HttpClient client = this.createHttpClient();

    try {
      var response =
          client.send(request, new JsonBodyHandler<>(String.class, ErrorResponse.class)).body();

      var result = response.get();

      if (!result.isSuccess()) {
        throw result.getErrorValue().toException();
      }

      List<GlossaryEntry> entries = new ArrayList<>();

      for (String line : result.getSuccessValue().split("\n")) {
        String[] values = line.split("\t");
        String source = values[0];
        String target = values[1];

        entries.add(GlossaryEntry.builder().source(source).target(target).build());
      }

      return entries;

    } catch (InterruptedException | IOException e) {
      throw new TranslationProviderException(e.getMessage(), e);
    }
  }

  private Optional<GlossaryResponse> getGlossaryResponse(String id) {

    URI uri = this.buildUri("/glossaries/" + id);

    HttpRequest request =
        HttpRequest.newBuilder(uri)
            .header(
                "Authorization", "DeepL-Auth-Key " + this.getProviderConfiguration().getAuthKey())
            .header("Accept", "application/json")
            .build();

    HttpClient client = this.createHttpClient();

    try {
      var response =
          client
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

    HttpRequest request =
        HttpRequest.newBuilder(uri)
            .header(
                "Authorization", "DeepL-Auth-Key " + this.getProviderConfiguration().getAuthKey())
            .header("Accept", "application/json")
            .DELETE()
            .build();

    HttpClient client = this.createHttpClient();

    try {
      var response =
          client.send(request, new JsonBodyHandler<>(String.class, ErrorResponse.class)).body();

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

    List<GlossaryResponse> glossaries = this.getGlossaries(glossary.getName());

    for (GlossaryResponse res : glossaries) {
      this.removeGlossary(res.glossaryId);
    }

    CreateGlossaryRequest req = CreateGlossaryRequest.build(glossary);

    URI uri = this.buildUri("/glossaries");

    HttpRequest request =
        HttpRequest.newBuilder(uri)
            .header(
                "Authorization", "DeepL-Auth-Key " + this.getProviderConfiguration().getAuthKey())
            .header("Accept", "application/json")
            .header("Content-Type", "application/json")
            .POST(this.buildBody(req))
            .build();

    HttpClient client = this.createHttpClient();

    try {
      var response =
          client
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

      return HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(o));
    } catch (JsonProcessingException e) {
      throw new TranslationProviderException(e.getMessage(), e);
    }
  }
}
