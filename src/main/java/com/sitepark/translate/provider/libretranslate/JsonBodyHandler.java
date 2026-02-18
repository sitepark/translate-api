package com.sitepark.translate.provider.libretranslate;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sitepark.translate.TranslationProviderException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.HttpURLConnection;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;

public class JsonBodyHandler<W> implements HttpResponse.BodyHandler<Supplier<W>> {

  private final Class<W> wClass;

  public JsonBodyHandler(Class<W> wClass) {
    this.wClass = wClass;
  }

  public static <W> HttpResponse.BodySubscriber<Supplier<W>> asJSON(Class<W> targetType) {
    HttpResponse.BodySubscriber<InputStream> upstream =
        HttpResponse.BodySubscribers.ofInputStream();

    return HttpResponse.BodySubscribers.mapping(
        upstream, inputStream -> toSupplierOfType(inputStream, targetType));
  }

  public static <W> Supplier<W> toSupplierOfType(InputStream inputStream, Class<W> targetType) {
    return () -> {
      String text = null;
      try (InputStream stream = inputStream) {
        text = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper.readValue(text, targetType);
      } catch (IOException e) {
        throw new UncheckedIOException(text, e);
      }
    };
  }

  @Override
  public HttpResponse.BodySubscriber<Supplier<W>> apply(HttpResponse.ResponseInfo responseInfo) {
    if (responseInfo.statusCode() != HttpURLConnection.HTTP_OK) {
      throw new TranslationProviderException("HTTP-Status: " + responseInfo.statusCode());
    }
    return asJSON(this.wClass);
  }
}
