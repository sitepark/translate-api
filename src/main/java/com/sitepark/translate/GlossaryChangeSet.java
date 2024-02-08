package com.sitepark.translate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class GlossaryChangeSet {

  private final List<GlossaryEntry> added = new ArrayList<>();

  private final List<GlossaryEntry> deleted = new ArrayList<>();

  public void added(GlossaryEntry entry) {
    this.added.add(entry);
  }

  public void deleted(GlossaryEntry entry) {
    this.deleted.add(entry);
  }

  public List<GlossaryEntry> getAdded() {
    return Collections.unmodifiableList(this.added);
  }

  public List<GlossaryEntry> getDeleted() {
    return Collections.unmodifiableList(this.deleted);
  }

  @Override
  public int hashCode() {
    int hashCode = 0;
    if (this.added != null) {
      hashCode += this.added.hashCode();
    }
    if (this.deleted != null) {
      hashCode += this.deleted.hashCode();
    }
    return hashCode;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof GlossaryChangeSet)) {
      return false;
    }

    GlossaryChangeSet changeSet = (GlossaryChangeSet) o;
    return Objects.equals(changeSet.added, this.added)
        && Objects.equals(changeSet.deleted, this.deleted);
  }
}
