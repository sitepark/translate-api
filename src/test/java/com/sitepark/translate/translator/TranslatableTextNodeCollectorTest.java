package com.sitepark.translate.translator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

@SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
class TranslatableTextNodeCollectorTest {

  private static final String YAML =
      "format:\n"
          + "  file_size:\n"
          + "    unit:\n"
          + "      ZB: zebi\n"
          + "  date: dd.MM.yyyy\n"
          + "map:\n"
          + "  label: Karte\n"
          + "list:\n"
          + "  label: Liste\n"
          + "tags:\n"
          + "  - keep\n"
          + "  - drop\n";

  private Set<String> collectSources(Set<String> excludeKeys) throws IOException {
    JsonNode node = new YAMLMapper().readTree(YAML);
    TranslatableTextNodeCollector collector = new TranslatableTextNodeCollector("msg.de");
    if (excludeKeys != null) {
      collector.excludes(TranslatableTextNodeCollectorExcludes.of(excludeKeys));
    }
    List<TranslatableTextNode> nodes = collector.collect(node);
    return nodes.stream().map(TranslatableTextNode::getSourceText).collect(Collectors.toSet());
  }

  @Test
  void testNoExcludesCollectsEverything() throws Exception {
    Set<String> sources = this.collectSources(null);
    assertEquals(6, sources.size(), "all leaves collected");
    assertTrue(sources.contains("zebi"), "ZB value present");
  }

  @Test
  void testExcludeByFullNestedPath() throws Exception {
    Set<String> sources = this.collectSources(Set.of("msg.de.format.file_size.unit.ZB"));
    assertFalse(sources.contains("zebi"), "nested leaf excluded by full path");
    assertEquals(5, sources.size(), "only one leaf removed");
  }

  @Test
  void testExcludeByLeafOnlyKeyStillWorks() throws Exception {
    // backward compatibility: the old leaf-only key must keep matching
    Set<String> sources = this.collectSources(Set.of("msg.de.ZB"));
    assertFalse(sources.contains("zebi"), "nested leaf excluded by leaf-only key");
  }

  @Test
  void testFullPathIsPrecise() throws Exception {
    // "label" appears twice (map.label, list.label); the full path targets only one
    Set<String> sources = this.collectSources(Set.of("msg.de.map.label"));
    assertFalse(sources.contains("Karte"), "map.label excluded");
    assertTrue(sources.contains("Liste"), "list.label must remain");
  }

  @Test
  void testLeafOnlyKeyMatchesAllSameNamedLeaves() throws Exception {
    // preserved legacy behavior: a leaf-only key still matches every same-named leaf
    Set<String> sources = this.collectSources(Set.of("msg.de.label"));
    assertFalse(sources.contains("Karte"), "map.label excluded by leaf-only key");
    assertFalse(sources.contains("Liste"), "list.label excluded by leaf-only key");
  }

  @Test
  void testDepthOneKeyUnaffected() throws Exception {
    // format.date is depth 2 here, but a top-level-style key resolves the same either way;
    // verify the full path form works for the date pattern
    Set<String> sources = this.collectSources(Set.of("msg.de.format.date"));
    assertFalse(sources.contains("dd.MM.yyyy"), "format.date excluded by full path");
  }

  @Test
  void testExcludeArrayElementByFullPath() throws Exception {
    Set<String> sources = this.collectSources(Set.of("msg.de.tags.1"));
    assertFalse(sources.contains("drop"), "array element excluded by indexed full path");
    assertTrue(sources.contains("keep"), "other array element remains");
  }
}
