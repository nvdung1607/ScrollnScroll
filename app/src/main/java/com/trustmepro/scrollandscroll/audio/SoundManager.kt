package com.trustmepro.scrollandscroll.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import java.io.File
import java.io.FileOutputStream
import kotlin.math.PI
import kotlin.math.sin

class SoundManager(private val context: Context) {

    private var soundPool: SoundPool? = null
    private var rollSoundId: Int = 0
    private var clickSoundId: Int = 0
    private var popSoundId: Int = 0
    private var fanfareSoundId: Int = 0
    private var overdriveSoundId: Int = 0

    var isSoundEnabled: Boolean = true

    init {
        initSoundPool()
    }

    private fun initSoundPool() {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(10)
            .setAudioAttributes(audioAttributes)
            .build()

        generateAndLoadSounds()
    }

    private fun generateAndLoadSounds() {
        try {
            // Generate synthesized PCM audio files in cache and load into SoundPool
            val cacheDir = context.cacheDir

            val clickFile = File(cacheDir, "sfx_click.wav")
            if (!clickFile.exists()) writeWavFile(clickFile, generateTone(frequency = 880.0, durationMs = 35))
            clickSoundId = soundPool?.load(clickFile.absolutePath, 1) ?: 0

            val rollFile = File(cacheDir, "sfx_roll.wav")
            if (!rollFile.exists()) writeWavFile(rollFile, generateNoise(durationMs = 60))
            rollSoundId = soundPool?.load(rollFile.absolutePath, 1) ?: 0

            val popFile = File(cacheDir, "sfx_pop.wav")
            if (!popFile.exists()) writeWavFile(popFile, generateTone(frequency = 520.0, durationMs = 70))
            popSoundId = soundPool?.load(popFile.absolutePath, 1) ?: 0

            val fanfareFile = File(cacheDir, "sfx_fanfare.wav")
            if (!fanfareFile.exists()) writeWavFile(fanfareFile, generateFanfare(durationMs = 400))
            fanfareSoundId = soundPool?.load(fanfareFile.absolutePath, 1) ?: 0

            val overdriveFile = File(cacheDir, "sfx_overdrive.wav")
            if (!overdriveFile.exists()) writeWavFile(overdriveFile, generateTone(frequency = 1200.0, durationMs = 120))
            overdriveSoundId = soundPool?.load(overdriveFile.absolutePath, 1) ?: 0
        } catch (_: Exception) {
            // Graceful fallback if file I/O fails
        }
    }

    fun playRoll(pitch: Float = 1.0f) {
        if (!isSoundEnabled || rollSoundId == 0) return
        val clampedPitch = pitch.coerceIn(0.6f, 2.0f)
        soundPool?.play(rollSoundId, 0.4f, 0.4f, 1, 0, clampedPitch)
    }

    fun playClick() {
        if (!isSoundEnabled || clickSoundId == 0) return
        soundPool?.play(clickSoundId, 0.7f, 0.7f, 2, 0, 1.0f)
    }

    fun playPop() {
        if (!isSoundEnabled || popSoundId == 0) return
        soundPool?.play(popSoundId, 0.9f, 0.9f, 3, 0, 1.2f)
    }

    fun playFanfare() {
        if (!isSoundEnabled || fanfareSoundId == 0) return
        soundPool?.play(fanfareSoundId, 1.0f, 1.0f, 4, 0, 1.0f)
    }

    fun playOverdrive() {
        if (!isSoundEnabled || overdriveSoundId == 0) return
        soundPool?.play(overdriveSoundId, 0.8f, 0.8f, 3, 0, 1.5f)
    }

    fun release() {
        soundPool?.release()
        soundPool = null
    }

    // --- Audio Synthesis Utilities ---

    private fun generateTone(frequency: Double, durationMs: Int): ByteArray {
        val sampleRate = 22050
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val buffer = ByteArray(numSamples * 2)

        for (i in 0 until numSamples) {
            val time = i.toDouble() / sampleRate
            val envelope = 1.0 - (i.toDouble() / numSamples) // Linear fade-out
            val sample = (sin(2.0 * PI * frequency * time) * 32767.0 * envelope).toInt().toShort()
            buffer[i * 2] = (sample.toInt() and 0xFF).toByte()
            buffer[i * 2 + 1] = ((sample.toInt() shr 8) and 0xFF).toByte()
        }
        return createWavHeader(buffer.size, sampleRate) + buffer
    }

    private fun generateNoise(durationMs: Int): ByteArray {
        val sampleRate = 22050
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val buffer = ByteArray(numSamples * 2)

        for (i in 0 until numSamples) {
            val envelope = 1.0 - (i.toDouble() / numSamples)
            val randomVal = (Math.random() * 2.0 - 1.0)
            val sample = (randomVal * 16000.0 * envelope).toInt().toShort()
            buffer[i * 2] = (sample.toInt() and 0xFF).toByte()
            buffer[i * 2 + 1] = ((sample.toInt() shr 8) and 0xFF).toByte()
        }
        return createWavHeader(buffer.size, sampleRate) + buffer
    }

    private fun generateFanfare(durationMs: Int): ByteArray {
        val sampleRate = 22050
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val buffer = ByteArray(numSamples * 2)

        for (i in 0 until numSamples) {
            val time = i.toDouble() / sampleRate
            val freq = if (time < 0.15) 523.25 else if (time < 0.28) 659.25 else 783.99
            val envelope = (1.0 - (i.toDouble() / numSamples)).coerceIn(0.0, 1.0)
            val sample = (sin(2.0 * PI * freq * time) * 28000.0 * envelope).toInt().toShort()
            buffer[i * 2] = (sample.toInt() and 0xFF).toByte()
            buffer[i * 2 + 1] = ((sample.toInt() shr 8) and 0xFF).toByte()
        }
        return createWavHeader(buffer.size, sampleRate) + buffer
    }

    private fun createWavHeader(dataSize: Int, sampleRate: Int): ByteArray {
        val totalDataLen = dataSize + 36
        val byteRate = sampleRate * 2
        return byteArrayOf(
            'R'.code.toByte(), 'I'.code.toByte(), 'F'.code.toByte(), 'F'.code.toByte(),
            (totalDataLen and 0xFF).toByte(), ((totalDataLen shr 8) and 0xFF).toByte(),
            ((totalDataLen shr 16) and 0xFF).toByte(), ((totalDataLen shr 24) and 0xFF).toByte(),
            'W'.code.toByte(), 'A'.code.toByte(), 'V'.code.toByte(), 'E'.code.toByte(),
            'f'.code.toByte(), 'm'.code.toByte(), 't'.code.toByte(), ' '.code.toByte(),
            16, 0, 0, 0, // subchunk1size (16 for PCM)
            1, 0, // audioFormat (1 for PCM)
            1, 0, // numChannels (1: mono)
            (sampleRate and 0xFF).toByte(), ((sampleRate shr 8) and 0xFF).toByte(),
            ((sampleRate shr 16) and 0xFF).toByte(), ((sampleRate shr 24) and 0xFF).toByte(),
            (byteRate and 0xFF).toByte(), ((byteRate shr 8) and 0xFF).toByte(),
            ((byteRate shr 16) and 0xFF).toByte(), ((byteRate shr 24) and 0xFF).toByte(),
            2, 0, // blockAlign
            16, 0, // bitsPerSample
            'd'.code.toByte(), 'a'.code.toByte(), 't'.code.toByte(), 'a'.code.toByte(),
            (dataSize and 0xFF).toByte(), ((dataSize shr 8) and 0xFF).toByte(),
            ((dataSize shr 16) and 0xFF).toByte(), ((dataSize shr 24) and 0xFF).toByte()
        )
    }

    private fun writeWavFile(file: File, bytes: ByteArray) {
        FileOutputStream(file).use { it.write(bytes) }
    }
}
