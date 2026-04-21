package com.sitepark.translate.translator;

import static java.awt.SystemColor.text;

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
  public void setTarget(String lang, String text) {
    this.object.put(this.key, text);
    super.setTarget(lang, text);
  }
}
