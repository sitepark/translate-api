package com.sitepark.translate.translator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public class TranslatableTextNodeCollector {

  private final String key;

  private TranslatableTextNodeCollectorExcludes excludes;

  public TranslatableTextNodeCollector() {
    this(null);
  }

  public TranslatableTextNodeCollector excludes(TranslatableTextNodeCollectorExcludes excludes) {
    this.excludes = excludes;
    return this;
  }

  public TranslatableTextNodeCollector(String key) {
    this.key = key;
  }

  public List<TranslatableTextNode> collect(List<JsonNode> jsonList) {
    List<TranslatableTextNode> translatableTextNodeList = new ArrayList<>();
    for (JsonNode json : jsonList) {
      this.filterTextNodes(
          null,
          null,
          json,
          (node) -> {
            translatableTextNodeList.add(node);
          });
    }
    return translatableTextNodeList;
  }

  public List<TranslatableTextNode> collect(JsonNode json) {
    List<TranslatableTextNode> translatableTextNodeList = new ArrayList<>();
    this.filterTextNodes(
        null,
        null,
        json,
        (node) -> {
          translatableTextNodeList.add(node);
        });
    return translatableTextNodeList;
  }

  private void filterTextNodes(
      JsonNode parent, Object nodeKey, JsonNode node, Consumer<TranslatableTextNode> consumer) {
    if (node instanceof ObjectNode) {
      node.fields()
          .forEachRemaining(e -> filterTextNodes(node, e.getKey(), e.getValue(), consumer));
    } else if (node instanceof ArrayNode) {
      ArrayNode arrayNode = (ArrayNode) node;
      Iterator<JsonNode> it = arrayNode.elements();
      for (int i = 0; it.hasNext(); i++) {
        this.filterTextNodes(node, i, it.next(), consumer);
      }
    } else if (node instanceof TextNode) {
      String absoluteKeys = this.getAbsoluteKey(nodeKey);
      if (this.excludes != null && this.excludes.contains(absoluteKeys)) {
        return;
      }
      TranslatableTextNode updatableTextNode =
          TranslatableTextNode.create(parent, nodeKey, (TextNode) node);
      consumer.accept(updatableTextNode);
    }
  }

  private String getAbsoluteKey(Object nodeKey) {
    if (nodeKey == null) {
      return this.key;
    }
    if (this.key == null) {
      return nodeKey.toString();
    }
    return this.key + "." + nodeKey;
  }
}
