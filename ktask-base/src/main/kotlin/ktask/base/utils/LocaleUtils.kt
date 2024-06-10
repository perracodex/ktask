/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.base.utils

import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * Helper class for working with locales.
 */
object LocaleUtils {
    private val localeCache = ConcurrentHashMap<String, Boolean>()
    private val availableLocaleTags: Set<String> = Locale.getAvailableLocales()
        .map { it.toLanguageTag().lowercase() }
        .toSet()

    /**
     * Checks if the given string is a valid locale.
     */
    fun isValidLocale(string: String): Boolean {
        return localeCache.computeIfAbsent(string) {
            try {
                val inputLocaleTag: String = Locale.Builder().setLanguageTag(it).build().toLanguageTag().lowercase()
                availableLocaleTags.contains(inputLocaleTag)
            } catch (e: IllformedLocaleException) {
                false
            }
        }
    }
}
