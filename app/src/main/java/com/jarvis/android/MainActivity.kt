package com.jarvis.android

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.jarvis.android.commands.CommandEngine
import com.jarvis.android.speech.SpeechRecognizerManager
import com.jarvis.android.speech.TextToSpeechManager

/**
 * Main (and only) Activity for JarvisAndroid v0.1.
 *
 * Wires together: mic permission → speech recognition → command engine → TTS response.
 */
class MainActivity : AppCompatActivity() {

    companion object {
        private const val PERMISSION_REQUEST_CODE = 100
    }

    // UI elements
    private lateinit var statusText: TextView
    private lateinit var recognizedText: TextView
    private lateinit var responseText: TextView
    private lateinit var micButton: FloatingActionButton

    // Modules
    private lateinit var commandEngine: CommandEngine
    private var speechManager: SpeechRecognizerManager? = null
    private var ttsManager: TextToSpeechManager? = null

    private var isListening = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Bind views
        statusText = findViewById(R.id.statusText)
        recognizedText = findViewById(R.id.recognizedText)
        responseText = findViewById(R.id.responseText)
        micButton = findViewById(R.id.micButton)

        // Init command engine (deterministic, no setup delay)
        commandEngine = CommandEngine(this)

        // Init TTS (offline on most devices)
        ttsManager = TextToSpeechManager(this) {
            // TTS is ready — nothing special to do
        }

        // Mic button toggles listening on/off
        micButton.setOnClickListener {
            if (isListening) {
                stopListening()
            } else {
                checkPermissionAndListen()
            }
        }
    }

    // ── Permission handling ───────────────────────────────────────────

    private fun checkPermissionAndListen() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            == PackageManager.PERMISSION_GRANTED
        ) {
            startListening()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                startListening()
            } else {
                statusText.text = getString(R.string.status_permission_denied)
                responseText.text = getString(R.string.permission_rationale)
            }
        }
    }

    // ── Listening lifecycle ───────────────────────────────────────────

    private fun startListening() {
        if (speechManager == null) {
            speechManager = SpeechRecognizerManager(
                context = this,
                onResult = { text -> handleSpeechResult(text) },
                onError = { error -> handleSpeechError(error) },
                onStatusChange = { status -> handleStatusChange(status) }
            )
            speechManager?.initialize()
        }

        speechManager?.startListening()
        isListening = true
        micButton.setImageResource(R.drawable.ic_mic_off)
        micButton.contentDescription = getString(R.string.btn_stop)
    }

    private fun stopListening() {
        speechManager?.stopListening()
        isListening = false
        micButton.setImageResource(R.drawable.ic_mic)
        micButton.contentDescription = getString(R.string.btn_start)
        statusText.text = getString(R.string.status_idle)
    }

    // ── Callbacks ─────────────────────────────────────────────────────

    private fun handleSpeechResult(text: String) {
        isListening = false
        micButton.setImageResource(R.drawable.ic_mic)
        micButton.contentDescription = getString(R.string.btn_start)
        recognizedText.text = text

        // Process through command engine
        val response = commandEngine.processInput(text)
        responseText.text = response

        // Speak the response
        ttsManager?.speak(response)
    }

    private fun handleSpeechError(error: String) {
        isListening = false
        micButton.setImageResource(R.drawable.ic_mic)
        micButton.contentDescription = getString(R.string.btn_start)
        statusText.text = getString(R.string.status_error)
        responseText.text = error
    }

    private fun handleStatusChange(status: SpeechRecognizerManager.Status) {
        runOnUiThread {
            statusText.text = when (status) {
                SpeechRecognizerManager.Status.IDLE ->
                    getString(R.string.status_idle)
                SpeechRecognizerManager.Status.LISTENING ->
                    getString(R.string.status_listening)
                SpeechRecognizerManager.Status.PROCESSING ->
                    getString(R.string.status_processing)
                SpeechRecognizerManager.Status.ERROR ->
                    getString(R.string.status_error)
            }

            val color = when (status) {
                SpeechRecognizerManager.Status.LISTENING ->
                    ContextCompat.getColor(this, R.color.listening)
                SpeechRecognizerManager.Status.ERROR ->
                    ContextCompat.getColor(this, R.color.error)
                else ->
                    ContextCompat.getColor(this, R.color.text_secondary)
            }
            statusText.setTextColor(color)
        }
    }

    // ── Cleanup ───────────────────────────────────────────────────────

    override fun onDestroy() {
        speechManager?.destroy()
        ttsManager?.destroy()
        super.onDestroy()
    }
}
