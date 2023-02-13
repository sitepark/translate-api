package com.sitepark.translate.translator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

public class TranslatableTextNodeCollector {

	public List<TranslatableTextNode> collect(List<JsonNode> jsonList) {
		List<TranslatableTextNode> translatableTextNodeList = new ArrayList<>();
		for (JsonNode json : jsonList) {
			this.filterTextNodes(null, null, json, (node) -> {
				translatableTextNodeList.add(node);
			});
		}
		return translatableTextNodeList;
	}

	private void filterTextNodes(JsonNode parent, Object key, JsonNode node, Consumer<TranslatableTextNode> consumer) {
		if (node instanceof ObjectNode) {
			node.fields().forEachRemaining(e -> filterTextNodes(node, e.getKey(), e.getValue(), consumer));
		} else if (node instanceof ArrayNode) {
			ArrayNode arrayNode = (ArrayNode)node;
			Iterator<JsonNode> it = arrayNode.elements();
			for (int i = 0; it.hasNext(); i++) {
				this.filterTextNodes(node, i, it.next(), consumer);
			}
		} else if (node instanceof TextNode) {
			TranslatableTextNode updatableTextNode = TranslatableTextNode.create(parent, key, (TextNode)node);
			consumer.accept(updatableTextNode);
		}
	}
}
