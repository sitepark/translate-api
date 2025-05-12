package com.sitepark.translate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class GlossaryChangeSet {

  private final List<GlossaryEntry> addedEntries = new ArrayList<>();

  private final List<GlossaryEntry> deletedEntries = new ArrayList<>();

  public void added(GlossaryEntry entry) {
    this.addedEntries.add(entry);
  }

  public void deleted(GlossaryEntry entry) {
    this.deletedEntries.add(entry);
  }

  public List<GlossaryEntry> getAddedEntries() {
    return Collections.unmodifiableList(this.addedEntries);
  }

  public List<GlossaryEntry> getDeletedEntries() {
    return Collections.unmodifiableList(this.deletedEntries);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.addedEntries, this.deletedEntries);
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof GlossaryChangeSet that)) {
      return false;
    }

    return Objects.equals(that.addedEntries, this.addedEntries)
        && Objects.equals(that.deletedEntries, this.deletedEntries);
  }
}
