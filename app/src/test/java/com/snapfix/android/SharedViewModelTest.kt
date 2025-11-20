package com.snapfix.android

import android.graphics.Bitmap
import android.graphics.RectF
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import io.mockk.mockk

/**
 * Unit tests for SharedViewModel
 */
@OptIn(ExperimentalCoroutinesApi::class)
class SharedViewModelTest {

    private lateinit var viewModel: SharedViewModel

    @Before
    fun setUp() {
        viewModel = SharedViewModel()
    }

    // ==================== Initial State Tests ====================

    @Test
    fun `initial capturedData is null`() = runTest {
        val initialValue = viewModel.capturedData.first()

        assertNull(initialValue)
    }

    @Test
    fun `viewModel initializes without error`() {
        assertNotNull(viewModel)
        assertNotNull(viewModel.capturedData)
    }

    // ==================== setCapturedData Tests ====================

    @Test
    fun `setCapturedData updates state with bitmap and detections`() = runTest {
        val mockBitmap = mockk<Bitmap>(relaxed = true)
        val detections = listOf(
            Detection(RectF(0f, 0f, 100f, 100f), "person", 0.9f)
        )

        viewModel.setCapturedData(mockBitmap, detections)

        val result = viewModel.capturedData.first()

        assertNotNull(result)
        assertEquals(mockBitmap, result?.bitmap)
        assertEquals(detections, result?.detections)
    }

    @Test
    fun `setCapturedData updates state with empty detections`() = runTest {
        val mockBitmap = mockk<Bitmap>(relaxed = true)
        val emptyDetections = emptyList<Detection>()

        viewModel.setCapturedData(mockBitmap, emptyDetections)

        val result = viewModel.capturedData.first()

        assertNotNull(result)
        assertTrue(result?.detections?.isEmpty() == true)
    }

    @Test
    fun `setCapturedData replaces previous data`() = runTest {
        val mockBitmap1 = mockk<Bitmap>(relaxed = true)
        val mockBitmap2 = mockk<Bitmap>(relaxed = true)
        val detections1 = listOf(Detection(RectF(), "person", 0.9f))
        val detections2 = listOf(Detection(RectF(), "car", 0.8f))

        viewModel.setCapturedData(mockBitmap1, detections1)
        viewModel.setCapturedData(mockBitmap2, detections2)

        val result = viewModel.capturedData.first()

        assertEquals(mockBitmap2, result?.bitmap)
        assertEquals(detections2, result?.detections)
    }

    @Test
    fun `setCapturedData with multiple detections`() = runTest {
        val mockBitmap = mockk<Bitmap>(relaxed = true)
        val detections = listOf(
            Detection(RectF(0f, 0f, 100f, 100f), "person", 0.95f),
            Detection(RectF(200f, 200f, 300f, 300f), "car", 0.87f),
            Detection(RectF(400f, 400f, 500f, 500f), "dog", 0.76f)
        )

        viewModel.setCapturedData(mockBitmap, detections)

        val result = viewModel.capturedData.first()

        assertEquals(3, result?.detections?.size)
    }

    // ==================== clearCapturedData Tests ====================

    @Test
    fun `clearCapturedData sets state to null`() = runTest {
        val mockBitmap = mockk<Bitmap>(relaxed = true)
        val detections = listOf(Detection(RectF(), "person", 0.9f))

        viewModel.setCapturedData(mockBitmap, detections)
        viewModel.clearCapturedData()

        val result = viewModel.capturedData.first()

        assertNull(result)
    }

    @Test
    fun `clearCapturedData on already null state is safe`() = runTest {
        viewModel.clearCapturedData()

        val result = viewModel.capturedData.first()

        assertNull(result)
    }

    @Test
    fun `clearCapturedData can be called multiple times`() = runTest {
        viewModel.clearCapturedData()
        viewModel.clearCapturedData()
        viewModel.clearCapturedData()

        val result = viewModel.capturedData.first()

        assertNull(result)
    }

    // ==================== StateFlow Behavior Tests ====================

    @Test
    fun `capturedData is observable StateFlow`() {
        assertNotNull(viewModel.capturedData)
    }

    @Test
    fun `multiple updates emit latest value`() = runTest {
        val mockBitmap = mockk<Bitmap>(relaxed = true)

        for (i in 1..5) {
            val detections = listOf(Detection(RectF(), "item$i", 0.9f))
            viewModel.setCapturedData(mockBitmap, detections)
        }

        val result = viewModel.capturedData.first()

        assertEquals("item5", result?.detections?.first()?.label)
    }

    // ==================== CapturedData Class Tests ====================

    @Test
    fun `CapturedData stores bitmap and detections correctly`() {
        val mockBitmap = mockk<Bitmap>(relaxed = true)
        val detections = listOf(
            Detection(RectF(), "person", 0.9f)
        )

        val capturedData = CapturedData(mockBitmap, detections)

        assertEquals(mockBitmap, capturedData.bitmap)
        assertEquals(detections, capturedData.detections)
    }

    @Test
    fun `CapturedData equality works correctly`() {
        val mockBitmap = mockk<Bitmap>(relaxed = true)
        val detections = listOf(Detection(RectF(), "person", 0.9f))

        val data1 = CapturedData(mockBitmap, detections)
        val data2 = CapturedData(mockBitmap, detections)

        assertEquals(data1, data2)
    }

    @Test
    fun `CapturedData with different detections are not equal`() {
        val mockBitmap = mockk<Bitmap>(relaxed = true)
        val detections1 = listOf(Detection(RectF(), "person", 0.9f))
        val detections2 = listOf(Detection(RectF(), "car", 0.9f))

        val data1 = CapturedData(mockBitmap, detections1)
        val data2 = CapturedData(mockBitmap, detections2)

        assertNotEquals(data1, data2)
    }

    @Test
    fun `CapturedData with empty detections is valid`() {
        val mockBitmap = mockk<Bitmap>(relaxed = true)
        val data = CapturedData(mockBitmap, emptyList())

        assertNotNull(data)
        assertTrue(data.detections.isEmpty())
    }

    // ==================== Detection Sorting Tests ====================

    @Test
    fun `detections can be sorted by confidence`() {
        val detections = listOf(
            Detection(RectF(), "low", 0.5f),
            Detection(RectF(), "high", 0.95f),
            Detection(RectF(), "medium", 0.75f)
        )

        val sorted = detections.sortedByDescending { it.score }

        assertEquals("high", sorted[0].label)
        assertEquals("medium", sorted[1].label)
        assertEquals("low", sorted[2].label)
    }

    @Test
    fun `top 5 detections can be extracted`() {
        val detections = (1..10).map { i ->
            Detection(RectF(), "item$i", i / 10f)
        }

        val top5 = detections.sortedByDescending { it.score }.take(5)

        assertEquals(5, top5.size)
        assertEquals("item10", top5[0].label)
    }
}
