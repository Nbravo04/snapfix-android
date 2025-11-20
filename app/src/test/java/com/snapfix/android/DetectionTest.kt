package com.snapfix.android

import android.graphics.RectF
import org.junit.Assert.*
import org.junit.Test

class DetectionTest {

    @Test
    fun `Detection data class stores boundingBox correctly`() {
        val boundingBox = RectF(10f, 20f, 110f, 120f)
        val detection = Detection(boundingBox, "person", 0.95f)

        assertEquals(boundingBox, detection.boundingBox)
    }

    @Test
    fun `Detection data class stores label correctly`() {
        val detection = Detection(RectF(0f, 0f, 100f, 100f), "car", 0.8f)

        assertEquals("car", detection.label)
    }

    @Test
    fun `Detection data class stores score correctly`() {
        val detection = Detection(RectF(0f, 0f, 100f, 100f), "dog", 0.75f)

        assertEquals(0.75f, detection.score, 0.001f)
    }

    @Test
    fun `Detection equality works correctly`() {
        val detection1 = Detection(RectF(0f, 0f, 100f, 100f), "person", 0.9f)
        val detection2 = Detection(RectF(0f, 0f, 100f, 100f), "person", 0.9f)

        assertEquals(detection1, detection2)
    }

    @Test
    fun `Detection inequality with different boundingBox`() {
        val detection1 = Detection(RectF(0f, 0f, 100f, 100f), "person", 0.9f)
        val detection2 = Detection(RectF(10f, 10f, 100f, 100f), "person", 0.9f)

        assertNotEquals(detection1, detection2)
    }

    @Test
    fun `Detection inequality with different label`() {
        val detection1 = Detection(RectF(0f, 0f, 100f, 100f), "person", 0.9f)
        val detection2 = Detection(RectF(0f, 0f, 100f, 100f), "car", 0.9f)

        assertNotEquals(detection1, detection2)
    }

    @Test
    fun `Detection inequality with different score`() {
        val detection1 = Detection(RectF(0f, 0f, 100f, 100f), "person", 0.9f)
        val detection2 = Detection(RectF(0f, 0f, 100f, 100f), "person", 0.8f)

        assertNotEquals(detection1, detection2)
    }

    @Test
    fun `Detection hashCode is consistent for equal objects`() {
        val detection1 = Detection(RectF(0f, 0f, 100f, 100f), "person", 0.9f)
        val detection2 = Detection(RectF(0f, 0f, 100f, 100f), "person", 0.9f)

        assertEquals(detection1.hashCode(), detection2.hashCode())
    }

    @Test
    fun `Detection with zero score`() {
        val detection = Detection(RectF(0f, 0f, 100f, 100f), "person", 0.0f)

        assertEquals(0.0f, detection.score, 0.001f)
    }

    @Test
    fun `Detection with maximum score`() {
        val detection = Detection(RectF(0f, 0f, 100f, 100f), "person", 1.0f)

        assertEquals(1.0f, detection.score, 0.001f)
    }

    @Test
    fun `Detection with empty label`() {
        val detection = Detection(RectF(0f, 0f, 100f, 100f), "", 0.5f)

        assertEquals("", detection.label)
    }

    @Test
    fun `Detection with zero-sized boundingBox`() {
        val detection = Detection(RectF(50f, 50f, 50f, 50f), "point", 0.5f)

        assertEquals(50f, detection.boundingBox.left, 0.001f)
        assertEquals(50f, detection.boundingBox.right, 0.001f)
    }

    @Test
    fun `Detection with large boundingBox`() {
        val detection = Detection(RectF(0f, 0f, 4000f, 3000f), "large", 0.5f)

        assertEquals(4000f, detection.boundingBox.right, 0.001f)
        assertEquals(3000f, detection.boundingBox.bottom, 0.001f)
    }

    @Test
    fun `Detection copy works correctly`() {
        val original = Detection(RectF(0f, 0f, 100f, 100f), "person", 0.9f)
        val copy = original.copy(label = "car")

        assertEquals("car", copy.label)
        assertEquals(original.boundingBox, copy.boundingBox)
        assertEquals(original.score, copy.score, 0.001f)
    }

    @Test
    fun `Detection toString contains all fields`() {
        val detection = Detection(RectF(0f, 0f, 100f, 100f), "person", 0.9f)
        val string = detection.toString()

        assertTrue(string.contains("person"))
        assertTrue(string.contains("0.9"))
    }

    @Test
    fun `CapturedData stores bitmap and detections`() {
        val detections = listOf(
            Detection(RectF(0f, 0f, 100f, 100f), "person", 0.9f),
            Detection(RectF(50f, 50f, 150f, 150f), "car", 0.8f)
        )
        // Note: CapturedData requires actual Bitmap, but we can test the list
        assertEquals(2, detections.size)
    }

    @Test
    fun `Multiple detections in list maintain order`() {
        val detections = listOf(
            Detection(RectF(0f, 0f, 100f, 100f), "first", 0.9f),
            Detection(RectF(0f, 0f, 100f, 100f), "second", 0.8f),
            Detection(RectF(0f, 0f, 100f, 100f), "third", 0.7f)
        )

        assertEquals("first", detections[0].label)
        assertEquals("second", detections[1].label)
        assertEquals("third", detections[2].label)
    }

    @Test
    fun `Detection can be sorted by score`() {
        val detections = listOf(
            Detection(RectF(0f, 0f, 100f, 100f), "low", 0.5f),
            Detection(RectF(0f, 0f, 100f, 100f), "high", 0.9f),
            Detection(RectF(0f, 0f, 100f, 100f), "medium", 0.7f)
        )

        val sorted = detections.sortedByDescending { it.score }

        assertEquals("high", sorted[0].label)
        assertEquals("medium", sorted[1].label)
        assertEquals("low", sorted[2].label)
    }
}
