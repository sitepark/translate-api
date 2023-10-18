package com.sitepark.translate.provider.libretranslate;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.sitepark.translate.Format;
import com.sitepark.translate.SupportedLanguages;
import com.sitepark.translate.SupportedProvider;
import com.sitepark.translate.TranslationConfiguration;
import com.sitepark.translate.TranslationLanguage;
import com.sitepark.translate.TranslationParameter;
import com.sitepark.translate.TranslationProvider;
import com.sitepark.translate.TranslationRequest;
import com.sitepark.translate.TranslationResult;
import com.sitepark.translate.translator.TranslatableText;
import com.sitepark.translate.translator.TranslatableTextListTranslator;

@Disabled
@SuppressWarnings("PMD")
class LibreTranslateTranslationProviderIntegrationTest {

	private TranslationProvider createProvider() throws IOException, URISyntaxException {
		return new LibreTranslateTranslationProvider(this.createConfiguration());
	}

	private TranslationConfiguration createConfiguration() throws IOException, URISyntaxException {

		LibreTranslateTestConnection con = LibreTranslateTestConnection.get();

		LibreTranslateTranslationProviderConfiguration.Builder builder =
				LibreTranslateTranslationProviderConfiguration.builder()
						.url(con.getUrl());
		if (con.getApiKey() != null) {
			builder.apiKey(con.getApiKey());
		}

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
				.source("de")
				.target("en")
				.build();

		TranslationParameter parameter = TranslationParameter.builder()
				.format(Format.TEXT)
				.language(translationLanguage)
				.providerType(SupportedProvider.DEEPL)
				.build();

		TranslationRequest req = TranslationRequest.builder()
				.parameter(parameter)
				.sourceText("Hallo", "Welt")
				.build();

		TranslationResult result = translator.translate(req);

		assertArrayEquals(new String[] {"Hello", "World"}, result.getText(), "Unexpected translation");
	}

	@Test
	void testTranslateHtml() throws URISyntaxException, IOException, InterruptedException {

		TranslationConfiguration translatorConfiguration = this.createConfiguration();

		List<TranslatableText> translatableTextList = new ArrayList<>();
		translatableTextList.add(new TranslatableText("Hallo Welt & <test>  \"Universum\""));
		translatableTextList.add(new TranslatableText("Hallo Welt &amp; &lt;test&gt; \"Universum\"", Format.HTML));

		TranslationLanguage language = TranslationLanguage.builder()
				.source("de")
				.target("en")
				.build();

		TranslatableTextListTranslator translator = TranslatableTextListTranslator.builder()
				.translatorConfiguration(translatorConfiguration)
				.build();

		TranslationParameter parameter = TranslationParameter.builder()
				.language(language)
				.providerType(SupportedProvider.DEEPL)
				.build();

		translator.translate(parameter, translatableTextList);

		System.out.println(translatableTextList.get(0).getTargetText());
		System.out.println(translatableTextList.get(1).getTargetText());

		//assertArrayEquals(new String[] {"Hello", "World"}, res, "Unexpected translation");
	}
}
