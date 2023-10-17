package com.sitepark.translate.provider.deepl;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.sitepark.translate.Format;
import com.sitepark.translate.Glossary;
import com.sitepark.translate.GlossaryEntry;
import com.sitepark.translate.GlossaryManager;
import com.sitepark.translate.SupportedLanguages;
import com.sitepark.translate.SupportedProvider;
import com.sitepark.translate.TranslationConfiguration;
import com.sitepark.translate.TranslationLanguage;
import com.sitepark.translate.TranslationParameter;
import com.sitepark.translate.TranslationProvider;
import com.sitepark.translate.TranslationProviderFactory;
import com.sitepark.translate.TranslationRequest;
import com.sitepark.translate.TranslationResult;
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

	@Test
	void testTranslateHtmlBroken() throws URISyntaxException, IOException, InterruptedException {

		TranslationConfiguration translatorConfiguration = this.createConfiguration();

		List<TranslatableText> translatableTextList = new ArrayList<>();
		translatableTextList.add(new TranslatableText(
				"<div class=\"SP-LinkList SP-LinkList--compact SP-Util__sectionMarginSmall\">\n"
				+ "<ul class=\"SP-LinkList__list\">\n"
				+ "   <li class=\"SP-LinkList__item\">\n"
				+ "      <a data-variant=\"in-linklist\" href=\"https://www.kunstcaching.de/\" rel=\"noopener\" target=\"_blank\" data-entity-ref=\"1\">www.kunstcaching.de</a>\n"
				+ "   </li>\n"
				+ "   <li class=\"SP-LinkList__item\">\n"
				+ "      <a data-variant=\"in-linklist\" href=\"https://www.pablo-zibes.de\" rel=\"noopener\" target=\"_blank\" data-entity-ref=\"2\">Website von Pablo Zibes</a>\n"
				+ "   </li>\n"
				+ "</ul>\n"
				+ "</div>"));

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

		//assertArrayEquals(new String[] {"Hello", "World"}, res, "Unexpected translation");
	}

	@Test
	void testTranslateHtmlBrokenAmp() throws URISyntaxException, IOException, InterruptedException {

		TranslationConfiguration translatorConfiguration = this.createConfiguration();

		List<TranslatableText> translatableTextList = new ArrayList<>();
		translatableTextList.add(new TranslatableText("Einrichtungen & Beteiligungen"));

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

		//assertArrayEquals(new String[] {"Hello", "World"}, res, "Unexpected translation");
	}


	@Test
	void testGlossarManagement() throws URISyntaxException, IOException, InterruptedException {

		TranslationConfiguration translatorConfiguration = this.createConfiguration();
		TranslationProviderFactory factory = new TranslationProviderFactory(translatorConfiguration);

		TranslationProvider provider = factory.create(SupportedProvider.DEEPL);

		GlossaryManager glossarManager = new GlossaryManager(provider);

		TranslationLanguage language = TranslationLanguage.builder()
				.source("de")
				.target("en")
				.build();

		Optional<String> id = glossarManager.getGlossaryId(language);
		System.out.println("exists glossary: " + id.orElse("not found"));

		Glossary glossary = Glossary.builder()
				.language(TranslationLanguage.builder()
						.source("de")
						.target("en")
						.build()
				)
				.entry(GlossaryEntry.builder()
						.source("Hallo")
						.target("Hey")
						.build()
				)
				.entry(GlossaryEntry.builder()
						.source("Foo")
						.target("Bar")
						.build()
				)
				.build();

		String newId = glossarManager.recreate(glossary);

		System.out.println("new glossary: " + newId);

		Glossary loaded = glossarManager.getGlossary(newId).get();
		for (GlossaryEntry entry : loaded.getEntryList()) {
			System.out.println(entry.getSource() + ", " + entry.getTarget());
		}
	}
}
