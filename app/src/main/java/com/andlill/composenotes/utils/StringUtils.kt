package com.andlill.composenotes.utils

object StringUtils {
    // Returns a list of pairs (start index, end index) of substrings found in string.
    fun String.indexesOfSubstring(word: String) : List<Pair<Int, Int>> {
        val list: ArrayList<Pair<Int, Int>> = ArrayList()

        if (word.isEmpty() || this.isEmpty())
            return emptyList()

        // Loop while index of word is found.
        var currentStartIndex = 0
        while (this.indexOf(word, currentStartIndex) != -1) {
            // Find start and end index of word and add to list.
            val start = this.indexOf(word, currentStartIndex)
            val end = start + word.length
            list.add(Pair(start, end))
            // Set new current start index to word end index.
            currentStartIndex = end
        }

        return list
    }
}