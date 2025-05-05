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
    int hashCode = 0;
    hashCode += this.addedEntries.hashCode();
    hashCode += this.deletedEntries.hashCode();
    return hashCode;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof GlossaryChangeSet changeSet)) {
      return false;
    }

    return Objects.equals(changeSet.addedEntries, this.addedEntries)
        && Objects.equals(changeSet.deletedEntries, this.deletedEntries);
  }
}
