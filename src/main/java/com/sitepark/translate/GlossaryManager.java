package com.sitepark.translate;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Optional;

public class GlossaryManager {

  private final TranslationProvider translationProvider;

  @SuppressFBWarnings("EI_EXPOSE_REP2")
  public GlossaryManager(TranslationProvider translationProvider) {
    this.translationProvider = translationProvider;
  }

  public Optional<Glossary> getGlossary(String id) {
    return translationProvider.getGlossary(id);
  }

  public Optional<String> getGlossaryId(String name) {
    return translationProvider.getGlossaryId(name);
  }

  public String recreate(Glossary glossary) {
    return translationProvider.recreate(glossary);
  }

  public void remove(String id) {
    translationProvider.removeGlossary(id);
  }
}
