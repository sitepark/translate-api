package com.sitepark.translate.translator;

import com.sitepark.translate.Format;
import com.sitepark.translate.TranslationCache;
import com.sitepark.translate.TranslationParameter;
import com.sitepark.translate.TranslationRequest;
import com.sitepark.translate.TranslationResult;
import com.sitepark.translate.TranslationResultStatistic;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public final class TranslatableTextListTranslator extends Translator {

  private TranslatableTextListTranslator(Builder builder) {
    super(builder);
  }

  public TranslationResultStatistic translate(
      TranslationParameter parameter, List<? extends TranslatableText> translatableTextList) {

    List<? extends TranslatableText> untranslated =
        this.translateWithCacheIfPossible(translatableTextList);

    if (untranslated.isEmpty()) {
      return TranslationResultStatistic.EMPTY;
    }

    TranslationResultStatistic htmlStatistic = this.translate(Format.HTML, parameter, untranslated);

    TranslationResultStatistic textStatistic = this.translate(Format.TEXT, parameter, untranslated);

    if (this.getTranslationCache() != null) {
      this.getTranslationCache().update(translatableTextList);
    }

    return htmlStatistic.add(textStatistic);
  }

  private TranslationResultStatistic translate(
      Format format,
      TranslationParameter parameter,
      List<? extends TranslatableText> translatableTextList) {

    List<? extends TranslatableText> formatFiltered =
        translatableTextList.stream()
            .filter(text -> text.getFormat() == format)
            .collect(Collectors.toList());

    if (formatFiltered.isEmpty()) {
      return TranslationResultStatistic.EMPTY;
    }

    String[] source =
        formatFiltered.stream()
            .map(
                node -> {
                  String text = node.getSourceText();
                  return text;
                })
            .toArray(String[]::new);

    TranslationParameter parameterWithFormat = parameter.toBuilder().format(format).build();

    TranslationRequest req =
        TranslationRequest.builder().parameter(parameterWithFormat).sourceText(source).build();

    TranslationResult result = super.translate(req);
    for (int i = 0; i < result.getText().length; i++) {
      TranslatableText node = formatFiltered.get(i);
      String text = result.getText()[i];
      node.setTargetText(text);
    }

    return result.getStatistic();
  }

  private TranslationCache getTranslationCache() {
    return this.getTranslatorConfiguration().getTranslationCache().orElse(null);
  }

  /**
   * @return all untranslated texts
   */
  private List<? extends TranslatableText> translateWithCacheIfPossible(
      List<? extends TranslatableText> translatableTextList) {

    if (this.getTranslationCache() == null) {
      return translatableTextList;
    }

    List<TranslatableText> untranslated = new ArrayList<>();

    for (TranslatableText text : translatableTextList) {
      Optional<String> translatedText = this.getTranslationCache().translate(text.getSourceText());
      if (translatedText.isPresent()) {
        text.setTargetText(translatedText.get());
      } else {
        untranslated.add(text);
      }
    }

    return untranslated;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder extends Translator.Builder<Builder> {

    protected Builder() {}

    protected Builder(TranslatableTextListTranslator updatableListTranslator) {
      super(updatableListTranslator);
    }

    @Override
    protected Builder self() {
      return this;
    }

    @Override
    public TranslatableTextListTranslator build() {
      return new TranslatableTextListTranslator(this);
    }
  }
}
