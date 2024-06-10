/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.base.utils

/**
 * Utility class for casting objects to specific types.
 */
object CastUtils {

    /**
     * Extracts a list of strings from the given list.
     *
     * @param list The source list to extract from.
     * @return A list containing only the string elements from the input list,
     * or an empty list if there are no string elements.
     */
    fun toStringList(list: Any?): List<String> {
        return (list as? List<*>)?.filterIsInstance<String>().orEmpty()
    }

    /**
     * Extracts a map with string keys and string values from the given map.
     *
     * @param map The source map to extract from.
     * @return A map containing only the entries with string keys and string values
     * from the input map, or an empty map if there are no such entries.
     */
    fun toStringMap(map: Any?): Map<String, String> {
        return (map as? Map<*, *>)?.entries
            ?.filter { it.key is String && it.value is String }
            ?.associate { it.key as String to it.value as String }
            ?: emptyMap()
    }
}