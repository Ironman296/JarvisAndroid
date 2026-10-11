package com.jarvis.android.actions

import android.content.Context
import android.content.Intent
import android.hardware.camera2.CameraManager
import android.media.AudioManager
import android.os.BatteryManager
import android.provider.Settings
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Device actions executed via Android system APIs.
 *
 * Every method returns a human-readable response string so the
 * TextToSpeech engine can speak the result back to the user.
 *
 * Only uses APIs available on API 24+ (our minSdk).
 * Flashlight via CameraManager.setTorchMode() does NOT need CAMERA permission.
 */
class DeviceActions(private val context: Context) {

    fun openCalculator(): String {
        val intent = Intent().apply {
            action = Intent.ACTION_MAIN
            addCategory(Intent.CATEGORY_APP_CALCULATOR)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        return try {
            context.startActivity(intent)
            "Opening calculator"
        } catch (e: Exception) {
            "Calculator app not found on this device"
        }
    }

    fun openSettings(): String {
        val intent = Intent(Settings.ACTION_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
        return "Opening settings"
    }

    fun setTorch(on: Boolean): String {
        return try {
            val cameraManager =
                context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
            val cameraId = cameraManager.cameraIdList.firstOrNull()
                ?: return "No camera found on this device"
            cameraManager.setTorchMode(cameraId, on)
            if (on) "Flashlight turned on" else "Flashlight turned off"
        } catch (e: Exception) {
            "Could not control flashlight: ${e.message}"
        }
    }

    fun getBatteryLevel(): String {
        val batteryManager =
            context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        val level = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        return "Battery is at $level percent"
    }

    fun adjustVolume(increase: Boolean): String {
        val audioManager =
            context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val direction =
            if (increase) AudioManager.ADJUST_RAISE else AudioManager.ADJUST_LOWER
        audioManager.adjustStreamVolume(
            AudioManager.STREAM_MUSIC,
            direction,
            AudioManager.FLAG_SHOW_UI
        )
        return if (increase) "Volume increased" else "Volume decreased"
    }

    fun getCurrentTime(): String {
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val time = sdf.format(Date())
        return "The current time is $time"
    }

    fun openWifiSettings(): String {
        val intent = Intent(Settings.ACTION_WIFI_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
        return "Opening Wi-Fi settings"
    }

    fun openBluetoothSettings(): String {
        val intent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
        return "Opening Bluetooth settings"
    }
}
