package com.sitepark.translate.translator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class UnifiedSourceText {

	private final String[] sourceText;

	private String[] unifiedText;

	@SuppressWarnings("PMD.UseConcurrentHashMap")
	private final Map<String, List<Integer>> index = new LinkedHashMap<>();

	public UnifiedSourceText(String... sourceText) {
		this.sourceText = Arrays.copyOf(sourceText, sourceText.length);
		this.unify();
	}

	private void unify() {
		for (int i = 0; i < this.sourceText.length; i++) {
			this.unify(i, this.sourceText[i]);
		}
		this.unifiedText = this.index.keySet().stream().toArray(String[]::new);
	}

	private void unify(int i, String text) {
		List<Integer> list = this.index.get(text);
		if (list == null) {
			list = new ArrayList<>();
			this.index.put(text, list);
		}
		list.add(i);
	}

	public String[] getSourceText() {
		return Arrays.copyOf(this.unifiedText, this.unifiedText.length);
	}

	public String[] expandTranslation(String... unifiedTranslation) {
		String[] translation = new String[this.sourceText.length];
		for (int i = 0; i < unifiedTranslation.length; i++) {
			String text = unifiedTranslation[i];

			List<Integer> indexList = this.index.get(this.sourceText[i]);
			for (Integer index : indexList) {
				translation[index] = text;
			}
		}
		return translation;
	}
}