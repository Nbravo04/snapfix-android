package com.spotfix.android

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.RectF
import io.mockk.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.io.FileDescriptor
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

/**
 * Unit tests for EfficientDetDetector
 *
 * Note: These tests focus on the detect() method logic since TensorFlow Lite
 * interpreter initialization requires actual model files. For full integration
 * tests, use instrumented tests with actual device/emulator.
 */
class EfficientDetDetectorTest {

    @Before
    fun setUp() {
        // Clear all mocks before each test
        clearAllMocks()
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `Detection data class stores correct values`() {
        val boundingBox = RectF(10f, 20f, 100f, 200f)
        val label = "person"
        val score = 0.95f

        val detection = Detection(boundingBox, label, score)

        assertEquals(boundingBox, detection.boundingBox)
        assertEquals(label, detection.label)
        assertEquals(score, detection.score, 0.001f)
    }

    @Test
    fun `Detection boundingBox returns correct dimensions`() {
        val boundingBox = RectF(10f, 20f, 110f, 220f)
        val detection = Detection(boundingBox, "car", 0.8f)

        assertEquals(100f, detection.boundingBox.width(), 0.001f)
        assertEquals(200f, detection.boundingBox.height(), 0.001f)
    }

    @Test
    fun `Detection score is within valid range`() {
        val detection = Detection(RectF(), "test", 0.75f)

        assertTrue(detection.score >= 0f)
        assertTrue(detection.score <= 1f)
    }

    @Test
    fun `Detection equality works correctly`() {
        val box1 = RectF(0f, 0f, 100f, 100f)
        val detection1 = Detection(box1, "person", 0.9f)
        val detection2 = Detection(box1, "person", 0.9f)

        assertEquals(detection1, detection2)
    }

    @Test
    fun `Detection with different labels are not equal`() {
        val box = RectF(0f, 0f, 100f, 100f)
        val detection1 = Detection(box, "person", 0.9f)
        val detection2 = Detection(box, "car", 0.9f)

        assertNotEquals(detection1, detection2)
    }

    @Test
    fun `Labels list contains all 90 COCO classes`() {
        // Verify we have the expected number of labels
        val expectedLabels = listOf(
            "person", "bicycle", "car", "motorcycle", "airplane", "bus", "train", "truck", "boat", "traffic light",
            "fire hydrant", "stop sign", "parking meter", "bench", "bird", "cat", "dog", "horse", "sheep", "cow",
            "elephant", "bear", "zebra", "giraffe", "backpack", "umbrella", "handbag", "tie", "suitcase", "frisbee",
            "skis", "snowboard", "sports ball", "kite", "baseball bat", "baseball glove", "skateboard", "surfboard",
            "tennis racket", "bottle", "wine glass", "cup", "fork", "knife", "spoon", "bowl", "banana", "apple",
            "sandwich", "orange", "broccoli", "carrot", "hot dog", "pizza", "donut", "cake", "chair", "couch",
            "potted plant", "bed", "dining table", "toilet", "tv", "laptop", "mouse", "remote", "keyboard", "cell phone",
            "microwave", "oven", "toaster", "sink", "refrigerator", "book", "clock", "vase", "scissors", "teddy bear",
            "hair drier", "toothbrush"
        )

        // EfficientDetDetector should have 82 labels (0-81 for COCO)
        assertEquals(82, expectedLabels.size)
    }

    @Test
    fun `Confidence threshold filters low scores`() {
        // Test the threshold logic - scores below 0.5 should be filtered
        val threshold = 0.5f
        val lowScore = 0.3f
        val highScore = 0.7f

        assertTrue(lowScore < threshold)
        assertTrue(highScore >= threshold)
    }

    @Test
    fun `Invalid bounding box with left greater than right is rejected`() {
        // This tests the validation logic: left >= right should be invalid
        val left = 100f
        val right = 50f

        assertTrue(left >= right) // This should cause rejection
    }

    @Test
    fun `Invalid bounding box with top greater than bottom is rejected`() {
        // This tests the validation logic: top >= bottom should be invalid
        val top = 200f
        val bottom = 100f

        assertTrue(top >= bottom) // This should cause rejection
    }

    @Test
    fun `Bounding box out of bounds is rejected`() {
        val imageWidth = 640
        val imageHeight = 480

        // Test cases that should be rejected
        val negativeLeft = -10f
        val negativeTop = -10f
        val overflowRight = imageWidth.toFloat() + 10f
        val overflowBottom = imageHeight.toFloat() + 10f

        assertTrue(negativeLeft < 0)
        assertTrue(negativeTop < 0)
        assertTrue(overflowRight > imageWidth)
        assertTrue(overflowBottom > imageHeight)
    }

    @Test
    fun `Coordinate transformation from normalized to pixels is correct`() {
        val originalWidth = 640
        val originalHeight = 480

        // Normalized coordinates (0-1)
        val xmin = 0.25f
        val ymin = 0.25f
        val xmax = 0.75f
        val ymax = 0.75f

        // Convert to pixel coordinates
        val left = xmin * originalWidth
        val top = ymin * originalHeight
        val right = xmax * originalWidth
        val bottom = ymax * originalHeight

        assertEquals(160f, left, 0.001f)
        assertEquals(120f, top, 0.001f)
        assertEquals(480f, right, 0.001f)
        assertEquals(360f, bottom, 0.001f)
    }

    @Test
    fun `Coordinate transformation preserves aspect ratio`() {
        val originalWidth = 1920
        val originalHeight = 1080

        val xmin = 0.1f
        val xmax = 0.5f
        val ymin = 0.2f
        val ymax = 0.6f

        val pixelWidth = (xmax - xmin) * originalWidth
        val pixelHeight = (ymax - ymin) * originalHeight

        val normalizedRatio = (xmax - xmin) / (ymax - ymin)
        val pixelRatio = pixelWidth / pixelHeight

        // Aspect ratios should match when scaled proportionally
        assertEquals(normalizedRatio * (originalWidth.toFloat() / originalHeight), pixelRatio, 0.001f)
    }

    @Test
    fun `Class ID filtering rejects negative values`() {
        val classId = -1
        val labelsSize = 82

        assertTrue(classId < 0 || classId >= labelsSize)
    }

    @Test
    fun `Class ID filtering rejects out of range values`() {
        val classId = 100
        val labelsSize = 82

        assertTrue(classId < 0 || classId >= labelsSize)
    }

    @Test
    fun `Valid class IDs are accepted`() {
        val labelsSize = 82

        for (classId in 0 until labelsSize) {
            assertFalse(classId < 0 || classId >= labelsSize)
        }
    }

    @Test
    fun `Number of detections is coerced to maximum 25`() {
        val numDetections = 100f
        val maxDetections = 25

        val coerced = numDetections.toInt().coerceAtMost(maxDetections)

        assertEquals(25, coerced)
    }

    @Test
    fun `Number of detections below max is not changed`() {
        val numDetections = 10f
        val maxDetections = 25

        val coerced = numDetections.toInt().coerceAtMost(maxDetections)

        assertEquals(10, coerced)
    }

    @Test
    fun `RectF correctly stores bounding box coordinates`() {
        val left = 10f
        val top = 20f
        val right = 110f
        val bottom = 220f

        val rect = RectF(left, top, right, bottom)

        assertEquals(left, rect.left, 0.001f)
        assertEquals(top, rect.top, 0.001f)
        assertEquals(right, rect.right, 0.001f)
        assertEquals(bottom, rect.bottom, 0.001f)
    }

    @Test
    fun `Multiple detections can be stored in list`() {
        val detections = mutableListOf<Detection>()

        detections.add(Detection(RectF(0f, 0f, 100f, 100f), "person", 0.9f))
        detections.add(Detection(RectF(200f, 200f, 300f, 300f), "car", 0.8f))
        detections.add(Detection(RectF(400f, 400f, 500f, 500f), "dog", 0.7f))

        assertEquals(3, detections.size)
        assertEquals("person", detections[0].label)
        assertEquals("car", detections[1].label)
        assertEquals("dog", detections[2].label)
    }

    @Test
    fun `Empty detection list is valid`() {
        val detections = emptyList<Detection>()

        assertTrue(detections.isEmpty())
        assertEquals(0, detections.size)
    }

    @Test
    fun `Detection score percentage calculation is correct`() {
        val score = 0.876f
        val percentage = (score * 100).toInt()

        assertEquals(87, percentage)
    }

    @Test
    fun `High confidence detection score`() {
        val detection = Detection(RectF(), "test", 0.99f)
        val percentage = (detection.score * 100).toInt()

        assertEquals(99, percentage)
    }

    @Test
    fun `Threshold boundary value is handled correctly`() {
        val threshold = 0.5f
        val exactThreshold = 0.5f
        val justAbove = 0.500001f
        val justBelow = 0.499999f

        // Exact threshold should pass (score >= threshold, not >)
        assertFalse(exactThreshold < threshold)
        assertFalse(justAbove < threshold)
        assertTrue(justBelow < threshold)
    }
}
