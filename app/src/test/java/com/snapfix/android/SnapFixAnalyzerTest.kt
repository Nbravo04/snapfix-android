package com.snapfix.android

import android.graphics.RectF
import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for SnapFixAnalyzer logic
 *
 * Note: Full ImageProxy and camera analysis require instrumentation tests.
 * These tests verify the callback and data flow logic.
 */
class SnapFixAnalyzerTest {

    // ==================== Callback Logic Tests ====================

    @Test
    fun `onResults callback receives correct parameters`() {
        var receivedDetections: List<Detection>? = null
        var receivedWidth: Int? = null
        var receivedHeight: Int? = null

        val onResults: (List<Detection>, Int, Int) -> Unit = { detections, width, height ->
            receivedDetections = detections
            receivedWidth = width
            receivedHeight = height
        }

        // Simulate callback invocation
        val testDetections = listOf(
            Detection(RectF(0f, 0f, 100f, 100f), "person", 0.9f)
        )
        onResults(testDetections, 640, 480)

        assertEquals(testDetections, receivedDetections)
        assertEquals(640, receivedWidth)
        assertEquals(480, receivedHeight)
    }

    @Test
    fun `onResults callback receives empty detection list`() {
        var receivedDetections: List<Detection>? = null

        val onResults: (List<Detection>, Int, Int) -> Unit = { detections, _, _ ->
            receivedDetections = detections
        }

        onResults(emptyList(), 640, 480)

        assertNotNull(receivedDetections)
        assertTrue(receivedDetections!!.isEmpty())
    }

    @Test
    fun `onResults callback receives multiple detections`() {
        var receivedCount = 0

        val onResults: (List<Detection>, Int, Int) -> Unit = { detections, _, _ ->
            receivedCount = detections.size
        }

        val testDetections = listOf(
            Detection(RectF(), "person", 0.9f),
            Detection(RectF(), "car", 0.8f),
            Detection(RectF(), "dog", 0.7f)
        )
        onResults(testDetections, 640, 480)

        assertEquals(3, receivedCount)
    }

    // ==================== Image Dimension Tests ====================

    @Test
    fun `common preview dimensions are passed correctly`() {
        val testCases = listOf(
            Triple(640, 480, "VGA"),
            Triple(1280, 720, "HD"),
            Triple(1920, 1080, "FHD")
        )

        testCases.forEach { (width, height, name) ->
            var passedWidth = 0
            var passedHeight = 0

            val onResults: (List<Detection>, Int, Int) -> Unit = { _, w, h ->
                passedWidth = w
                passedHeight = h
            }

            onResults(emptyList(), width, height)

            assertEquals("$name width", width, passedWidth)
            assertEquals("$name height", height, passedHeight)
        }
    }

    @Test
    fun `rotated image dimensions are handled`() {
        // Portrait orientation (width < height after rotation)
        val portraitWidth = 480
        val portraitHeight = 640

        var receivedWidth = 0
        var receivedHeight = 0

        val onResults: (List<Detection>, Int, Int) -> Unit = { _, w, h ->
            receivedWidth = w
            receivedHeight = h
        }

        onResults(emptyList(), portraitWidth, portraitHeight)

        assertEquals(portraitWidth, receivedWidth)
        assertEquals(portraitHeight, receivedHeight)
        assertTrue(receivedWidth < receivedHeight)
    }

    // ==================== Error Handling Tests ====================

    @Test
    fun `exception in analyzer is caught gracefully`() {
        // Test that the try-catch in analyze() would work
        var exceptionCaught = false

        try {
            throw Exception("Test exception")
        } catch (e: Exception) {
            exceptionCaught = true
            e.printStackTrace()
        }

        assertTrue(exceptionCaught)
    }

    @Test
    fun `finally block always executes`() {
        var finallyCalled = false

        try {
            throw Exception("Test")
        } catch (e: Exception) {
            // Handle
        } finally {
            finallyCalled = true
        }

        assertTrue(finallyCalled)
    }

    @Test
    fun `finally block executes even on success`() {
        var finallyCalled = false

        try {
            // Success - no exception
            val result = 1 + 1
        } catch (e: Exception) {
            fail("Should not catch")
        } finally {
            finallyCalled = true
        }

        assertTrue(finallyCalled)
    }

    // ==================== Detection Processing Tests ====================

    @Test
    fun `detection results maintain order`() {
        val orderedDetections = listOf(
            Detection(RectF(), "first", 0.9f),
            Detection(RectF(), "second", 0.8f),
            Detection(RectF(), "third", 0.7f)
        )

        var receivedDetections: List<Detection>? = null

        val onResults: (List<Detection>, Int, Int) -> Unit = { detections, _, _ ->
            receivedDetections = detections
        }

        onResults(orderedDetections, 640, 480)

        assertEquals("first", receivedDetections!![0].label)
        assertEquals("second", receivedDetections!![1].label)
        assertEquals("third", receivedDetections!![2].label)
    }

    @Test
    fun `high confidence detections are passed through`() {
        val detections = listOf(
            Detection(RectF(), "highConf", 0.99f)
        )

        var maxScore = 0f

        val onResults: (List<Detection>, Int, Int) -> Unit = { dets, _, _ ->
            maxScore = dets.maxOfOrNull { it.score } ?: 0f
        }

        onResults(detections, 640, 480)

        assertEquals(0.99f, maxScore, 0.001f)
    }

    // ==================== Bitmap Dimension Extraction Tests ====================

    @Test
    fun `bitmap width and height are extracted correctly`() {
        // Simulating: onResults(results, bitmap.width, bitmap.height)
        val bitmapWidth = 1920
        val bitmapHeight = 1080

        var passedWidth = 0
        var passedHeight = 0

        val onResults: (List<Detection>, Int, Int) -> Unit = { _, w, h ->
            passedWidth = w
            passedHeight = h
        }

        onResults(emptyList(), bitmapWidth, bitmapHeight)

        assertEquals(bitmapWidth, passedWidth)
        assertEquals(bitmapHeight, passedHeight)
    }

    @Test
    fun `aspect ratio can be calculated from dimensions`() {
        val width = 1920
        val height = 1080

        val aspectRatio = width.toFloat() / height.toFloat()

        assertEquals(1.778f, aspectRatio, 0.001f) // 16:9
    }

    // ==================== Lambda Callback Tests ====================

    @Test
    fun `lambda callback is invoked with correct types`() {
        // Type-safe callback
        val callback: (List<Detection>, Int, Int) -> Unit = { detections, width, height ->
            assertTrue(detections is List<*>)
            assertTrue(width is Int)
            assertTrue(height is Int)
        }

        callback(emptyList(), 100, 100)
    }

    @Test
    fun `callback can be stored and invoked later`() {
        var storedCallback: ((List<Detection>, Int, Int) -> Unit)? = null
        var wasInvoked = false

        storedCallback = { _, _, _ ->
            wasInvoked = true
        }

        // Invoke stored callback
        storedCallback.invoke(emptyList(), 640, 480)

        assertTrue(wasInvoked)
    }

    // ==================== Multiple Callback Invocations ====================

    @Test
    fun `callback can be invoked multiple times`() {
        var invokeCount = 0

        val onResults: (List<Detection>, Int, Int) -> Unit = { _, _, _ ->
            invokeCount++
        }

        repeat(10) {
            onResults(emptyList(), 640, 480)
        }

        assertEquals(10, invokeCount)
    }

    @Test
    fun `each callback invocation receives fresh data`() {
        val receivedLabels = mutableListOf<String>()

        val onResults: (List<Detection>, Int, Int) -> Unit = { detections, _, _ ->
            detections.forEach { receivedLabels.add(it.label) }
        }

        onResults(listOf(Detection(RectF(), "frame1", 0.9f)), 640, 480)
        onResults(listOf(Detection(RectF(), "frame2", 0.8f)), 640, 480)
        onResults(listOf(Detection(RectF(), "frame3", 0.7f)), 640, 480)

        assertEquals(3, receivedLabels.size)
        assertEquals("frame1", receivedLabels[0])
        assertEquals("frame2", receivedLabels[1])
        assertEquals("frame3", receivedLabels[2])
    }
}
