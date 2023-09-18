package com.adriantache.gptassistant.di

import android.app.Activity
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.adriantache.gptassistant.data.RepositoryImpl
import com.adriantache.gptassistant.data.firebase.FirebaseDatabaseImpl
import com.adriantache.gptassistant.data.retrofit.OpenAiApi
import com.adriantache.gptassistant.data.retrofit.getRetrofit
import com.adriantache.gptassistant.domain.ConversationUseCases
import com.adriantache.gptassistant.domain.data.Repository
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module
import retrofit2.Retrofit

private const val preferencesFile = "prefs"

fun Activity.koinSetup() {
    startKoin {
        androidContext(this@koinSetup)

        modules(
            module {
                single { ConversationUseCases(get()) }

                single<Repository> { RepositoryImpl(get(), get()) }

                single { getRetrofit() }
                factory<OpenAiApi> { get<Retrofit>().create(OpenAiApi::class.java) }

                single { FirebaseDatabaseImpl(get()) }

                factory<SharedPreferences> { get<Context>().getSharedPreferences(preferencesFile, MODE_PRIVATE) }
            }
        )
    }
}
