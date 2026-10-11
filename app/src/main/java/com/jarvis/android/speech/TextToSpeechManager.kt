package com.jarvis.android.speech

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

/**
 * Wraps Android's built-in TextToSpeech engine for spoken responses.
 *
 * Uses en-IN locale for natural Hinglish output.
 * TTS is fully offline on most Android devices (no internet required).
 */
class TextToSpeechManager(
    context: Context,
    private val onReady: () -> Unit
) {

    private var tts: TextToSpeech? = null
    private var isReady = false

    init {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = tts?.setLanguage(Locale("en", "IN"))
                if (result == TextToSpeech.LANG_MISSING_DATA ||
                    result == TextToSpeech.LANG_NOT_SUPPORTED
                ) {
                    // Fall back to default English
                    tts?.setLanguage(Locale.ENGLISH)
                }
                isReady = true
                onReady()
            }
        }
    }

    /** Speak the given text, interrupting any current speech. */
    fun speak(text: String) {
        if (!isReady) return
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "jarvis_response")
    }

    /** Stop any current speech. */
    fun stop() {
        tts?.stop()
    }

    /** Release resources. Call in Activity.onDestroy(). */
    fun destroy() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        isReady = false
    }
}
