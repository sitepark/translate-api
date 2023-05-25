package com.sitepark.translate.provider.deepl;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.sitepark.translate.Format;
import com.sitepark.translate.SupportedLanguages;
import com.sitepark.translate.SupportedProvider;
import com.sitepark.translate.TranslationConfiguration;
import com.sitepark.translate.TranslationLanguage;
import com.sitepark.translate.TranslationProvider;
import com.sitepark.translate.translator.TranslatableText;
import com.sitepark.translate.translator.TranslatableTextListTranslator;


@SuppressWarnings("PMD")
@Tag("IntegrationTest")
@Disabled
class DeeplTranslationProviderIntegrationTest {

	private TranslationProvider createProvider() throws IOException, URISyntaxException {
		return new DeeplTranslationProvider(this.createConfiguration());
	}

	private TranslationConfiguration createConfiguration() throws IOException, URISyntaxException {

		DeeplTestConnection con = DeeplTestConnection.get();

		DeeplTranslationProviderConfiguration.Builder builder =
				DeeplTranslationProviderConfiguration.builder()
				.url(con.getUrl())
				.authKey(con.getAuthKey());

		return TranslationConfiguration.builder()
				.translationProviderConfiguration(builder.build())
				.encodePlaceholder(true)
				.build();
	}

	@Test
	void testSupportedLanguages() throws URISyntaxException, IOException, InterruptedException {

		TranslationProvider translator = this.createProvider();

		SupportedLanguages supportedLanguages = translator.getSupportedLanguages();

		assertTrue(supportedLanguages.getAll().size() > 0, "supportedLanguages should not be empty");
	}

	@Test
	void testTranslate() throws URISyntaxException, IOException, InterruptedException {

		TranslationProvider translator = this.createProvider();

		TranslationLanguage translationLanguage = TranslationLanguage.builder()
				.providerType(SupportedProvider.DEEPL)
				.source("de")
				.target("en")
				.build();

		String[] res = translator.translate(translationLanguage, new String[] {
				"Hallo", "Welt"
		});

		assertArrayEquals(new String[] {"Hello", "World"}, res, "Unexpected translation");
	}

	@Test
	void testTranslateHtml() throws URISyntaxException, IOException, InterruptedException {

		TranslationConfiguration translatorConfiguration = this.createConfiguration();

		List<TranslatableText> translatableTextList = new ArrayList<>();
		translatableTextList.add(new TranslatableText("Hallo Welt & <test>  \"Universum\""));
		translatableTextList.add(new TranslatableText("Hallo Welt &amp; &lt;test&gt; \"Universum\"", Format.HTML));

		TranslationLanguage language = TranslationLanguage.builder()
				.providerType(SupportedProvider.DEEPL)
				.source("de")
				.target("en")
				.build();

		TranslatableTextListTranslator translator = TranslatableTextListTranslator.builder()
				.translatorConfiguration(translatorConfiguration)
				.build();

		translator.translate(language, translatableTextList);

		translator.translate(language, translatableTextList);

		System.out.println(translatableTextList.get(0).getTargetText());
		System.out.println(translatableTextList.get(1).getTargetText());

		//assertArrayEquals(new String[] {"Hello", "World"}, res, "Unexpected translation");
	}

}
