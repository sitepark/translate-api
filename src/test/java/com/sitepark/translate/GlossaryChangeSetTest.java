package com.sitepark.translate;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class GlossaryChangeSetTest {

  @Test
  @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
  public void testEqualsContract() {
    EqualsVerifier.forClass(GlossaryChangeSet.class).verify();
  }

  @Test
  public void testAdded() {

    GlossaryChangeSet changeSet = new GlossaryChangeSet();
    GlossaryEntry entry = GlossaryEntry.builder().source("Hallo").target("Hello").build();

    changeSet.added(entry);

    assertEquals(Arrays.asList(entry), changeSet.getAddedEntries(), "unexpected added list");
  }

  @Test
  public void testDeleted() {

    GlossaryChangeSet changeSet = new GlossaryChangeSet();
    GlossaryEntry entry = GlossaryEntry.builder().source("Hallo").target("Hello").build();

    changeSet.deleted(entry);

    assertEquals(Arrays.asList(entry), changeSet.getDeletedEntries(), "unexpected deleted list");
  }
}
