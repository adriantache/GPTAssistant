package com.adriantache.gptassistant.presentation.util

// Used to keep
class ListScrollEntries {
    private val entries = mutableListOf<Int>()

    val isScrollingDown: Boolean
        get() {
            for (i in 1 until entries.size) {
                if (entries[i] < entries[i - 1]) return false
            }

            return true
        }

    fun addItem(item: Int) {
        entries.add(item)

        if (entries.size > 3) entries.removeAt(0)
    }
}
