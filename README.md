[![codecov](https://codecov.io/gh/sitepark/translate-api/branch/main/graph/badge.svg?token=68Mk5j1BY3)](https://codecov.io/gh/sitepark/translate-api)
[![Known Vulnerabilities](https://snyk.io/test/github/sitepark/translate-api/badge.svg)](https://snyk.io/test/github/sitepark/translate-api)
# Translate Java API

A Java API to translate texts with various translation services. The following services are supported

- [LibreTranslate](https://github.com/LibreTranslate/LibreTranslate)
- [Deepl](https://www.deepl.com/pro-api?cta=header-pro-api)

## Features
- [String translations](#translate-a-singe-stringhtml)
- [Html translations](#translate-a-singe-stringhtml)
- [String-Array translations](#translate-a-string-array)
- [JSON translations](#translate-a-json-string)
- [Translate all JSON-Files in a directory structure](#translate-all-json-files-in-a-directory-structure)
- [Caching](#caching)
- [Translation listener](#translation-listener)
- [Protect placeholder `${...}` and `{...}`](#protect-placeholder--and)

## Dependencies

- Java 11
- FasterXML/jackson-databind for json support

## How to build

```sh
mvn install package
```

## How to use

Maven-Dependency

```xml
<dependency>
	<groupId>com.sitepark</groupId>
	<artifactId>translate-api</artifactId>
	<version>2.1.0</version>
</dependency>
```

## Select language and provider

The API supports multiple providers that can translate the text. For translation, the provider type must be specified along with the source and target languages.

For this purpose a `TranslationLanguage` object is created.

```java
TranslationLanguage language = TranslationLanguage.builder()
		.providerType(SupportedProvider.LIBRE_TRANSLATE)
		.source("de")
		.target("en")
		.build();
```

A configuration must be stored for the specified provider, which can be set via the `TranslatorConfiguration.Builder`.

```java

LibreTranslateTranslationProviderConfiguration providerConfig =
		LibreTranslateTranslationProviderConfiguration.builder()
				.url(...)
				.apiKey(...)
				.build();

TranslatorConfiguration translatorConfiguration =
		TranslatorConfiguration.builder()
				.translationProviderConfiguration(providerConfig)
				...
				.build();
```

### Translate a singe String/Html

```java
TranslatorConfiguration translatorConfiguration = TranslatorConfiguration.builder()
		.providerConfig(...)
		...
		.build();

StringTranslator translator = StringTranslator.builder()
		.translatorConfiguration(translatorConfiguration)
		.build();

TranslationLanguage language = TranslationLanguage.builder()
		.source("de")
		.target("en")
		.build();

// translate string
TranslationParameter parameterForText = TranslationParameter.builder()
		.format(Format.TEXT)
		.language(language)
		.providerType(SupportedProvider.DEEPL)
		.build();
String result = translator.translate(parameterForText, "Hallo");

// translate html
TranslationParameter parameterForHtml = TranslationParameter.builder()
		.format(Format.HTML)
		.language(language)
		.providerType(SupportedProvider.DEEPL)
		.build();
String htmlResult = translator.translate(parameterForHtml, "<strong>Hallo</strong>");
```

### Translate a String-Array

```java
TranslatorConfiguration translatorConfiguration = TranslatorConfiguration.builder()
		.providerConfig(...)
		...
		.build();

StringArrayTranslator translator = StringArrayTranslator.builder()
		.translatorConfiguration(translatorConfiguration)
		.build();

TranslationLanguage language = TranslationLanguage.builder()
		.source("de")
		.target("en")
		.build();

TranslationParameter parameter = TranslationParameter.builder()
		.format(Format.TEXT)
		.language(language)
		.providerType(SupportedProvider.DEEPL)
		.build();

String[] result = translator.translate(	parameter, new String[] {"Hallo", "Welt"});

// or
String[] result = translator.translate(	parameter, "Hallo", "Welt");
```

### Translate a Json-String

```java
TranslatorConfiguration translatorConfiguration = TranslatorConfiguration.builder()
		.providerConfig(...)
		...
		.build();

JsonStringTranslator translator = JsonStringTranslator.builder()
		.translatorConfiguration(translatorConfiguration)
		.build();

TranslationLanguage language = TranslationLanguage.builder()
		.source("de")
		.target("en")
		.build();

TranslationParameter parameter = TranslationParameter.builder()
		.format(Format.TEXT)
		.language(language)
		.providerType(SupportedProvider.DEEPL)
		.build();

String sourceJson = ...
String targetJson = translator.translate(parameter, sourceJson);
```

`sourceJson`:
```json
{
	"headline": "Überschrift",
	"text": "Ein schöner Text",
	"items": [
		{
			"text": "Blumen",
			"number": 10,
			"boolean": true
		}
	]
}
```

`targetJson`:

```json
{
	"headline": "Heading",
	"text": "A beautiful text",
	"items": [
		{
			"text": "Flowers",
			"number": 10,
			"boolean": true
		}
	]
}
```

### Translate all JSON-Files in a directory structure

Is the following directory structure given

```
└─ basedir/
   └─ de/
      ├─ a.json
      └─ b/
         └─ c.json
```

The following code translates all json files and puts them in a parallel directory.

```java
TranslatorConfiguration translatorConfiguration = TranslatorConfiguration.builder()
		.providerConfig(...)
		...
		.build();

JsonFileListTranslator jsonFileListTranslator = JsonFileListTranslator.builder()
		.dir(Paths.get("basedir"))
		.output(Paths.get("output/de.translated"))
		.sourceLang("de")
		.targetLangList("en")
		.translatorConfiguration(translatorConfiguration)
		.build();

jsonFileListTranslator.translate(SupportedProvider.LIBRE_TRANSLATE);
```

The translations are stored in this structure
```
output/
└── de.translated/
    └── en/
        ├── a.json
        └── b/
            └── c.json
```

### Caching

With the caching implementation you can prevent that already translated texts are translated again. This can be useful if, for example, a list contains many texts, but only individual texts have been updated. With the help of the cache, the entire list can be retranslated, whereby only the changed texts are passed to libretranslate.

For this purpose the `TranslationCache` interface is implemented.

```java
public interface TranslationCache {
	public Optional<String> translate(String sourceText);
	public void update(List<? extends TranslatableText> translated);
}
```

An example here is the `TranslationFileCache` implementation. An example here is the `TranslationFileCache` implementation. Together with the `JsonFileListTranslator` only the texts within the JSON files that have changed are retranslated.

```java
Path output = Paths.get("output/de.translated");
TranslationFileCache translationCache = new TranslationFileCache(
		output.resolve(".translation-cache"));

TranslatorConfiguration translatorConfiguration = TranslatorConfiguration.builder()
		.providerConfig(...)
		...
		.build();

JsonFileListTranslator jsonFileListTranslator = JsonFileListTranslator.builder()
		.dir(Paths.get("basedir"))
		.output(output)
		.translationCache(translationCache)
		.sourceLang("de")
		.targetLangList("en")
		.translatorConfiguration(translatorConfiguration)
		.build();
jsonFileListTranslator.translate(SupportedProvider.LIBRE_TRANSLATE);
```

### Protect placeholder `${...}` and `{...}`

In the case that language packages of applications are to be translated, the case occurs that these texts contain placeholders, which are only filled in at runtime in the application.

With placeholders of the form `${...}` and `{...}` it can be prevented that these are also translated. This way the placeholders remain in the translated text.

```java
TranslatorConfiguration translatorConfiguration = TranslatorConfiguration.builder()
		//...
		.encodePlaceholder(true)
		//...
		.build();
```
