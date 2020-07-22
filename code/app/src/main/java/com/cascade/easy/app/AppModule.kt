package com.cascade.easy.app

import android.content.Context
import androidx.annotation.StringRes
import com.cascade.easy.service.NetworkService
import com.cascade.easy.service.PreferenceService
import com.cascade.easy.service.SpeechService
import com.cascade.easy.util.toast

interface AppModule {
    val preferenceService: PreferenceService?
    val networkService: NetworkService?
    val speechService: SpeechService?

    fun provideContext(): Context

    fun createMessage(message: CharSequence, imageSource: Any? = null) {
        provideContext().toast(message) {
            speechService?.speak(message)
        }
    }

    fun createMessage(@StringRes messageRes: Int, imageSource: Any? = null) {
        val message = provideContext().getText(messageRes)
        createMessage(message, imageSource)
    }
}