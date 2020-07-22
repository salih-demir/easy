package com.cascade.easy.manager

import android.content.Context
import android.speech.tts.TextToSpeech
import com.cascade.easy.service.SpeechService

class SpeechManager(context: Context, onInitListener: OnInitListener) :
    TextToSpeech(context, onInitListener), SpeechService {
    override fun speak(message: CharSequence) {
        speak(message, QUEUE_FLUSH, null, message.toString())
    }
}