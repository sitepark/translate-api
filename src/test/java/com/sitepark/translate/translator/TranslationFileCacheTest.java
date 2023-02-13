package com.sitepark.translate.translator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
class TranslationFileCacheTest {

	@Test
	void test() throws Exception {
		Path file = Paths.get("target/test/lang/en/.cache-file");
		Files.createDirectories(file.getParent());
		TranslationFileCache cache = new TranslationFileCache(file);

		List<TranslatableText> list = new ArrayList<>();
		TranslatableText text = new TranslatableText("Hallo Welt");
		text.setTargetText("Hello World");
		list.add(text);

		cache.update(list);
		cache.store();

		String expected = "{\"-972308576\":\"Hello World\"}";
		assertEquals(expected, Files.readString(file), "unexpected file content");
	}
}
