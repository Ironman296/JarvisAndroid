package com.jarvis.android.commands

import android.content.Context
import com.jarvis.android.actions.DeviceActions

/**
 * Deterministic command matching engine.
 *
 * Matches spoken text against registered command phrases using
 * normalized substring matching. No ML, no cloud — just Kotlin rules.
 */
class CommandEngine(private val context: Context) {

    private val commands = mutableListOf<Command>()

    init {
        registerDefaultCommands()
    }

    private fun registerDefaultCommands() {
        val actions = DeviceActions(context)

        commands.add(Command(
            name = "open_calculator",
            phrases = listOf("calculator kholo", "open calculator", "calculator"),
            description = "Opens the calculator app",
            action = { actions.openCalculator() }
        ))

        commands.add(Command(
            name = "open_settings",
            phrases = listOf("settings kholo", "open settings", "settings"),
            description = "Opens device settings",
            action = { actions.openSettings() }
        ))

        commands.add(Command(
            name = "torch_on",
            phrases = listOf("torch on", "flashlight on", "torch chalu", "torch jala"),
            description = "Turns on the flashlight",
            action = { actions.setTorch(true) }
        ))

        commands.add(Command(
            name = "torch_off",
            phrases = listOf("torch off", "flashlight off", "torch band", "torch bujha"),
            description = "Turns off the flashlight",
            action = { actions.setTorch(false) }
        ))

        commands.add(Command(
            name = "battery_status",
            phrases = listOf("battery kitni hai", "battery status", "battery level", "battery check"),
            description = "Reports battery level",
            action = { actions.getBatteryLevel() }
        ))

        commands.add(Command(
            name = "volume_up",
            phrases = listOf("volume up", "volume badhao", "awaz badhao"),
            description = "Increases media volume",
            action = { actions.adjustVolume(true) }
        ))

        commands.add(Command(
            name = "volume_down",
            phrases = listOf("volume down", "volume kam karo", "awaz kam karo"),
            description = "Decreases media volume",
            action = { actions.adjustVolume(false) }
        ))

        commands.add(Command(
            name = "current_time",
            phrases = listOf("time kya hai", "what time", "kitne baje", "time batao"),
            description = "Tells the current time",
            action = { actions.getCurrentTime() }
        ))

        commands.add(Command(
            name = "open_wifi",
            phrases = listOf("wifi settings", "wifi kholo", "open wifi"),
            description = "Opens Wi-Fi settings",
            action = { actions.openWifiSettings() }
        ))

        commands.add(Command(
            name = "open_bluetooth",
            phrases = listOf("bluetooth settings", "bluetooth kholo", "open bluetooth"),
            description = "Opens Bluetooth settings",
            action = { actions.openBluetoothSettings() }
        ))
    }

    /**
     * Match input text against registered commands.
     *
     * Strategy: normalize input to lowercase, then check if any registered
     * phrase is contained in the input. Longer phrases are checked first to
     * avoid partial matches (e.g. "torch off" before "torch").
     *
     * Returns the response string from the matched command action.
     */
    fun processInput(input: String): String {
        val normalized = input.trim().lowercase()

        // Sort commands so phrases are checked longest-first to avoid
        // partial matches like "settings" matching before "open settings".
        for (command in commands) {
            val sortedPhrases = command.phrases.sortedByDescending { it.length }
            for (phrase in sortedPhrases) {
                if (normalized.contains(phrase)) {
                    return try {
                        command.action()
                    } catch (e: Exception) {
                        "Sorry, I couldn't do that: ${e.message}"
                    }
                }
            }
        }

        return "I didn't understand that. Try saying: calculator kholo, torch on, or time kya hai."
    }

    /** Get list of available commands for display in the UI. */
    fun getAvailableCommands(): List<Command> = commands.toList()
}
