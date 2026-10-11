package com.jarvis.android.commands

import android.content.Context

/**
 * Represents a single voice command with its trigger phrases and action.
 *
 * @param name     Internal identifier for the command.
 * @param phrases  List of spoken phrases that trigger this command (lowercase).
 * @param description  Human-readable description of what the command does.
 * @param action   Lambda that executes the command and returns a spoken response string.
 */
data class Command(
    val name: String,
    val phrases: List<String>,
    val description: String,
    val action: (Context) -> String
)
