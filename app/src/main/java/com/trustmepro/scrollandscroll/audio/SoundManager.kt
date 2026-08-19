package com.trustmepro.scrollandscroll.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import java.io.File
import java.io.FileOutputStream
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

class SoundManager(private val context: Context) {

    private var soundPool: SoundPool? = null
    private var rollSoundId: Int = 0
    private var clickSoundId: Int = 0
    private var popSoundId: Int = 0
    private var fanfareSoundId: Int = 0
    private var overdriveSoundId: Int = 0

    var isSoundEnabled: Boolean = true

    // Rate limiting to prevent ear fatigue
    private var lastRollTime = 0L

    init {
        initSoundPool()
    }

    private fun initSoundPool() {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(6)
            .setAudioAttributes(audioAttributes)
            .build()

        generateAndLoadSounds()
    }

    private fun generateAndLoadSounds() {
        try {
            val cacheDir = context.cacheDir

            // Bright paper flick: tactile, but short enough to feel like an animated game.
            val rollFile = File(cacheDir, "sfx_comic_paper_flick.wav")
            writeWavFile(rollFile, generateComicPaperFlick(durationMs = 45))
            rollSoundId = soundPool?.load(rollFile.absolutePath, 1) ?: 0

            // Snappy UI tap
            val clickFile = File(cacheDir, "sfx_comic_click.wav")
            writeWavFile(clickFile, generateComicClick(durationMs = 28))
            clickSoundId = soundPool?.load(clickFile.absolutePath, 1) ?: 0

            // Reward pop for selecting an item or skin
            val popFile = File(cacheDir, "sfx_comic_pop.wav")
            writeWavFile(popFile, generateComicPop(durationMs = 70))
            popSoundId = soundPool?.load(popFile.absolutePath, 1) ?: 0

            // Compact major-key unlock fanfare
            val fanfareFile = File(cacheDir, "sfx_comic_fanfare.wav")
            writeWavFile(fanfareFile, generateComicFanfare(durationMs = 500))
            fanfareSoundId = soundPool?.load(fanfareFile.absolutePath, 1) ?: 0

            // Cartoon zip when the speed lines appear
            val overdriveFile = File(cacheDir, "sfx_comic_overdrive.wav")
            writeWavFile(overdriveFile, generateComicOverdriveZip(durationMs = 150))
            overdriveSoundId = soundPool?.load(overdriveFile.absolutePath, 1) ?: 0
        } catch (_: Exception) {
            // Graceful fallback
        }
    }

    fun playRoll(pitch: Float = 1.0f) {
        if (!isSoundEnabled || rollSoundId == 0) return
        val now = System.currentTimeMillis()
        if (now - lastRollTime < 75L) return // Throttle to prevent ear fatigue
        lastRollTime = now

        val clampedPitch = pitch.coerceIn(0.85f, 1.4f)
        soundPool?.play(rollSoundId, 0.22f, 0.22f, 1, 0, clampedPitch)
    }

    fun playClick() {
        if (!isSoundEnabled || clickSoundId == 0) return
        soundPool?.play(clickSoundId, 0.25f, 0.25f, 2, 0, 1.0f)
    }

    fun playPop() {
        if (!isSoundEnabled || popSoundId == 0) return
        soundPool?.play(popSoundId, 0.35f, 0.35f, 3, 0, 1.0f)
    }

    fun playFanfare() {
        if (!isSoundEnabled || fanfareSoundId == 0) return
        soundPool?.play(fanfareSoundId, 0.45f, 0.45f, 4, 0, 1.0f)
    }

    fun playOverdrive() {
        if (!isSoundEnabled || overdriveSoundId == 0) return
        soundPool?.play(overdriveSoundId, 0.3f, 0.3f, 3, 0, 1.1f)
    }

    fun release() {
        soundPool?.release()
        soundPool = null
    }

    // --- Lightweight procedural comic-game synthesis (no external audio assets required). ---

    private fun generateComicPaperFlick(durationMs: Int): ByteArray {
        val sampleRate = 22050
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val buffer = ByteArray(numSamples * 2)

        var lastSample = 0.0
        for (i in 0 until numSamples) {
            val t = i.toDouble() / numSamples
            val envelope = (1.0 - t) * exp(-t * 3.6)

            // Filtered paper noise with a tiny animated flick at the attack.
            val whiteNoise = Math.random() * 2.0 - 1.0
            lastSample = (lastSample * 0.7) + (whiteNoise * 0.3) // Low-pass filter

            val frictionSine = sin(2.0 * PI * 235.0 * (i.toDouble() / sampleRate)) * 0.23
            val combined = (lastSample * 0.72 + frictionSine) * envelope * 9800.0

            val sample = combined.toInt().coerceIn(-32767, 32767).toShort()
            buffer[i * 2] = (sample.toInt() and 0xFF).toByte()
            buffer[i * 2 + 1] = ((sample.toInt() shr 8) and 0xFF).toByte()
        }
        return createWavHeader(buffer.size, sampleRate) + buffer
    }

    private fun generateComicClick(durationMs: Int): ByteArray {
        val sampleRate = 22050
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val buffer = ByteArray(numSamples * 2)

        for (i in 0 until numSamples) {
            val t = i.toDouble() / numSamples
            val envelope = exp(-t * 13.0)
            val tone = sin(2.0 * PI * 760.0 * (i.toDouble() / sampleRate))
            val sample = (tone * envelope * 14500.0).toInt().toShort()
            buffer[i * 2] = (sample.toInt() and 0xFF).toByte()
            buffer[i * 2 + 1] = ((sample.toInt() shr 8) and 0xFF).toByte()
        }
        return createWavHeader(buffer.size, sampleRate) + buffer
    }

    private fun generateComicPop(durationMs: Int): ByteArray {
        val sampleRate = 22050
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val buffer = ByteArray(numSamples * 2)

        for (i in 0 until numSamples) {
            val t = i.toDouble() / numSamples
            val freq = 520.0 + t * 420.0
            val envelope = sin(t * PI) * exp(-t * 4.0)
            val tone = sin(2.0 * PI * freq * (i.toDouble() / sampleRate))
            val sample = (tone * envelope * 16000.0).toInt().toShort()
            buffer[i * 2] = (sample.toInt() and 0xFF).toByte()
            buffer[i * 2 + 1] = ((sample.toInt() shr 8) and 0xFF).toByte()
        }
        return createWavHeader(buffer.size, sampleRate) + buffer
    }

    private fun generateComicFanfare(durationMs: Int): ByteArray {
        val sampleRate = 22050
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val buffer = ByteArray(numSamples * 2)

        for (i in 0 until numSamples) {
            val time = i.toDouble() / sampleRate
            val t = i.toDouble() / numSamples
            val envelope = (1.0 - t) * exp(-t * 2.0)

            // Crisp major chord (C5 + E5 + G5)
            val chord = sin(2.0 * PI * 523.25 * time) * 0.4 +
                    sin(2.0 * PI * 659.25 * time) * 0.35 +
                    sin(2.0 * PI * 783.99 * time) * 0.25

            val sample = (chord * envelope * 18000.0).toInt().toShort()
            buffer[i * 2] = (sample.toInt() and 0xFF).toByte()
            buffer[i * 2 + 1] = ((sample.toInt() shr 8) and 0xFF).toByte()
        }
        return createWavHeader(buffer.size, sampleRate) + buffer
    }

    private fun generateComicOverdriveZip(durationMs: Int): ByteArray {
        val sampleRate = 22050
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val buffer = ByteArray(numSamples * 2)

        for (i in 0 until numSamples) {
            val t = i.toDouble() / numSamples
            val envelope = sin(t * PI)
            val tone = sin(2.0 * PI * (400.0 + t * 400.0) * (i.toDouble() / sampleRate))
            val sample = (tone * envelope * 12000.0).toInt().toShort()
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
            16, 0, 0, 0,
            1, 0,
            1, 0,
            (sampleRate and 0xFF).toByte(), ((sampleRate shr 8) and 0xFF).toByte(),
            ((sampleRate shr 16) and 0xFF).toByte(), ((sampleRate shr 24) and 0xFF).toByte(),
            (byteRate and 0xFF).toByte(), ((byteRate shr 8) and 0xFF).toByte(),
            ((byteRate shr 16) and 0xFF).toByte(), ((byteRate shr 24) and 0xFF).toByte(),
            2, 0,
            16, 0,
            'd'.code.toByte(), 'a'.code.toByte(), 't'.code.toByte(), 'a'.code.toByte(),
            (dataSize and 0xFF).toByte(), ((dataSize shr 8) and 0xFF).toByte(),
            ((dataSize shr 16) and 0xFF).toByte(), ((dataSize shr 24) and 0xFF).toByte()
        )
    }

    private fun writeWavFile(file: File, bytes: ByteArray) {
        FileOutputStream(file).use { it.write(bytes) }
    }
}
