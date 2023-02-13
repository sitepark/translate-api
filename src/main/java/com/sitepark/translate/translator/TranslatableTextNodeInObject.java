package com.sitepark.translate.translator;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

class TranslatableTextNodeInObject extends TranslatableTextNode {

	private final ObjectNode object;

	private final String key;

	public TranslatableTextNodeInObject(ObjectNode object, String key, TextNode node) {
		super(node);
		this.object = object;
		this.key = key;
	}

	@Override
	public void setTargetText(String text) {
		this.object.put(this.key, text);
		super.setTargetText(text);
	}
}