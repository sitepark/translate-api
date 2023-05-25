package com.sitepark.translate.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MainTest {

	private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
	private static final PrintStream ORIGINAL_OUT = System.out;
	private static final PrintStream ORIGINAL_ERR = System.err;

	@BeforeEach
	public void setUpStreams() {
		System.setOut(new PrintStream(outContent, true, StandardCharsets.UTF_8));
		System.setErr(new PrintStream(errContent, true, StandardCharsets.UTF_8));
	}

	@AfterEach
	public void restoreStreams() {
		System.setOut(ORIGINAL_OUT);
		System.setErr(ORIGINAL_ERR);
	}

	@Test
	void testMissingCommand() {
		Main.main();
		assertEquals(
				"missing command" + System.lineSeparator(),
				this.outContent.toString(StandardCharsets.UTF_8),
				"unexprected message");
	}

	@Test
	void testUnknwonCommand() {
		Main.main("test");
		assertEquals(
				"unknown command: test" + System.lineSeparator(),
				this.errContent.toString(StandardCharsets.UTF_8),
				"unexprected message");
	}
}
