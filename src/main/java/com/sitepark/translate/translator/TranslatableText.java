package com.sitepark.translate.translator;

import com.sitepark.translate.Format;

public class TranslatableText {

	private final String sourceText;

	private final Format format;

	private String targetText;

	public TranslatableText(String sourceText) {
		this.sourceText = sourceText;
		this.format = FormatDetector.detect(sourceText);
	}

	public TranslatableText(String sourceText, Format format) {
		this.sourceText = sourceText;
		this.format = format;
	}

	public void setTargetText(String targetText) {
		this.targetText = targetText;
	}

	public Format getFormat() {
		return this.format;
	}

	public String getSourceText() {
		return this.sourceText;
	}

	public String getTargetText() {
		return this.targetText;
	}
}