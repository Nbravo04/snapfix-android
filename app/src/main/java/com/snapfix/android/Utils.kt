package com.snapfix.android

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ImageDecoder
import android.graphics.Paint
import android.graphics.RectF
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

/**
 * Converts a content URI to a Bitmap
 */
fun Context.uriToBitmap(uri: Uri): Bitmap? {
    return try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(contentResolver, uri)
            ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                decoder.setMutableRequired(true)
                decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
            }
        } else {
            @Suppress("DEPRECATION")
            MediaStore.Images.Media.getBitmap(contentResolver, uri)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

/**
 * Draws bounding boxes and labels on a bitmap
 */
fun Bitmap.drawDetections(detections: List<Detection>): Bitmap {
    val mutableBitmap = this.copy(Bitmap.Config.ARGB_8888, true)
    val canvas = Canvas(mutableBitmap)

    val boxPaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 8f
        isAntiAlias = true
    }

    val textPaint = Paint().apply {
        color = android.graphics.Color.WHITE
        textSize = 40f
        isAntiAlias = true
        style = Paint.Style.FILL
    }

    val textBackgroundPaint = Paint().apply {
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    detections.forEach { detection ->
        // Color-code by confidence (matching live view)
        boxPaint.color = when {
            detection.score >= 0.8f -> android.graphics.Color.GREEN
            detection.score >= 0.6f -> android.graphics.Color.YELLOW
            else -> android.graphics.Color.RED
        }
        textBackgroundPaint.color = boxPaint.color

        // Draw bounding box
        canvas.drawRect(detection.boundingBox, boxPaint)

        // Draw label with background
        val label = "${detection.label} ${(detection.score * 100).toInt()}%"
        val textBounds = android.graphics.Rect()
        textPaint.getTextBounds(label, 0, label.length, textBounds)

        val textX = detection.boundingBox.left
        val textY = detection.boundingBox.top - 10f

        // Draw text background
        canvas.drawRect(
            textX,
            textY - textBounds.height() - 10f,
            textX + textBounds.width() + 20f,
            textY + 5f,
            textBackgroundPaint
        )

        // Draw text
        canvas.drawText(label, textX + 10f, textY, textPaint)
    }

    return mutableBitmap
}

/**
 * Shares a bitmap image
 */
fun Context.shareBitmap(bitmap: Bitmap, title: String = "Share SnapFix Result") {
    try {
        val cachePath = File(cacheDir, "images")
        cachePath.mkdirs()
        val file = File(cachePath, "snapfix_result_${System.currentTimeMillis()}.png")

        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }

        val uri = FileProvider.getUriForFile(
            this,
            "${packageName}.fileprovider",
            file
        )

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        startActivity(Intent.createChooser(shareIntent, title))
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

/**
 * Shares text content
 */
fun Context.shareText(text: String, title: String = "Share") {
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }
    startActivity(Intent.createChooser(shareIntent, title))
}

/**
 * Maps detection labels to Material Icons and helpful descriptions
 */
data class DetectionInfo(
    val iconName: String,
    val helpText: String
)

fun getDetectionInfo(label: String): DetectionInfo {
    return when (label.lowercase()) {
        "person" -> DetectionInfo(
            "person",
            "Person detected — ensure proper safety equipment if working at height or with power tools"
        )
        "bicycle" -> DetectionInfo(
            "pedal_bike",
            "Bicycle detected — check brakes, tire pressure, and chain lubrication regularly"
        )
        "car", "truck", "bus" -> DetectionInfo(
            "directions_car",
            "Vehicle detected — regular maintenance includes oil changes, tire rotation, and brake inspection"
        )
        "motorcycle" -> DetectionInfo(
            "two_wheeler",
            "Motorcycle detected — check tire pressure, chain tension, and brake fluid levels"
        )
        "airplane" -> DetectionInfo(
            "flight",
            "Aircraft detected — professional maintenance required for all repairs"
        )
        "boat" -> DetectionInfo(
            "directions_boat",
            "Boat detected — inspect hull for damage, check engine oil, and test bilge pump"
        )
        "traffic light" -> DetectionInfo(
            "traffic",
            "Traffic light detected — report malfunctions to local authorities"
        )
        "fire hydrant" -> DetectionInfo(
            "local_fire_department",
            "Fire hydrant detected — ensure clear access and report leaks to water department"
        )
        "stop sign" -> DetectionInfo(
            "stop",
            "Stop sign detected — report damaged or obscured signs to local authorities"
        )
        "bench" -> DetectionInfo(
            "deck",
            "Bench detected — check for loose bolts, splinters, and apply weather-resistant sealant"
        )
        "bird" -> DetectionInfo(
            "attractions",
            "Bird detected — if nesting near building, consult wildlife services for safe relocation"
        )
        "cat", "dog" -> DetectionInfo(
            "pets",
            "Pet detected — ensure safe working area, keep pets away from tools and chemicals"
        )
        "horse", "sheep", "cow", "elephant", "bear", "zebra", "giraffe" -> DetectionInfo(
            "pets",
            "Animal detected — maintain safe distance when working near animals"
        )
        "backpack", "suitcase", "handbag" -> DetectionInfo(
            "backpack",
            "Bag detected — repair tears with fabric glue or patches, clean zippers with graphite"
        )
        "umbrella" -> DetectionInfo(
            "umbrella",
            "Umbrella detected — fix broken ribs with fiberglass rods, waterproof fabric with spray"
        )
        "tie" -> DetectionInfo(
            "checkroom",
            "Tie detected — remove stains gently, press on low heat"
        )
        "frisbee", "sports ball" -> DetectionInfo(
            "sports_soccer",
            "Sports equipment detected — check for punctures, maintain proper inflation"
        )
        "skateboard", "surfboard", "snowboard" -> DetectionInfo(
            "skateboarding",
            "Board detected — inspect for cracks, replace worn grip tape, tighten hardware"
        )
        "baseball bat", "baseball glove", "tennis racket" -> DetectionInfo(
            "sports_baseball",
            "Sports gear detected — check for damage, clean and condition leather items"
        )
        "bottle", "wine glass", "cup" -> DetectionInfo(
            "local_cafe",
            "Glassware detected — handle with care, recycle if cracked"
        )
        "fork", "knife", "spoon" -> DetectionInfo(
            "restaurant",
            "Utensils detected — clean thoroughly, sharpen knives regularly for safety"
        )
        "bowl", "banana", "apple", "orange" -> DetectionInfo(
            "restaurant",
            "Food/kitchenware detected — maintain cleanliness to prevent contamination"
        )
        "chair", "couch" -> DetectionInfo(
            "chair",
            "Furniture detected — tighten loose screws, repair wobbly legs, reupholster worn fabric"
        )
        "potted plant", "vase" -> DetectionInfo(
            "local_florist",
            "Plant/vase detected — ensure proper drainage, check for cracks in pottery"
        )
        "bed" -> DetectionInfo(
            "bed",
            "Bed detected — check frame stability, tighten bolts, rotate mattress regularly"
        )
        "dining table" -> DetectionInfo(
            "table_restaurant",
            "Table detected — tighten loose legs, refinish scratched surfaces, protect with coasters"
        )
        "toilet" -> DetectionInfo(
            "wc",
            "Toilet detected — fix leaks immediately, replace worn flapper valve, check water level"
        )
        "tv", "laptop", "monitor" -> DetectionInfo(
            "tv",
            "Electronics detected — clean vents to prevent overheating, check cable connections"
        )
        "keyboard", "mouse" -> DetectionInfo(
            "keyboard",
            "Computer peripheral detected — clean regularly with compressed air and isopropyl alcohol"
        )
        "remote" -> DetectionInfo(
            "settings_remote",
            "Remote detected — clean battery contacts, replace batteries, test all buttons"
        )
        "cell phone" -> DetectionInfo(
            "smartphone",
            "Phone detected — clean charging port, replace cracked screen protector, backup data"
        )
        "microwave", "oven", "toaster" -> DetectionInfo(
            "microwave",
            "Appliance detected — clean regularly, check power cord for damage, test safety features"
        )
        "refrigerator" -> DetectionInfo(
            "kitchen",
            "Refrigerator detected — clean coils quarterly, check door seals, maintain proper temperature"
        )
        "sink" -> DetectionInfo(
            "countertops",
            "Sink detected — fix dripping faucets, clear clogs with plunger, check for leaks under cabinet"
        )
        "book" -> DetectionInfo(
            "menu_book",
            "Book detected — repair torn pages with archival tape, store away from moisture"
        )
        "clock" -> DetectionInfo(
            "schedule",
            "Clock detected — replace batteries, clean glass face, oil mechanical movements"
        )
        "scissors" -> DetectionInfo(
            "content_cut",
            "Scissors detected — sharpen blades, oil pivot point, tighten screw if loose"
        )
        "teddy bear" -> DetectionInfo(
            "cruelty_free",
            "Toy detected — repair tears with matching thread, clean according to material type"
        )
        "hair drier", "toothbrush" -> DetectionInfo(
            "bathroom",
            "Personal care item detected — clean regularly, replace when worn, check electrical safety"
        )
        else -> DetectionInfo(
            "build",
            "$label detected — inspect for damage, clean regularly, and maintain according to manufacturer guidelines"
        )
    }
}
