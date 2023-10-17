package com.sitepark.translate.translator; // NOPMD by veltrup on 24.05.23, 15:37

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sitepark.translate.Language;
import com.sitepark.translate.SupportedLanguages;
import com.sitepark.translate.SupportedProvider;
import com.sitepark.translate.TranslationLanguage;
import com.sitepark.translate.TranslationParameter;
import com.sitepark.translate.TranslationProvider;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressWarnings({
	"PMD.GuardLogStatement",
	"PMD.TooManyMethods"
})
public final class JsonFileListTranslator extends Translator {

	private final Path dir;

	private final Path output;

	private final String sourceLang;

	private final Set<String> targetLangList;

	private final Logger logger;

	private Path sourceDir;

	private List<JsonFile> jsonFiles;

	private List<JsonNode> jsonNodes;

	private List<TranslatableTextNode> translatableTextNodeList;

	private JsonFileListTranslator(Builder builder) {
		super(builder);
		this.dir = builder.dir;
		this.output = builder.output;
		this.sourceLang = builder.sourceLang;
		this.targetLangList = builder.targetLangList;
		this.logger = builder.logger;
	}

	public static Builder builder() {
		return new Builder();
	}

	public void translate(SupportedProvider provider) throws IOException {
		this.translate(provider, (List<String>)null);
	}

	public void translate(SupportedProvider provider, List<String> targets) throws IOException {

		this.sourceDir = this.dir.resolve(this.sourceLang);

		Language sourceLanguage = this.getSourceLanguage(provider);
		this.loadJsonFiles();
		this.collectJsonNodes();
		this.collectTranslatableText();

		long tt = System.currentTimeMillis();

		List<String> targetLanguageList;
		if (targets != null) {
			targetLanguageList = targets;
		} else {
			targetLanguageList = sourceLanguage.getTargets();
		}

		for (String targetLanguage : targetLanguageList) {
			if (this.sourceLang.equals(targetLanguage)) {
				continue;
			}
			if (!this.targetLangList.isEmpty() && !this.targetLangList.contains(targetLanguage)) {
				continue;
			}
			long t = System.currentTimeMillis();

			this.logger.info(
					"translate " + this.sourceLang + " -> " + targetLanguage);
			this.translate(provider, targetLanguage);
			long duration = (System.currentTimeMillis() - t) / 1000;
			this.logger.info(
					"translated " + this.sourceLang + " -> " + targetLanguage +
					" in " + duration + " seconds.");
			this.write(targetLanguage);
		}
		long totalDuration = (System.currentTimeMillis() - tt) / 1000;
		this.logger.info("translated in " + totalDuration + " seconds.");
	}

	private void write(String lang) throws IOException {

		ObjectMapper mapper = new ObjectMapper();

		for (JsonFile jsonFile : this.jsonFiles) {
			Path translatedFile = this.getOutputDir(lang).resolve(jsonFile.sourceFile);
			Path parent = translatedFile.getParent();
			if (parent != null && !Files.exists(parent)) {
				Files.createDirectories(parent);
			}
			mapper.writerWithDefaultPrettyPrinter()
					.writeValue(translatedFile.toFile(), jsonFile.node);
		}
	}

	private void collectJsonNodes() {
		this.jsonNodes = this.jsonFiles.stream()
				.map(json -> json.node)
				.collect(Collectors.toList());
	}

	private void collectTranslatableText() {
		TranslatableTextNodeCollector collector = new TranslatableTextNodeCollector();
		this.translatableTextNodeList = collector.collect(this.jsonNodes);
	}

	@SuppressFBWarnings("RCN_REDUNDANT_NULLCHECK_WOULD_HAVE_BEEN_A_NPE")
	private void loadJsonFiles() throws IOException {
		try (Stream<Path> stream = Files.walk(this.sourceDir)) {
			this.jsonFiles = stream
				.filter(Files::isRegularFile)
				.filter(p -> p.toString().endsWith(".json"))
				.map(this::createJsonFile)
				.collect(Collectors.toList());
		}
	}

	private Language getSourceLanguage(SupportedProvider provider) {
		TranslationProvider translator = this.createTransporter(provider);
		SupportedLanguages supportedLanguages = translator.getSupportedLanguages();
		Optional<Language> sourceLanguage = supportedLanguages.getSourceLanguage(this.sourceLang);
		return sourceLanguage.orElseThrow(() -> new IllegalArgumentException(
				"Unsupported source language '" + this.sourceLang + "'"));
	}

	private void translate(SupportedProvider provider, String targetLang) {

		TranslationFileCache cache = this.createTranslationCache(targetLang);

		TranslatableTextListTranslator translator = TranslatableTextListTranslator.builder()
				.translatorConfiguration(this.getTranslatorConfiguration().toBuilder()
						.translationCache(cache)
				)
				.build();

		TranslationLanguage language = TranslationLanguage.builder()
				.source(this.sourceLang)
				.target(targetLang)
				.build();

		TranslationParameter parameter = TranslationParameter.builder()
				.providerType(provider)
				.language(language)
				.build();

		translator.translate(parameter, this.translatableTextNodeList);

		try {
			cache.store();
		} catch (Exception e) {
			this.logger.error(
					"Unable to store cache " + cache.getFile() + ": " + e.getMessage(),
					e);
		}
	}

	private JsonFile createJsonFile(Path file) {

		if (this.sourceDir == null) {
			throw new IllegalStateException("sourceDir is not set");
		}

		Path path = file.subpath(this.sourceDir.getNameCount(), file.getNameCount());
		JsonNode node = this.parseJson(file);
		return new JsonFile(path, node);
	}

	private JsonNode parseJson(Path file) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			return mapper.readTree(file.toFile());
		} catch (Exception e) {
			throw new TranslatorException(e.getMessage(), e);
		}
	}

	private TranslationFileCache createTranslationCache(String targetLang) {

		Path cacheFile = this.getOutputDir(targetLang).resolve(".translation-cache-file");

		TranslationFileCache cache = new TranslationFileCache(cacheFile);
		try {
			cache.load();
		} catch (Exception e) {
			this.logger.error(
					"Unalbe to load cache " + cacheFile + ": " + e.getMessage(),
					e);
		}
		return cache;
	}

	private Path getOutputDir(String lang) {
		return this.output.resolve(lang);
	}

	public static interface Logger {
		void info(String msg);
		void error(String msg, Throwable t);
	}

	private final static class JsonFile {
		private final Path sourceFile;
		private final JsonNode node;
		private JsonFile(Path sourceFile, JsonNode node) {
			this.sourceFile = sourceFile;
			this.node = node;
		}
	}

	public final static class Builder extends Translator.Builder<Builder> {

		private Path dir;

		private Path output;

		private String sourceLang;

		private final Set<String> targetLangList = new HashSet<>();

		private Logger logger;

		private Builder() { }

		public Builder dir(Path dir) {
			Objects.requireNonNull(dir, "dir is null");
			this.dir = dir.toAbsolutePath();
			return this;
		}

		public Builder output(Path output) {
			Objects.requireNonNull(output, "output is null");
			this.output = output.toAbsolutePath();
			return this;
		}

		public Builder sourceLang(String sourceLang) {
			Objects.requireNonNull(sourceLang, "sourceLang is null");
			this.sourceLang = sourceLang;
			return this;
		}

		public Builder targetLangList(String... targetLangList) {
			Objects.requireNonNull(targetLangList, "targetLangList is null");
			this.targetLangList.addAll(Arrays.asList(targetLangList));
			return this;
		}

		public Builder targetLangList(Set<String> targetLangList) {
			Objects.requireNonNull(targetLangList, "targetLangList is null");
			this.targetLangList.addAll(targetLangList);
			return this;
		}

		public Builder logger(Logger logger) {
			Objects.requireNonNull(logger, "logger is null");
			this.logger = logger;
			return this;
		}

		@Override
		protected Builder self() {
			return this;
		}

		@Override
		public JsonFileListTranslator build() {
			assert dir != null : "dir is null";
			assert output != null : "output is null";
			assert sourceLang != null : "sourceLang is null";
			if (this.logger == null) {
				this.logger = new NullLogger();
			}
			return new JsonFileListTranslator(this);
		}
	}

	private final static class NullLogger implements Logger {
		@Override
		public void info(String msg) {
			// null logger
		}
		@Override
		public void error(String msg, Throwable t) {
			// null logger
		}
	}

}
