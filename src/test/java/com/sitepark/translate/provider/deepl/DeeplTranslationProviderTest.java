package com.sitepark.translate.provider.deepl;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

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
import com.sitepark.translate.TranslationEvent;
import com.sitepark.translate.TranslationLanguage;
import com.sitepark.translate.TranslationListener;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
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
	private TranslationListenerDummy listener;

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
		this.listener = new TranslationListenerDummy();

		TranslationConfiguration config = TranslationConfiguration.builder()
				.translationProviderConfiguration(providerConfig)
				.translationListener(this.listener)
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
				.providerType(SupportedProvider.DEEPL)
				.source("de")
				.target("en")
				.build();

		String[] translated = this.provider.translate(Format.TEXT, language, new String[] {"Hallo", "Welt"});

		assertArrayEquals(
				new String[] {"Hello", "World"},
				translated,
				"unexpected translation");
	}

	@Test
	@SuppressFBWarnings("UWF_FIELD_NOT_INITIALIZED_IN_CONSTRUCTOR")
	void testTranslationListener() {


		TranslationLanguage language = TranslationLanguage.builder()
				.providerType(SupportedProvider.DEEPL)
				.source("de")
				.target("en")
				.build();

		this.provider.translate(Format.TEXT, language, new String[] {"Hallo", "Welt"});

		assertSame(
				language,
				listener.event.getTranslationLanguage(),
				"unexpected language");
		assertEquals(2, listener.event.getChunks(), "unexpected chunks");
		assertEquals(9, listener.event.getSourceBytes(), "unexpected sourceBytes");
		assertEquals(10, listener.event.getTargetBytes(), "unexpected targetBytes");
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
		String id = this.provider.getGlossaryId("de", "en").get();
		assertEquals("ee0c28af-e9cd-4b59-9199-f114ebc0d602", id, "unexpected glossary id");
	}

	@Test
	void testGetGlossary() {

		Glossary glossary = this.provider.getGlossary("ee0c28af-e9cd-4b59-9199-f114ebc0d602").get();

		Glossary expected = Glossary.builder()
				.sourceLanguage("de")
				.targetLanguage("en")
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
				.sourceLanguage("de")
				.targetLanguage("en")
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


	private static final class TranslationListenerDummy implements TranslationListener {
		public TranslationEvent event;
		@Override
		public void translated(TranslationEvent event) {
			this.event = event;
		}
	}
}
