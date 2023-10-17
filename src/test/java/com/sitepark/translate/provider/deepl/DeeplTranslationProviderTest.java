package com.sitepark.translate.provider.deepl;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sitepark.translate.Format;
import com.sitepark.translate.Glossary;
import com.sitepark.translate.GlossaryEntry;
import com.sitepark.translate.SupportedLanguages;
import com.sitepark.translate.SupportedProvider;
import com.sitepark.translate.TranslationConfiguration;
import com.sitepark.translate.TranslationLanguage;
import com.sitepark.translate.TranslationParameter;
import com.sitepark.translate.TranslationRequest;
import com.sitepark.translate.TranslationResult;

import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

@SuppressWarnings({
	"PMD.AvoidDuplicateLiterals",
	"PMD.JUnitTestContainsTooManyAsserts"
})
class DeeplTranslationProviderTest {

	private MockWebServer mockWebServer;
	private DeeplTranslationProvider provider;

	private static final Dispatcher DISPATCHER = new DeeplTranslationProviderTestDispatcher();

	@BeforeEach
	void setUp() throws URISyntaxException {
		this.mockWebServer = new MockWebServer();
		this.mockWebServer.setDispatcher(DISPATCHER);
		String baseUrl = this.mockWebServer.url("").toString();
		if (baseUrl.endsWith("/")) {
			baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
		}
		DeeplTranslationProviderConfiguration providerConfig = DeeplTranslationProviderConfiguration.builder()
				.url(baseUrl)
				.authKey("test")
				.build();

		TranslationConfiguration config = TranslationConfiguration.builder()
				.translationProviderConfiguration(providerConfig)
				.build();

		this.provider = new DeeplTranslationProvider(config);
	}

	@AfterEach
	void tearDown() throws IOException {
		this.mockWebServer.shutdown();
	}

	@Test
	void testTranslation() {

		TranslationLanguage language = TranslationLanguage.builder()
				.source("de")
				.target("en")
				.build();

		TranslationRequest req = TranslationRequest.builder()
				.parameter(TranslationParameter.builder()
						.format(Format.TEXT)
						.language(language)
						.providerType(SupportedProvider.DEEPL)
						.build()
				)
				.sourceText("Hallo", "Welt")
				.build();

		TranslationResult translated = this.provider.translate(req);

		assertArrayEquals(
				new String[] {"Hello", "World"},
				translated.getText(),
				"unexpected translation");
	}

	@Test
	void testGetSupportedLanguages() {

		SupportedLanguages supportedLanguages = this.provider.getSupportedLanguages();

		assertEquals(
				"de",
				supportedLanguages.getSourceLanguage("de").get().getCode(),
				"unexpected source language");
		assertEquals(
				"en-us",
				supportedLanguages.getTargetLanguage("de", "en-us").get().getCode(),
				"unexpected source language");
	}

	@Test
	void testGetGlossaryId() {
		TranslationLanguage language = TranslationLanguage.builder()
				.source("de")
				.target("en")
				.build();
		String id = this.provider.getGlossaryId(language).get();
		assertEquals("ee0c28af-e9cd-4b59-9199-f114ebc0d602", id, "unexpected glossary id");
	}

	@Test
	void testGetGlossary() {

		Glossary glossary = this.provider.getGlossary("ee0c28af-e9cd-4b59-9199-f114ebc0d602").get();

		Glossary expected = Glossary.builder()
				.language(TranslationLanguage.builder()
						.source("de")
						.target("en")
						.build()
				)
				.entry(GlossaryEntry.builder()
						.source("Foo")
						.target("Bar")
						.build()
				)
				.entry(GlossaryEntry.builder()
						.source("Hallo")
						.target("Hey")
						.build()
				)
				.build();

		assertEquals(expected, glossary, "unexpected glossary");

	}

	@Test
	void testRemoveGlossary() throws Exception {
		this.provider.removeGlossary("ee0c28af-e9cd-4b59-9199-f114ebc0d602");
		RecordedRequest req = mockWebServer.takeRequest();
		assertEquals("/glossaries/ee0c28af-e9cd-4b59-9199-f114ebc0d602", req.getPath(), "unexpected path");
		assertEquals("DELETE", req.getMethod(), "unexpected method");
	}

	@Test
	void testRecreateGlossary() throws Exception {
		Glossary glossary = Glossary.builder()
				.language(TranslationLanguage.builder()
						.source("de")
						.target("en")
						.build()
				)
				.entry(GlossaryEntry.builder()
						.source("Foo")
						.target("Bar")
						.build()
				)
				.entry(GlossaryEntry.builder()
						.source("Hallo")
						.target("Hey")
						.build()
				)
				.build();

		String id = this.provider.recreate(glossary);
		assertEquals("ee0c28af-e9cd-4b59-9199-f114ebc0d602", id, "unexpected glossary id");

		RecordedRequest lastRequest = null;
		for (int i = 0; i < this.mockWebServer.getRequestCount(); i++) {
			lastRequest = this.mockWebServer.takeRequest();
		}

		String expected = "{\"name\":\"de - en\"," +
				"\"entries\":\"Foo\\tBar\\nHallo\\tHey\\n\"," +
				"\"source_lang\":\"de\"," +
				"\"target_lang\":\"en\"," +
				"\"entries_format\":\"tsv\"}";

		assertEquals("POST", lastRequest.getMethod(), "unexpected request method");
		assertEquals(expected, lastRequest.getBody().readUtf8(), "unexpected request body");
	}
}
