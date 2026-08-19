package com.trustmepro.scrollandscroll.util

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Typeface
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.FileProvider
import com.trustmepro.scrollandscroll.data.model.BadgeType
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ShareHelper {

    /**
     * Tạo hình ảnh Giấy Khen chuẩn phôi Việt Nam thuần vẽ đồ họa Canvas và bắn Intent chia sẻ Story
     */
    fun shareCertificate(
        context: Context,
        badge: BadgeType,
        nickname: String,
        totalMeters: Double
    ) {
        val bitmap = generateCertificateBitmap(badge, nickname, totalMeters)
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

    /**
     * Tải Giấy Khen lưu trực tiếp vào Thư Viện Ảnh (Gallery / Pictures / ScrollAndScroll)
     */
    fun downloadCertificateToGallery(
        context: Context,
        badge: BadgeType,
        nickname: String,
        totalMeters: Double
    ) {
        try {
            val bitmap = generateCertificateBitmap(badge, nickname, totalMeters)
            val filename = "GiayKhen_${badge.id}_${System.currentTimeMillis()}.png"
            var success = false

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/ScrollAndScroll")
                    put(MediaStore.MediaColumns.IS_PENDING, 1)
                }
                val contentResolver = context.contentResolver
                val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                if (uri != null) {
                    contentResolver.openOutputStream(uri)?.use { out ->
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                    }
                    contentValues.clear()
                    contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                    contentResolver.update(uri, contentValues, null, null)
                    success = true
                }
            } else {
                val picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                val appDir = File(picturesDir, "ScrollAndScroll").apply { mkdirs() }
                val file = File(appDir, filename)
                val out = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                out.close()
                MediaScannerConnection.scanFile(context, arrayOf(file.absolutePath), arrayOf("image/png"), null)
                success = true
            }

            if (success) {
                Toast.makeText(context, "💾 Đã tải Giấy Khen về Thư Viện Ảnh thành công!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "❌ Không thể lưu ảnh vào thư viện!", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "❌ Lỗi khi tải ảnh: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun generateCertificateBitmap(
        badge: BadgeType,
        nickname: String,
        totalMeters: Double
    ): Bitmap {
        val width = 1600
        val height = 1160
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // 1. Nền giấy kem cổ điển trang nhã
        canvas.drawColor(android.graphics.Color.parseColor("#FFFDF2"))

        val redColor = android.graphics.Color.parseColor("#D32F2F")
        val goldColor = android.graphics.Color.parseColor("#FFD54F")
        val goldDark = android.graphics.Color.parseColor("#FFA000")
        val inkBlack = android.graphics.Color.parseColor("#1A1A1A")

        // 2. Viền ngoài cùng màu đỏ
        val outerBorderPaint = Paint().apply {
            color = redColor
            style = Paint.Style.STROKE
            strokeWidth = 10f
            isAntiAlias = true
        }
        canvas.drawRect(RectF(16f, 16f, width - 16f, height - 16f), outerBorderPaint)

        // 3. Khung mạ vàng kép hoa văn
        val goldBorderPaint = Paint().apply {
            color = goldColor
            style = Paint.Style.STROKE
            strokeWidth = 24f
            isAntiAlias = true
        }
        canvas.drawRect(RectF(38f, 38f, width - 38f, height - 38f), goldBorderPaint)

        // 4. Viền trong mảnh đỏ
        val innerBorderPaint = Paint().apply {
            color = redColor
            style = Paint.Style.STROKE
            strokeWidth = 6f
            isAntiAlias = true
        }
        canvas.drawRect(RectF(60f, 60f, width - 60f, height - 60f), innerBorderPaint)

        // 5. Vẽ 4 góc hoa văn góc vuông cổ điển (Chữ Vạn cách điệu)
        val cornerPaint = Paint().apply {
            color = redColor
            style = Paint.Style.STROKE
            strokeWidth = 8f
            isAntiAlias = true
        }
        val cornerLen = 100f
        val corners = listOf(
            (60f to 60f) to (1f to 1f),
            (width - 60f to 60f) to (-1f to 1f),
            (60f to height - 60f) to (1f to -1f),
            (width - 60f to height - 60f) to (-1f to -1f)
        )
        corners.forEach { (pos, dir) ->
            val (px, py) = pos
            val (dx, dy) = dir
            val path = Path().apply {
                moveTo(px, py + dy * cornerLen)
                lineTo(px, py)
                lineTo(px + dx * cornerLen, py)
                moveTo(px + dx * 28f, py + dy * cornerLen * 0.7f)
                lineTo(px + dx * 28f, py + dy * 28f)
                lineTo(px + dx * cornerLen * 0.7f, py + dy * 28f)
            }
            canvas.drawPath(path, cornerPaint)
        }

        // 6. Huy hiệu Quốc Huy / Cờ đỏ sao vàng ở đỉnh trên giữa
        val emblemCenterX = width / 2f
        val emblemCenterY = 48f
        val emblemRadius = 45f

        val flagPaint = Paint().apply {
            color = redColor
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        val flagPath = Path().apply {
            moveTo(emblemCenterX - 130f, 65f)
            lineTo(emblemCenterX - emblemRadius, 30f)
            lineTo(emblemCenterX + emblemRadius, 30f)
            lineTo(emblemCenterX + 130f, 65f)
            lineTo(emblemCenterX + 105f, 90f)
            lineTo(emblemCenterX - 105f, 90f)
            close()
        }
        canvas.drawPath(flagPath, flagPaint)

        canvas.drawCircle(emblemCenterX, emblemCenterY + 16f, emblemRadius, flagPaint)
        val emblemBorderPaint = Paint().apply {
            color = goldDark
            style = Paint.Style.STROKE
            strokeWidth = 6f
            isAntiAlias = true
        }
        canvas.drawCircle(emblemCenterX, emblemCenterY + 16f, emblemRadius, emblemBorderPaint)

        // Ngôi sao vàng 5 cánh ở giữa
        val starPaint = Paint().apply {
            color = goldColor
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        canvas.drawCircle(emblemCenterX, emblemCenterY + 16f, 18f, starPaint)

        // 7. Hoa sen chìm mờ ở tâm nền
        val watermarkPaint = Paint().apply {
            color = android.graphics.Color.parseColor("#15FFA000")
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        canvas.drawCircle(width / 2f, height / 2f, height * 0.28f, watermarkPaint)

        val watermarkLinePaint = Paint().apply {
            color = android.graphics.Color.parseColor("#12D32F2F")
            style = Paint.Style.STROKE
            strokeWidth = 4f
            pathEffect = DashPathEffect(floatArrayOf(20f, 20f), 0f)
            isAntiAlias = true
        }
        canvas.drawCircle(width / 2f, height / 2f, height * 0.35f, watermarkLinePaint)

        val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        val displayName = if (nickname.isBlank()) "Chiến Thần Giấu Tên" else nickname

        // 8. Quốc Hiệu & Tiêu Ngữ
        val countryPaint = Paint().apply {
            color = inkBlack
            textSize = 34f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
            isAntiAlias = true
        }
        canvas.drawText("CỘNG HÒA XÃ HỘI CHỦ NGHĨA VIỆT NAM", width / 2f, 175f, countryPaint)

        val mottoPaint = Paint().apply {
            color = inkBlack
            textSize = 30f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
            isAntiAlias = true
        }
        canvas.drawText("Độc lập – Tự do – Hạnh phúc", width / 2f, 225f, mottoPaint)

        val sepPaint = Paint().apply {
            color = android.graphics.Color.parseColor("#666666")
            textSize = 28f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.SERIF, Typeface.NORMAL)
            isAntiAlias = true
        }
        canvas.drawText("----------o0o----------", width / 2f, 265f, sepPaint)

        // 9. Tiêu đề lớn GIẤY KHEN
        val titlePaint = Paint().apply {
            color = redColor
            textSize = 88f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
            letterSpacing = 0.12f
            isAntiAlias = true
        }
        canvas.drawText("GIẤY KHEN", width / 2f, 385f, titlePaint)

        // 10. Dòng thẩm quyền ban hành
        val authorityPaint = Paint().apply {
            color = inkBlack
            textSize = 38f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
            letterSpacing = 0.05f
            isAntiAlias = true
        }
        canvas.drawText("VIỆN TRƯỞNG VIỆN KHOA HỌC VÔ TRI", width / 2f, 465f, authorityPaint)

        // 11. Khen tặng Chiến Thần
        val recipientPaint = Paint().apply {
            color = inkBlack
            textSize = 38f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.SERIF, Typeface.NORMAL)
            isAntiAlias = true
        }
        canvas.drawText("Khen tặng: $displayName    -    Lớp: Hội Vô Tri Toàn Cầu", width / 2f, 555f, recipientPaint)

        // 12. Đạt danh hiệu
        val badgeTitlePaint = Paint().apply {
            color = redColor
            textSize = 46f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
            isAntiAlias = true
        }
        canvas.drawText("Đạt danh hiệu: ${badge.title} ${badge.badgeEmoji}", width / 2f, 635f, badgeTitlePaint)

        // 13. Thành tích số mét cuộn
        val statsPaint = Paint().apply {
            color = android.graphics.Color.parseColor("#1B5E20")
            textSize = 36f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
            isAntiAlias = true
        }
        canvas.drawText("Thành tích: Đã cuộn ${String.format(Locale.US, "%,.1f", totalMeters)} mét giấy vệ sinh vô tận", width / 2f, 715f, statsPaint)

        // 14. Lời phê / Lời cà khịa
        val quotePaint = Paint().apply {
            color = android.graphics.Color.parseColor("#4E342E")
            textSize = 34f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.SERIF, Typeface.ITALIC)
            isAntiAlias = true
        }
        canvas.drawText("\"${badge.description}\"", width / 2f, 785f, quotePaint)

        // 15. Góc dưới bên trái: Vào sổ khen thưởng
        val recordPaint = Paint().apply {
            color = android.graphics.Color.parseColor("#555555")
            textSize = 28f
            textAlign = Paint.Align.LEFT
            typeface = Typeface.create(Typeface.SERIF, Typeface.ITALIC)
            isAntiAlias = true
        }
        canvas.drawText("Vào sổ khen thưởng: Số 3669/QĐ-VOTRI", 140f, 920f, recordPaint)
        canvas.drawText("Ngày $currentDate", 140f, 965f, recordPaint)

        // 16. Góc dưới bên phải: Ngày tháng, Viện trưởng & Chữ ký
        val dateSignPaint = Paint().apply {
            color = android.graphics.Color.parseColor("#333333")
            textSize = 30f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.SERIF, Typeface.ITALIC)
            isAntiAlias = true
        }
        val rightColX = width - 360f
        canvas.drawText("Việt Nam, ngày $currentDate", rightColX, 875f, dateSignPaint)

        val titleSignPaint = Paint().apply {
            color = inkBlack
            textSize = 34f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
            isAntiAlias = true
        }
        canvas.drawText("Viện Trưởng", rightColX, 925f, titleSignPaint)

        // Chữ ký bay bổng
        val signaturePaint = Paint().apply {
            color = redColor
            textSize = 38f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD_ITALIC)
            isAntiAlias = true
        }
        canvas.drawText("Scroll Master", rightColX, 1025f, signaturePaint)

        // 17. Con dấu mộc đỏ tròn "CHỨNG NHẬN 100% VÔ TRI"
        val stampCenterX = rightColX + 60f
        val stampCenterY = 970f
        val stampRadius = 80f

        val stampPaint = Paint().apply {
            color = redColor
            style = Paint.Style.STROKE
            strokeWidth = 5f
            isAntiAlias = true
        }
        canvas.drawCircle(stampCenterX, stampCenterY, stampRadius, stampPaint)
        canvas.drawCircle(stampCenterX, stampCenterY, stampRadius - 8f, stampPaint)

        val stampTextPaint = Paint().apply {
            color = redColor
            textSize = 19f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        canvas.drawText("CHỨNG NHẬN", stampCenterX, stampCenterY - 20f, stampTextPaint)
        canvas.drawText("100% VÔ TRI", stampCenterX, stampCenterY + 12f, stampTextPaint)
        canvas.drawText("★ ★ ★", stampCenterX, stampCenterY + 42f, stampTextPaint)

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
