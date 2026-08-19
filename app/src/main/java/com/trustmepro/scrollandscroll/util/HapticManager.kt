package com.trustmepro.scrollandscroll.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

class HapticManager(private val context: Context) {

    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    var isHapticEnabled: Boolean = true

    // Rate limiting to prevent overwhelming vibrations
    private var lastTickTime = 0L

    fun tick() {
        if (!isHapticEnabled || vibrator?.hasVibrator() != true) return
        val now = System.currentTimeMillis()
        if (now - lastTickTime < 60L) return // Throttle
        lastTickTime = now

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK))
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(4, 50)) // Super light 4ms vibration
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(4)
        }
    }

    fun click() {
        if (!isHapticEnabled || vibrator?.hasVibrator() != true) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(10, 100))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(10)
        }
    }

    fun heavyPulse() {
        if (!isHapticEnabled || vibrator?.hasVibrator() != true) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val timings = longArrayOf(0, 30, 40, 45)
            val amplitudes = intArrayOf(0, 120, 0, 180)
            vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(longArrayOf(0, 30, 40, 45), -1)
        }
    }
}
