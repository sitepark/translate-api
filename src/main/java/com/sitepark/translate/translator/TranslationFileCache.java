package com.sitepark.translate.translator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sitepark.translate.TranslationCache;

public class TranslationFileCache implements TranslationCache {

	private final Map<Integer, String> cache = new ConcurrentHashMap<>();

	private final Path file;

	public TranslationFileCache(Path file) {
		this.file = file.toAbsolutePath();
	}

	public Path getFile() {
		return this.file;
	}

	public void load() throws IOException {

		if (!Files.exists(this.file)) {
			return;
		}

		ObjectMapper mapper = new ObjectMapper();
		this.cache.clear();
		this.cache.putAll(
				mapper.readValue(this.file.toFile(), new CacheType()));
	}

	public void store() throws IOException {
		Path parent = this.getFile().getParent();
		if (parent != null) {
			Files.createDirectories(parent);
		}
		ObjectMapper mapper = new ObjectMapper();
		mapper.writeValue(this.file.toFile(), this.cache);
	}

	@Override
	public Optional<String> translate(String sourceText) {
		Integer key = this.toKey(sourceText);
		return Optional.ofNullable(this.cache.get(key));
	}

	@Override
	public void update(List<? extends TranslatableText> translated) {
		this.cache.clear();
		for (TranslatableText text : translated) {
			Integer key = this.toKey(text.getSourceText());
			this.cache.put(key, text.getTargetText());
		}
	}

	private Integer toKey(String text) {
		return text.hashCode();
	}

	private final static class CacheType extends TypeReference<Map<Integer, String>> { }
}
