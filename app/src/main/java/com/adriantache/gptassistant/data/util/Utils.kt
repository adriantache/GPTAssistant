package com.adriantache.gptassistant.data.util

import android.content.SharedPreferences

fun SharedPreferences.getString(key: String): String? {
    return this.getString(key, null)
}
