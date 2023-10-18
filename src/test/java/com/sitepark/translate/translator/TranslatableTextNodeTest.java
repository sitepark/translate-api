package com.sitepark.translate.translator;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

class TranslatableTextNodeTest {

	@Test
	void testCreateWithNullParent() {
		TextNode textNode = Mockito.mock(TextNode.class);
		assertThrows(NullPointerException.class, () -> {
			TranslatableTextNode.create(null, "key", textNode);
		});
	}

	@Test
	void testCreateWithNullKey() {
		JsonNode jsonNode = Mockito.mock(JsonNode.class);
		TextNode textNode = Mockito.mock(TextNode.class);
		assertThrows(NullPointerException.class, () -> {
			TranslatableTextNode.create(jsonNode, null, textNode);
		});
	}

	@Test
	void testCreateWithNullNode() {
		JsonNode jsonNode = Mockito.mock(JsonNode.class);
		assertThrows(NullPointerException.class, () -> {
			TranslatableTextNode.create(jsonNode, "key", null);
		});
	}

	@Test
	void testWithObjectNodet() {
		JsonNode jsonNode = Mockito.mock(ObjectNode.class);
		TextNode textNode = Mockito.mock(TextNode.class);
		when(textNode.asText()).thenReturn("Text");
		TranslatableTextNode translatableTextNode =
				TranslatableTextNode.create(jsonNode, "key", textNode);

		assertThat(
				"unexpected instance",
				translatableTextNode,
				instanceOf(TranslatableTextNodeInObject.class));
	}

	@Test
	void testWithArrayNode() {
		JsonNode jsonNode = Mockito.mock(ArrayNode.class);
		TextNode textNode = Mockito.mock(TextNode.class);
		when(textNode.asText()).thenReturn("Text");
		TranslatableTextNode translatableTextNode =
				TranslatableTextNode.create(jsonNode, 1, textNode);

		assertThat(
				"unexpected instance",
				translatableTextNode,
				instanceOf(TranslatableTextNodeOfArray.class));
	}

	@Test
	void testWithInvalidNode() {
		JsonNode jsonNode = Mockito.mock(JsonNode.class);
		TextNode textNode = Mockito.mock(TextNode.class);
		assertThrows(IllegalArgumentException.class, () -> {
			TranslatableTextNode.create(jsonNode, 1, textNode);
		});
	}
}
