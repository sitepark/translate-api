package com.sitepark.translate;

import java.util.List;
import java.util.Optional;

import com.sitepark.translate.translator.TranslatableText;

/**
 * With the caching implementation you can prevent that already translated
 * texts are translated again. This can be useful if, for example, a list
 * contains many texts, but only individual texts have been updated. With
 * the help of the cache, the entire list can be retranslated, whereby only
 * the changed texts are passed to libretranslate.
 */
public interface TranslationCache {

	/**
	 * Returns an non empty optional, if the translation is cached.
	 */
	public Optional<String> translate(String sourceText);

	/**
	 * Passes all translated texts of a translation process. Also those that
	 * were determined via caching.
	 */
	public void update(List<? extends TranslatableText> translated);
}
