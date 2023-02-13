package com.sitepark.translate.translator;

public class TranslatableText {

	private final String sourceText;

	private String targetText;

	public TranslatableText(String sourceText) {
		this.sourceText = sourceText;
	}

	public void setTargetText(String targetText) {
		this.targetText = targetText;
	}

	public String getSourceText() {
		return this.sourceText;
	}

	public String getTargetText() {
		return this.targetText;
	}
}