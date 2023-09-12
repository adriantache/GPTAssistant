package com.adriantache.gptassistant.domain.util

class Event<T>(wrappedValue: T) {
    private var isConsumed = false

    val value: T? = wrappedValue
        get() {
            if (isConsumed) return null

            isConsumed = true
            return field
        }

    companion object {
        fun <T> T.asEvent(): Event<T> {
            return Event(this)
        }
    }
}
