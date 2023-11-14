package com.adriantache.gptassistant.di

import android.app.Application
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.adriantache.gptassistant.data.RepositoryImpl
import com.adriantache.gptassistant.data.dataSource.ApiKeyDataSourceImpl
import com.adriantache.gptassistant.data.dataSource.SettingsDataSourceImpl
import com.adriantache.gptassistant.data.firebase.FirebaseDatabaseImpl
import com.adriantache.gptassistant.data.retrofit.OpenAiApi
import com.adriantache.gptassistant.data.retrofit.getRetrofit
import com.adriantache.gptassistant.domain.AssistantUseCases
import com.adriantache.gptassistant.domain.ConversationUseCases
import com.adriantache.gptassistant.domain.SettingsUseCases
import com.adriantache.gptassistant.domain.data.ApiKeyDataSource
import com.adriantache.gptassistant.domain.data.Repository
import com.adriantache.gptassistant.domain.data.SettingsDataSource
import com.adriantache.gptassistant.presentation.tts.AudioRecognizer
import com.adriantache.gptassistant.presentation.tts.TtsHelper
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module
import retrofit2.Retrofit

private const val preferencesFile = "prefs"

fun Application.koinSetup() {
    startKoin {
        androidContext(this@koinSetup)

        modules(
            module {
                single { AssistantUseCases(get(), get()) }
                single { ConversationUseCases(get()) }
                single { SettingsUseCases(get()) }

                single<Repository> { RepositoryImpl(get(), get(), get()) }
                single<SettingsDataSource> { SettingsDataSourceImpl(get()) }
                single<ApiKeyDataSource> { ApiKeyDataSourceImpl(get()) }

                single { getRetrofit() }
                factory<OpenAiApi> { get<Retrofit>().create(OpenAiApi::class.java) }

                single { FirebaseDatabaseImpl(get()) }

                factory<SharedPreferences> { get<Context>().getSharedPreferences(preferencesFile, MODE_PRIVATE) }

                single { TtsHelper(get()) }
                single { AudioRecognizer(get()) }
            }
        )
    }
}
