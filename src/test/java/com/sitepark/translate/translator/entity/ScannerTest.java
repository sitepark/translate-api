package com.sitepark.translate.translator.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@SuppressWarnings({
  "PMD.JUnitTestContainsTooManyAsserts",
  "PMD.JUnitAssertionsShouldIncludeMessage",
})
class ScannerTest {

  @Test
  void testText() {
    Scanner scanner = new Scanner("abc");
    List<Token> token = scanner.scanTokens();

    Token t1 = token.get(0);
    Token t2 = token.get(1);

    assertEquals(TokenType.STRING, t1.type);
    assertEquals("abc", t1.lexeme);
    assertEquals(TokenType.EOF, t2.type);
  }

  @Test
  void testStartEntity() {
    Scanner scanner = new Scanner("${{");
    List<Token> token = scanner.scanTokens();

    Token t1 = token.get(0);
    Token t2 = token.get(1);
    Token t3 = token.get(2);

    assertEquals(TokenType.STRING, t1.type);
    assertEquals("${", t1.lexeme);
    assertEquals(TokenType.STRING, t2.type);
    assertEquals("{", t2.lexeme);
    assertEquals(TokenType.EOF, t3.type);
  }

  @Test
  void testEndEntity() {
    Scanner scanner = new Scanner("}}");
    List<Token> token = scanner.scanTokens();

    Token t1 = token.get(0);
    Token t2 = token.get(1);
    assertEquals(TokenType.STRING, t1.type);
    assertEquals("}}", t1.lexeme);
    assertEquals(TokenType.EOF, t2.type);
  }

  @Test
  void testString() {
    Scanner scanner = new Scanner("Ein String");
    List<Token> token = scanner.scanTokens();

    Token t1 = token.get(0);
    Token t2 = token.get(1);
    assertEquals(TokenType.STRING, t1.type);
    assertEquals("Ein String", t1.lexeme);
    assertEquals(TokenType.EOF, t2.type);
  }

  @Test
  @Disabled
  void testStringWithEntitiesWithAmp() {
    Scanner scanner = new Scanner("Ein String &amp; ${a} {0}");
    List<Token> token = scanner.scanTokens();

    Token t1 = token.get(0);
    Token t2 = token.get(1);
    Token t3 = token.get(2);
    Token t4 = token.get(3);
    Token t5 = token.get(4);
    Token t6 = token.get(5);

    assertEquals(TokenType.STRING, t1.type);
    assertEquals("Ein String ", t1.lexeme);
    assertEquals(TokenType.ENTITY, t2.type);
    assertEquals("&amp;", t2.lexeme);
    assertEquals(TokenType.STRING, t3.type);
    assertEquals(" ", t3.lexeme);
    assertEquals(TokenType.ENTITY, t4.type);
    assertEquals("${a}", t4.lexeme);
    assertEquals(TokenType.STRING, t5.type);
    assertEquals(" ", t5.lexeme);
    assertEquals(TokenType.ENTITY, t6.type);
    assertEquals("{0}", t6.lexeme);
  }

  @Test
  void testStringWithEntities() {
    Scanner scanner = new Scanner("Ein String &amp; ${a} {0}");
    List<Token> token = scanner.scanTokens();

    Token t1 = token.get(0);
    Token t2 = token.get(1);
    Token t3 = token.get(2);
    Token t4 = token.get(3);

    assertEquals(TokenType.STRING, t1.type);
    assertEquals("Ein String &amp; ", t1.lexeme);
    assertEquals(TokenType.ENTITY, t2.type);
    assertEquals("${a}", t2.lexeme);
    assertEquals(TokenType.STRING, t3.type);
    assertEquals(" ", t3.lexeme);
    assertEquals(TokenType.ENTITY, t4.type);
    assertEquals("{0}", t4.lexeme);
  }
}
