package com.sitepark.translate;

import java.util.HashSet;
import java.util.Set;

public final class GlossaryDiffer {

	private final Glossary a;

	private final Glossary b;

	public GlossaryDiffer(Glossary a, Glossary b) {
		this.a = a;
		this.b = b;
	}

	public GlossaryChangeSet diff() {

		GlossaryChangeSet changeSet = new GlossaryChangeSet();

		Set<GlossaryEntry> entrySetOfB = new HashSet<>(this.b.getEntryList());
		for (GlossaryEntry entryOfA : this.a.getEntryList()) {
			if (!entrySetOfB.contains(entryOfA)) {
				changeSet.deleted(entryOfA);
			}
		}

		Set<GlossaryEntry> entrySetOfA = new HashSet<>(this.a.getEntryList());
		for (GlossaryEntry entryOfB : this.b.getEntryList()) {
			if (!entrySetOfA.contains(entryOfB)) {
				changeSet.added(entryOfB);
			}
		}

		return changeSet;
	}
}
