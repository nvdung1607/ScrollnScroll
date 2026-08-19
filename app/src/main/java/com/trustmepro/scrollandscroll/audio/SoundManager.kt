package com.trustmepro.scrollandscroll.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import java.io.File
import java.io.FileOutputStream
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.pow
import kotlin.math.sin
import kotlin.random.Random

// ─────────────────────────────────────────────────────────────────────────────
// SoundManager — Hệ thống âm thanh ASMR cuộn giấy chân thực và sinh động
// ─────────────────────────────────────────────────────────────────────────────

class SoundManager(private val context: Context) {

    private var soundPool: SoundPool? = null
    private val rollSoundIds = mutableListOf<Int>()
    private var spinSoundId: Int = 0
    private var clickSoundId: Int = 0
    private var popSoundId: Int = 0
    private var fanfareSoundId: Int = 0
    private var overdriveSoundId: Int = 0

    var isSoundEnabled: Boolean = true

    private var lastRollTime = 0L
    private var soundIndex = 0

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
            val cacheDir = context.cacheDir

            // 1. Ba biến thể âm thanh cuộn giấy sột soạt (ASMR Paper Rustle / Swish)
            val swish1 = File(cacheDir, "sfx_asmr_swish_1.wav")
            writeWavFile(swish1, generateAsmrPaperSwish(durationMs = 60, seed = 101))
            val id1 = soundPool?.load(swish1.absolutePath, 1) ?: 0
            if (id1 != 0) rollSoundIds.add(id1)

            val swish2 = File(cacheDir, "sfx_asmr_swish_2.wav")
            writeWavFile(swish2, generateAsmrPaperSwish(durationMs = 50, seed = 202))
            val id2 = soundPool?.load(swish2.absolutePath, 1) ?: 0
            if (id2 != 0) rollSoundIds.add(id2)

            val swish3 = File(cacheDir, "sfx_asmr_swish_3.wav")
            writeWavFile(swish3, generateAsmrPaperSwish(durationMs = 70, seed = 303))
            val id3 = soundPool?.load(swish3.absolutePath, 1) ?: 0
            if (id3 != 0) rollSoundIds.add(id3)

            // 2. Tiếng trục quay lách cách / rít gió khi vuốt nhanh
            val spinFile = File(cacheDir, "sfx_asmr_core_spin.wav")
            writeWavFile(spinFile, generateAsmrCoreSpin(durationMs = 85))
            spinSoundId = soundPool?.load(spinFile.absolutePath, 1) ?: 0

            // 3. Tiếng gõ nút UI
            val clickFile = File(cacheDir, "sfx_comic_click.wav")
            writeWavFile(clickFile, generateComicClick(durationMs = 30))
            clickSoundId = soundPool?.load(clickFile.absolutePath, 1) ?: 0

            // 4. Tiếng chọn Skin Pop
            val popFile = File(cacheDir, "sfx_comic_pop.wav")
            writeWavFile(popFile, generateComicPop(durationMs = 70))
            popSoundId = soundPool?.load(popFile.absolutePath, 1) ?: 0

            // 5. Tiếng kèn Fanfare mở khóa Bằng khen
            val fanfareFile = File(cacheDir, "sfx_comic_fanfare.wav")
            writeWavFile(fanfareFile, generateComicFanfare(durationMs = 550))
            fanfareSoundId = soundPool?.load(fanfareFile.absolutePath, 1) ?: 0

            // 6. Tiếng hiệu ứng Overdrive
            val overdriveFile = File(cacheDir, "sfx_comic_overdrive.wav")
            writeWavFile(overdriveFile, generateComicOverdriveZip(durationMs = 180))
            overdriveSoundId = soundPool?.load(overdriveFile.absolutePath, 1) ?: 0
        } catch (_: Exception) {
            // Graceful fallback
        }
    }

    /**
     * Phát âm thanh cuộn giấy với tốc độ (velocity) và nhịp SPS thực tế
     */
    fun playRoll(velocity: Float = 500f, sps: Float = 2.0f) {
        if (!isSoundEnabled || rollSoundIds.isEmpty()) return

        val now = System.currentTimeMillis()
        // Nhịp độ giãn cách âm thanh tỷ lệ nghịch với tốc độ vuốt (càng nhanh thì âm càng dồn dập)
        val minInterval = (90L - (velocity / 45f).toLong()).coerceIn(28L, 95L)
        if (now - lastRollTime < minInterval) return
        lastRollTime = now

        // Cao độ (pitch): kéo nhanh thì âm cao hơn, kéo chậm thì âm trầm êm ái
        val basePitch = 0.88f + (sps * 0.05f) + (velocity / 5000f) * 0.35f
        val clampedPitch = basePitch.coerceIn(0.80f, 1.65f)

        // Âm lượng (volume): tỷ lệ thuận với lực kéo
        val volume = (0.22f + (velocity / 3500f) * 0.28f).coerceIn(0.20f, 0.60f)

        // Luân phiên các biến thể âm thanh ngẫu nhiên để không bị lặp lại đơn điệu
        val soundId = if (velocity > 1800f && spinSoundId != 0 && Random.nextFloat() > 0.4f) {
            spinSoundId
        } else {
            rollSoundIds[soundIndex % rollSoundIds.size]
        }
        soundIndex++

        soundPool?.play(soundId, volume, volume, 1, 0, clampedPitch)
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
        soundPool?.play(overdriveSoundId, 0.35f, 0.35f, 3, 0, 1.1f)
    }

    fun release() {
        soundPool?.release()
        soundPool = null
        rollSoundIds.clear()
    }

    // ─────────────────────────────────────────────────────────────────────────
    // TỔNG HỢP ÂM THANH ASMR PCM 44.1kHz CHÂN THỰC
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Tạo âm thanh sột soạt ma sát giấy lụa nhiều lớp (ASMR Paper Rustle)
     */
    private fun generateAsmrPaperSwish(durationMs: Int, seed: Long): ByteArray {
        val sampleRate = 44100
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val buffer = ByteArray(numSamples * 2)
        val rng = Random(seed)

        var b0 = 0.0
        var b1 = 0.0
        var b2 = 0.0

        for (i in 0 until numSamples) {
            val t = i.toDouble() / numSamples
            // Đường cong biên độ: tấn công nhanh, ngân êm
            val envelope = sin(t * PI).pow(0.8) * exp(-t * 2.5)

            // Bộ lọc Pink Noise tạo tiếng sột soạt mềm của giấy
            val white = rng.nextDouble() * 2.0 - 1.0
            b0 = 0.99886 * b0 + white * 0.0555179
            b1 = 0.99332 * b1 + white * 0.0750759
            b2 = 0.96900 * b2 + white * 0.1538520
            val pinkNoise = b0 + b1 + b2 + white * 0.5362

            // Tiếng ma sát bề mặt giấy
            val frictionTone = sin(2.0 * PI * 420.0 * (i.toDouble() / sampleRate)) * 0.18
            val combined = (pinkNoise * 0.82 + frictionTone) * envelope * 16500.0

            val sample = combined.toInt().coerceIn(-32767, 32767).toShort()
            buffer[i * 2] = (sample.toInt() and 0xFF).toByte()
            buffer[i * 2 + 1] = ((sample.toInt() shr 8) and 0xFF).toByte()
        }
        return createWavHeader(buffer.size, sampleRate) + buffer
    }

    /**
     * Tạo tiếng trục lõi carton quay lách cách khi cuộn nhanh
     */
    private fun generateAsmrCoreSpin(durationMs: Int): ByteArray {
        val sampleRate = 44100
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val buffer = ByteArray(numSamples * 2)

        for (i in 0 until numSamples) {
            val t = i.toDouble() / numSamples
            val envelope = sin(t * PI) * exp(-t * 1.8)

            // Tiếng gõ trục xoay lách cách (Spindle rattle click)
            val woodClick = sin(2.0 * PI * 180.0 * (i.toDouble() / sampleRate)) * 0.45 +
                    sin(2.0 * PI * 340.0 * (i.toDouble() / sampleRate)) * 0.35
            val airWhoosh = (Math.random() * 2.0 - 1.0) * 0.35

            val sample = ((woodClick + airWhoosh) * envelope * 17000.0).toInt().coerceIn(-32767, 32767).toShort()
            buffer[i * 2] = (sample.toInt() and 0xFF).toByte()
            buffer[i * 2 + 1] = ((sample.toInt() shr 8) and 0xFF).toByte()
        }
        return createWavHeader(buffer.size, sampleRate) + buffer
    }

    private fun generateComicClick(durationMs: Int): ByteArray {
        val sampleRate = 44100
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val buffer = ByteArray(numSamples * 2)

        for (i in 0 until numSamples) {
            val t = i.toDouble() / numSamples
            val envelope = exp(-t * 14.0)
            val tone = sin(2.0 * PI * 820.0 * (i.toDouble() / sampleRate))
            val sample = (tone * envelope * 15500.0).toInt().toShort()
            buffer[i * 2] = (sample.toInt() and 0xFF).toByte()
            buffer[i * 2 + 1] = ((sample.toInt() shr 8) and 0xFF).toByte()
        }
        return createWavHeader(buffer.size, sampleRate) + buffer
    }

    private fun generateComicPop(durationMs: Int): ByteArray {
        val sampleRate = 44100
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val buffer = ByteArray(numSamples * 2)

        for (i in 0 until numSamples) {
            val t = i.toDouble() / numSamples
            val freq = 540.0 + t * 450.0
            val envelope = sin(t * PI) * exp(-t * 4.0)
            val tone = sin(2.0 * PI * freq * (i.toDouble() / sampleRate))
            val sample = (tone * envelope * 17000.0).toInt().toShort()
            buffer[i * 2] = (sample.toInt() and 0xFF).toByte()
            buffer[i * 2 + 1] = ((sample.toInt() shr 8) and 0xFF).toByte()
        }
        return createWavHeader(buffer.size, sampleRate) + buffer
    }

    private fun generateComicFanfare(durationMs: Int): ByteArray {
        val sampleRate = 44100
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val buffer = ByteArray(numSamples * 2)

        for (i in 0 until numSamples) {
            val time = i.toDouble() / sampleRate
            val t = i.toDouble() / numSamples
            val envelope = (1.0 - t) * exp(-t * 1.8)

            val chord = sin(2.0 * PI * 523.25 * time) * 0.4 +
                    sin(2.0 * PI * 659.25 * time) * 0.35 +
                    sin(2.0 * PI * 783.99 * time) * 0.25

            val sample = (chord * envelope * 19000.0).toInt().toShort()
            buffer[i * 2] = (sample.toInt() and 0xFF).toByte()
            buffer[i * 2 + 1] = ((sample.toInt() shr 8) and 0xFF).toByte()
        }
        return createWavHeader(buffer.size, sampleRate) + buffer
    }

    private fun generateComicOverdriveZip(durationMs: Int): ByteArray {
        val sampleRate = 44100
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val buffer = ByteArray(numSamples * 2)

        for (i in 0 until numSamples) {
            val t = i.toDouble() / numSamples
            val envelope = sin(t * PI)
            val tone = sin(2.0 * PI * (380.0 + t * 480.0) * (i.toDouble() / sampleRate))
            val sample = (tone * envelope * 14000.0).toInt().toShort()
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

