package com.sitepark.translate.provider.deepl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sitepark.translate.TranslationProviderException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.HttpURLConnection;
import java.net.http.HttpResponse.BodyHandler;
import java.net.http.HttpResponse.BodySubscriber;
import java.net.http.HttpResponse.BodySubscribers;
import java.net.http.HttpResponse.ResponseInfo;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;
import java.util.function.Supplier;

public class JsonBodyHandler<T, U> implements BodyHandler<Supplier<JsonResult<T, U>>> {

  private final Class<T> successType;

  private final Class<U> errorType;

  public JsonBodyHandler(Class<T> successType, Class<U> errorType) {
    this.successType = successType;
    this.errorType = errorType;
  }

  @Override
  public BodySubscriber<Supplier<JsonResult<T, U>>> apply(ResponseInfo responseInfo) {

    Function<InputStream, Supplier<JsonResult<T, U>>> mapper;

    if (responseInfo.statusCode() == HttpURLConnection.HTTP_OK
        || responseInfo.statusCode() == HttpURLConnection.HTTP_CREATED) {
      mapper = this.parseSuccessResult(responseInfo.statusCode());
    } else if (responseInfo.statusCode() == HttpURLConnection.HTTP_NO_CONTENT) {
      mapper = this.successNoContent(responseInfo.statusCode());
    } else {
      mapper = this.parseErrorResult(responseInfo.statusCode());
    }
    return BodySubscribers.mapping(BodySubscribers.ofInputStream(), mapper);
  }

  private Function<InputStream, Supplier<JsonResult<T, U>>> parseSuccessResult(int statusCode) {
    return (input) -> {
      return () -> {
        return JsonResult.success(statusCode, parseResult(input, successType));
      };
    };
  }

  private Function<InputStream, Supplier<JsonResult<T, U>>> parseErrorResult(int statusCode) {
    return (input) -> {
      return () -> {
        return JsonResult.error(statusCode, parseResult(input, errorType));
      };
    };
  }

  private Function<InputStream, Supplier<JsonResult<T, U>>> successNoContent(int statusCode) {
    return (input) -> {
      return () -> {
        try {
          return JsonResult.success(statusCode, this.successType.getConstructor().newInstance());
        } catch (Exception e) {
          throw new TranslationProviderException(e.getMessage(), e);
        }
      };
    };
  }

  private <R> R parseResult(InputStream input, Class<R> type) {
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());

    if (type == String.class) {
      try {
        String text = new String(input.readAllBytes(), StandardCharsets.UTF_8);
        return type.cast(text);
      } catch (IOException e) {
        throw new UncheckedIOException(e.getMessage(), e);
      }
    }

    try {
      return mapper.readValue(input, type);
    } catch (IOException ex) {
      throw new UncheckedIOException(ex);
    }
  }
}
