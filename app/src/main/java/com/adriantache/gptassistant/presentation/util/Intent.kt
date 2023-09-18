package com.adriantache.gptassistant.presentation.util

import android.app.SearchManager
import android.content.Context
import android.content.Intent

fun Context.openSearch(query: String) {
    val intent = Intent(Intent.ACTION_WEB_SEARCH)
    intent.putExtra(SearchManager.QUERY, query)

    this.startActivity(intent)
}
