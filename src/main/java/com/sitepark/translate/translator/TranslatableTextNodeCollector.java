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

  private TranslatableTextNodeCollectorExcludes excludesNodes;

  public TranslatableTextNodeCollector() {
    this(null);
  }

  public TranslatableTextNodeCollector(String key) {
    this.key = key;
  }

  public TranslatableTextNodeCollector excludes(TranslatableTextNodeCollectorExcludes excludes) {
    this.excludesNodes = excludes;
    return this;
  }

  public List<TranslatableTextNode> collect(List<JsonNode> jsonList) {
    List<TranslatableTextNode> translatableTextNodeList = new ArrayList<>();
    for (JsonNode json : jsonList) {
      this.filterTextNodes(null, null, null, json, translatableTextNodeList::add);
    }
    return translatableTextNodeList;
  }

  public List<TranslatableTextNode> collect(JsonNode json) {
    List<TranslatableTextNode> translatableTextNodeList = new ArrayList<>();
    this.filterTextNodes(null, null, null, json, translatableTextNodeList::add);
    return translatableTextNodeList;
  }

  private void filterTextNodes(
      JsonNode parent,
      Object nodeKey,
      String parentPath,
      JsonNode node,
      Consumer<TranslatableTextNode> consumer) {
    String path = this.appendPath(parentPath, nodeKey);
    if (node instanceof ObjectNode) {
      node.fields()
          .forEachRemaining(e -> filterTextNodes(node, e.getKey(), path, e.getValue(), consumer));
    } else if (node instanceof ArrayNode arrayNode) {
      Iterator<JsonNode> it = arrayNode.elements();
      for (int i = 0; it.hasNext(); i++) {
        this.filterTextNodes(node, i, path, it.next(), consumer);
      }
    } else if (node instanceof TextNode) {
      if (this.isExcluded(path, nodeKey)) {
        return;
      }
      TranslatableTextNode updatableTextNode =
          TranslatableTextNode.create(parent, nodeKey, (TextNode) node);
      consumer.accept(updatableTextNode);
    }
  }

  /**
   * A leaf is excluded if the excludes file contains its full nested-path key (e.g. {@code
   * <fileKey>.format.file_size.unit.ZB}) or, for backward compatibility, its leaf-only key (e.g.
   * {@code <fileKey>.ZB}).
   */
  private boolean isExcluded(String path, Object nodeKey) {
    if (this.excludesNodes == null) {
      return false;
    }
    String fullPathKey = this.withFileKey(path);
    String leafOnlyKey = this.withFileKey(nodeKey == null ? null : nodeKey.toString());
    return this.excludesNodes.contains(fullPathKey) || this.excludesNodes.contains(leafOnlyKey);
  }

  /** Appends a node key to the accumulated within-file path (null-safe). */
  private String appendPath(String parentPath, Object nodeKey) {
    if (nodeKey == null) {
      return parentPath;
    }
    if (parentPath == null || parentPath.isEmpty()) {
      return nodeKey.toString();
    }
    return parentPath + "." + nodeKey;
  }

  /** Prefixes a within-file path (or leaf key) with the file base key. */
  private String withFileKey(String suffix) {
    if (suffix == null) {
      return this.key;
    }
    if (this.key == null) {
      return suffix;
    }
    return this.key + "." + suffix;
  }
}
