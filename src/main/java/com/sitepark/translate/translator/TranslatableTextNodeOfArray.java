package com.sitepark.translate.translator;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.TextNode;

class TranslatableTextNodeOfArray extends TranslatableTextNode {

	private final ArrayNode array;

	private final int index;

	public TranslatableTextNodeOfArray(ArrayNode array, int index, TextNode node) {
		super(node);
		this.array = array;
		this.index = index;
	}

	@Override
	public void setTargetText(String targetText) {
		this.array.set(this.index, targetText);
		super.setTargetText(targetText);
	}
}