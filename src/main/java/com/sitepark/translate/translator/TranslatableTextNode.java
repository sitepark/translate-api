package com.sitepark.translate.translator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Objects;

public abstract class TranslatableTextNode extends TranslatableText {

  private final TextNode node;

  protected TranslatableTextNode(TextNode node) {
    super(node.asText());
    this.node = node;
  }

  @SuppressFBWarnings("EI_EXPOSE_REP")
  public TextNode getNode() {
    return this.node;
  }

  public static TranslatableTextNode create(JsonNode parent, Object key, TextNode node) {

    Objects.requireNonNull(parent, "parent is null");
    Objects.requireNonNull(key, "key is null");
    Objects.requireNonNull(node, "node is null");

    if (parent instanceof ObjectNode && key instanceof String) {
      return new TranslatableTextNodeInObject((ObjectNode) parent, (String) key, node);
    } else if (parent instanceof ArrayNode && key instanceof Integer) {
      return new TranslatableTextNodeOfArray((ArrayNode) parent, (Integer) key, node);
    } else {
      throw new IllegalArgumentException(
          "ObjectNode-Parent with String-Key or "
              + "ArrayNode-Parent with Integer-Key expected. "
              + "parent:"
              + parent.getClass().getName()
              + ", key:"
              + key.getClass().getName());
    }
  }
}
