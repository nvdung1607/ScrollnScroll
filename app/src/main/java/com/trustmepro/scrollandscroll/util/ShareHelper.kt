package com.trustmepro.scrollandscroll.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import androidx.core.content.FileProvider
import com.trustmepro.scrollandscroll.R
import com.trustmepro.scrollandscroll.data.model.BadgeType
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ShareHelper {

    /**
     * Tạo hình ảnh Giấy Khen chuẩn phôi Việt Nam chất lượng cao dạng Bitmap và bắn Intent chia sẻ Story
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
            putExtra(Intent.EXTRA_TEXT, "🔥 Tôi vừa nhận Giấy Khen danh hiệu '${badge.title}' trong game Scroll & Scroll! Đã cuộn ${String.format(Locale.US, "%,.1f", totalMeters)} mét giấy vệ sinh vô tri!")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val chooser = Intent.createChooser(intent, "Chia sẻ Giấy Khen Lên Story").apply {
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
        // Tải phôi ảnh giấy khen mẫu
        val templateBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.bg_certificate_template)
        val width = templateBitmap.width
        val height = templateBitmap.height

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // 1. Vẽ hình nền phôi giấy khen
        canvas.drawBitmap(templateBitmap, 0f, 0f, null)

        val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        val displayName = if (nickname.isBlank()) "Chiến Thần Giấu Tên" else nickname

        // 2. Dòng chức danh người ban hành
        val authorityPaint = Paint().apply {
            color = android.graphics.Color.parseColor("#1E1B18")
            textSize = width * 0.026f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
            isAntiAlias = true
        }
        canvas.drawText("VIỆN TRƯỞNG VIỆN KHOA HỌC VÔ TRI", width / 2f, height * 0.495f, authorityPaint)

        // 3. Khen tặng Chiến Thần
        val namePaint = Paint().apply {
            color = android.graphics.Color.parseColor("#1E1B18")
            textSize = width * 0.028f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.SERIF, Typeface.NORMAL)
            isAntiAlias = true
        }
        val redHighlightPaint = Paint().apply {
            color = android.graphics.Color.parseColor("#D32F2F")
            textSize = width * 0.032f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
            isAntiAlias = true
        }
        canvas.drawText("Khen tặng: $displayName   - Lớp: Hội Vô Tri Toàn Cầu", width / 2f, height * 0.560f, namePaint)

        // 4. Đạt danh hiệu
        val badgeTitleText = "Đạt danh hiệu: ${badge.title} ${badge.badgeEmoji}"
        canvas.drawText(badgeTitleText, width / 2f, height * 0.625f, redHighlightPaint)

        // 5. Thành tích số mét cuộn
        val statsPaint = Paint().apply {
            color = android.graphics.Color.parseColor("#2E7D32")
            textSize = width * 0.024f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        canvas.drawText("Thành tích: Đã cuộn ${String.format(Locale.US, "%,.1f", totalMeters)} mét giấy vệ sinh vô tận", width / 2f, height * 0.680f, statsPaint)

        // 6. Lời khen / Lời cà khịa
        val quotePaint = Paint().apply {
            color = android.graphics.Color.parseColor("#5D4037")
            textSize = width * 0.022f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.SERIF, Typeface.ITALIC)
            isAntiAlias = true
        }
        canvas.drawText("\"${badge.description}\"", width / 2f, height * 0.730f, quotePaint)

        // 7. Góc dưới bên trái: Vào sổ khen thưởng
        val recordPaint = Paint().apply {
            color = android.graphics.Color.parseColor("#444444")
            textSize = width * 0.019f
            textAlign = Paint.Align.LEFT
            typeface = Typeface.create(Typeface.SERIF, Typeface.ITALIC)
            isAntiAlias = true
        }
        canvas.drawText("Vào sổ khen thưởng: Số 3669/QĐ-VOTRI", width * 0.16f, height * 0.795f, recordPaint)
        canvas.drawText("Ngày $currentDate", width * 0.16f, height * 0.835f, recordPaint)

        // 8. Góc dưới bên phải: Ngày tháng, Viện trưởng & Chữ ký
        val dateSignPaint = Paint().apply {
            color = android.graphics.Color.parseColor("#333333")
            textSize = width * 0.020f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.SERIF, Typeface.ITALIC)
            isAntiAlias = true
        }
        val rightColX = width * 0.72f
        canvas.drawText("Việt Nam, ngày $currentDate", rightColX, height * 0.735f, dateSignPaint)

        val titleSignPaint = Paint().apply {
            color = android.graphics.Color.parseColor("#1E1B18")
            textSize = width * 0.024f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
            isAntiAlias = true
        }
        canvas.drawText("Viện Trưởng", rightColX, height * 0.775f, titleSignPaint)

        // Chữ ký bay bổng
        val signaturePaint = Paint().apply {
            color = android.graphics.Color.parseColor("#D32F2F")
            textSize = width * 0.024f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD_ITALIC)
            isAntiAlias = true
        }
        canvas.drawText("Scroll Master", rightColX, height * 0.845f, signaturePaint)

        // 9. Con dấu mộc đỏ tròn "CHỨNG NHẬN 100% VÔ TRI"
        val stampCenterX = rightColX + width * 0.04f
        val stampCenterY = height * 0.805f
        val stampRadius = width * 0.055f

        val stampPaint = Paint().apply {
            color = android.graphics.Color.parseColor("#D32F2F")
            style = Paint.Style.STROKE
            strokeWidth = width * 0.0035f
            isAntiAlias = true
        }
        canvas.drawCircle(stampCenterX, stampCenterY, stampRadius, stampPaint)
        canvas.drawCircle(stampCenterX, stampCenterY, stampRadius * 0.88f, stampPaint)

        val stampTextPaint = Paint().apply {
            color = android.graphics.Color.parseColor("#D32F2F")
            textSize = width * 0.013f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        canvas.drawText("CHỨNG NHẬN", stampCenterX, stampCenterY - stampRadius * 0.22f, stampTextPaint)
        canvas.drawText("100% VÔ TRI", stampCenterX, stampCenterY + stampRadius * 0.15f, stampTextPaint)
        canvas.drawText("★ ★ ★", stampCenterX, stampCenterY + stampRadius * 0.48f, stampTextPaint)

        return bitmap
    }

    private fun saveBitmapToCache(context: Context, bitmap: Bitmap): android.net.Uri? {
        return try {
            val cachePath = File(context.cacheDir, "images").apply { mkdirs() }
            val file = File(cachePath, "giay_khen_${System.currentTimeMillis()}.png")
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
