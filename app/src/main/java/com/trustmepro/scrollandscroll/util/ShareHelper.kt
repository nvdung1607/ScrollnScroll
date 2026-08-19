package com.trustmepro.scrollandscroll.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import androidx.core.content.FileProvider
import com.trustmepro.scrollandscroll.data.model.BadgeType
import java.io.File
import java.io.FileOutputStream
import java.util.Locale

object ShareHelper {

    /**
     * Tạo hình ảnh Bằng Khen tỷ lệ 9:16 chất lượng cao dạng Bitmap và bắn Intent chia sẻ Story
     */
    fun shareCertificate(
        context: Context,
        badge: BadgeType,
        nickname: String,
        totalMeters: Double
    ) {
        val bitmap = generateCertificateBitmap(context, badge, nickname, totalMeters)
        val uri = saveBitmapToCache(context, bitmap) ?: return

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_TEXT, "🔥 Tôi vừa đạt danh hiệu '${badge.title}' trong game Scroll & Scroll! Đã cuộn ${String.format(Locale.US, "%,.1f", totalMeters)} mét giấy vệ sinh vô tri!")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val chooser = Intent.createChooser(intent, "Chia sẻ Bằng Khen Lên Story").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(chooser)
    }

    private fun generateCertificateBitmap(
        context: Context,
        badge: BadgeType,
        nickname: String,
        totalMeters: Double
    ): Bitmap {
        val width = 1080
        val height = 1920
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // 1. Nền vàng kem ấm áp
        canvas.drawColor(android.graphics.Color.parseColor("#FFFDF0"))

        // 2. Viền hoa văn đôi mạ vàng & đen đậm
        val borderPaint = Paint().apply {
            color = android.graphics.Color.parseColor("#1E1B18")
            style = Paint.Style.STROKE
            strokeWidth = 14f
            isAntiAlias = true
        }
        val goldBorderPaint = Paint().apply {
            color = android.graphics.Color.parseColor("#FFD54F")
            style = Paint.Style.STROKE
            strokeWidth = 24f
            isAntiAlias = true
        }
        canvas.drawRoundRect(RectF(40f, 40f, width - 40f, height - 40f), 36f, 36f, goldBorderPaint)
        canvas.drawRoundRect(RectF(60f, 60f, width - 60f, height - 60f), 24f, 24f, borderPaint)

        // 3. Quốc hiệu trào phúng
        val subHeaderPaint = Paint().apply {
            color = android.graphics.Color.parseColor("#1E1B18")
            textSize = 34f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        canvas.drawText("VIỆN HÀN LÂM KHOA HỌC VÔ TRI", width / 2f, 160f, subHeaderPaint)

        val mottoPaint = Paint().apply {
            color = android.graphics.Color.parseColor("#5D4037")
            textSize = 28f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
            isAntiAlias = true
        }
        canvas.drawText("« Cuộn Bất Tận - Vô Tri Bất Diệt »", width / 2f, 210f, mottoPaint)

        // Đường kẻ phân cách
        val linePaint = Paint().apply {
            color = android.graphics.Color.parseColor("#1E1B18")
            strokeWidth = 4f
        }
        canvas.drawLine(width * 0.25f, 250f, width * 0.75f, 250f, linePaint)

        // 4. Tiêu đề BẰNG KHEN CHỨNG NHẬN
        val certTitlePaint = Paint().apply {
            color = android.graphics.Color.parseColor("#FF5722")
            textSize = 76f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        canvas.drawText("BẰNG KHEN DANH DỰ", width / 2f, 370f, certTitlePaint)

        // 5. Tên người nhận
        val certifyPaint = Paint().apply {
            color = android.graphics.Color.parseColor("#1E1B18")
            textSize = 36f
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText("Trân trọng trao tặng cho Chiến Thần:", width / 2f, 470f, certifyPaint)

        val namePaint = Paint().apply {
            color = android.graphics.Color.parseColor("#1E1B18")
            textSize = 68f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        val displayName = if (nickname.isBlank()) "Chiến Thần Giấu Tên" else nickname
        canvas.drawText("★ $displayName ★", width / 2f, 560f, namePaint)

        // 6. Icon Emoji to ở giữa
        val emojiPaint = Paint().apply {
            textSize = 180f
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText(badge.badgeEmoji, width / 2f, 800f, emojiPaint)

        // 7. Danh hiệu đạt được
        val badgeTitlePaint = Paint().apply {
            color = android.graphics.Color.parseColor("#E65100")
            textSize = 54f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        canvas.drawText("[ ${badge.title} ]", width / 2f, 940f, badgeTitlePaint)

        // 8. Lời cà khịa
        val quotePaint = Paint().apply {
            color = android.graphics.Color.parseColor("#37474F")
            textSize = 36f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
            isAntiAlias = true
        }
        canvas.drawText("\"${badge.description}\"", width / 2f, 1030f, quotePaint)

        // 9. Thống kê số mét tiêu hao
        val statsBox = RectF(width * 0.15f, 1120f, width * 0.85f, 1300f)
        val statsBgPaint = Paint().apply {
            color = android.graphics.Color.parseColor("#1E1B18")
        }
        canvas.drawRoundRect(statsBox, 28f, 28f, statsBgPaint)

        val statsLabelPaint = Paint().apply {
            color = android.graphics.Color.parseColor("#FFD54F")
            textSize = 30f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        canvas.drawText("TỔNG KHOẢNG CÁCH ĐÃ CUỘN", width / 2f, 1180f, statsLabelPaint)

        val statsNumberPaint = Paint().apply {
            color = android.graphics.Color.WHITE
            textSize = 58f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        canvas.drawText("${String.format(Locale.US, "%,.1f", totalMeters)} MÉT", width / 2f, 1260f, statsNumberPaint)

        // 10. Con dấu đỏ 100% Vô Tri
        val stampCenterX = width * 0.75f
        val stampCenterY = 1540f
        val stampRadius = 140f
        val stampPaint = Paint().apply {
            color = android.graphics.Color.parseColor("#D32F2F")
            style = Paint.Style.STROKE
            strokeWidth = 8f
            isAntiAlias = true
        }
        canvas.drawCircle(stampCenterX, stampCenterY, stampRadius, stampPaint)
        canvas.drawCircle(stampCenterX, stampCenterY, stampRadius - 16f, stampPaint)

        val stampTextPaint = Paint().apply {
            color = android.graphics.Color.parseColor("#D32F2F")
            textSize = 28f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        canvas.drawText("CHỨNG NHẬN", stampCenterX, stampCenterY - 30f, stampTextPaint)
        canvas.drawText("100% VÔ TRI", stampCenterX, stampCenterY + 15f, stampTextPaint)
        canvas.drawText("★ ★ ★", stampCenterX, stampCenterY + 55f, stampTextPaint)

        // 11. Footer Game Branding
        val footerPaint = Paint().apply {
            color = android.graphics.Color.parseColor("#757575")
            textSize = 26f
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText("Game: Scroll & Scroll - Cuộn Giấy Vệ Sinh Vô Tận", width / 2f, 1800f, footerPaint)
        canvas.drawText("Tải ngay trên Google Play Store để thử thách ngón tay!", width / 2f, 1840f, footerPaint)

        return bitmap
    }

    private fun saveBitmapToCache(context: Context, bitmap: Bitmap): android.net.Uri? {
        return try {
            val cachePath = File(context.cacheDir, "images").apply { mkdirs() }
            val file = File(cachePath, "certificate_${System.currentTimeMillis()}.png")
            val stream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.close()

            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
