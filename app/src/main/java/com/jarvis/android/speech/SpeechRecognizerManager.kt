package com.jarvis.android.speech

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer

/**
 * Wraps Android's built-in SpeechRecognizer.
 *
 * IMPORTANT CAVEAT: Android's SpeechRecognizer usually requires Google's
 * speech services and an internet connection. For true offline recognition,
 * Vosk will be evaluated in a future version. This module is structured so
 * swapping the backend only requires changing this class.
 */
class SpeechRecognizerManager(
    private val context: Context,
    private val onResult: (String) -> Unit,
    private val onError: (String) -> Unit,
    private val onStatusChange: (Status) -> Unit
) {

    enum class Status {
        IDLE, LISTENING, PROCESSING, ERROR
    }

    private var speechRecognizer: SpeechRecognizer? = null
    private var isListening = false

    /** Create the SpeechRecognizer instance. Call once after construction. */
    fun initialize() {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            onError("Speech recognition is not available on this device")
            return
        }
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
            setRecognitionListener(createListener())
        }
    }

    /** Start listening for speech input. */
    fun startListening() {
        if (isListening) return

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            // en-IN supports Hinglish mixed input on most devices
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-IN")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }

        speechRecognizer?.startListening(intent)
        isListening = true
        onStatusChange(Status.LISTENING)
    }

    /** Stop listening gracefully. */
    fun stopListening() {
        if (!isListening) return
        speechRecognizer?.stopListening()
        isListening = false
        onStatusChange(Status.IDLE)
    }

    /** Release resources. Call in Activity.onDestroy(). */
    fun destroy() {
        speechRecognizer?.destroy()
        speechRecognizer = null
        isListening = false
    }

    private fun createListener(): RecognitionListener {
        return object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                onStatusChange(Status.LISTENING)
            }

            override fun onBeginningOfSpeech() {}

            override fun onRmsChanged(rmsdB: Float) {}

            override fun onBufferReceived(buffer: ByteArray?) {}

            override fun onEndOfSpeech() {
                isListening = false
                onStatusChange(Status.PROCESSING)
            }

            override fun onError(error: Int) {
                isListening = false
                val message = when (error) {
                    SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
                    SpeechRecognizer.ERROR_CLIENT -> "Client side error"
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
                    SpeechRecognizer.ERROR_NETWORK ->
                        "Network error — offline speech may not be available on this device"
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
                    SpeechRecognizer.ERROR_NO_MATCH -> "No speech recognized"
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Recognition service busy"
                    SpeechRecognizer.ERROR_SERVER -> "Server error"
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech input detected"
                    else -> "Unknown error ($error)"
                }
                onStatusChange(Status.ERROR)
                onError(message)
            }

            override fun onResults(results: Bundle?) {
                isListening = false
                val matches =
                    results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val text = matches?.firstOrNull().orEmpty()
                onStatusChange(Status.IDLE)
                if (text.isNotEmpty()) {
                    onResult(text)
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {
                // Could show partial transcription in UI — future enhancement
            }

            override fun onEvent(eventType: Int, params: Bundle?) {}
        }
    }
}
