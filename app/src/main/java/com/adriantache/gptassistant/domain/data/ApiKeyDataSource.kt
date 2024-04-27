package com.adriantache.gptassistant.domain.data

interface ApiKeyDataSource {
    var openAiApiKey: String?
    var stabilityAiApiKey: String?
}
