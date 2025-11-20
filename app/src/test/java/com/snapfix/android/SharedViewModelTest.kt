package com.snapfix.android

import android.graphics.Bitmap
import android.graphics.RectF
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SharedViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: SharedViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = SharedViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should be null`() {
        assertNull(viewModel.capturedData.value)
    }

    @Test
    fun `setCapturedData should update state`() {
        val mockBitmap = mockk<Bitmap>()
        val detections = listOf(
            Detection(RectF(0f, 0f, 100f, 100f), "person", 0.9f)
        )

        viewModel.setCapturedData(mockBitmap, detections)

        assertNotNull(viewModel.capturedData.value)
        assertEquals(mockBitmap, viewModel.capturedData.value?.bitmap)
        assertEquals(detections, viewModel.capturedData.value?.detections)
    }

    @Test
    fun `setCapturedData should update with empty detections`() {
        val mockBitmap = mockk<Bitmap>()
        val emptyDetections = emptyList<Detection>()

        viewModel.setCapturedData(mockBitmap, emptyDetections)

        assertNotNull(viewModel.capturedData.value)
        assertEquals(mockBitmap, viewModel.capturedData.value?.bitmap)
        assertTrue(viewModel.capturedData.value?.detections?.isEmpty() == true)
    }

    @Test
    fun `setCapturedData should update with multiple detections`() {
        val mockBitmap = mockk<Bitmap>()
        val detections = listOf(
            Detection(RectF(0f, 0f, 100f, 100f), "person", 0.9f),
            Detection(RectF(50f, 50f, 150f, 150f), "car", 0.8f),
            Detection(RectF(100f, 100f, 200f, 200f), "dog", 0.7f)
        )

        viewModel.setCapturedData(mockBitmap, detections)

        assertEquals(3, viewModel.capturedData.value?.detections?.size)
    }

    @Test
    fun `clearCapturedData should set state to null`() {
        val mockBitmap = mockk<Bitmap>()
        val detections = listOf(
            Detection(RectF(0f, 0f, 100f, 100f), "person", 0.9f)
        )

        viewModel.setCapturedData(mockBitmap, detections)
        assertNotNull(viewModel.capturedData.value)

        viewModel.clearCapturedData()
        assertNull(viewModel.capturedData.value)
    }

    @Test
    fun `multiple setCapturedData calls should keep latest data`() {
        val mockBitmap1 = mockk<Bitmap>()
        val mockBitmap2 = mockk<Bitmap>()
        val detections1 = listOf(Detection(RectF(0f, 0f, 100f, 100f), "person", 0.9f))
        val detections2 = listOf(Detection(RectF(50f, 50f, 150f, 150f), "car", 0.8f))

        viewModel.setCapturedData(mockBitmap1, detections1)
        viewModel.setCapturedData(mockBitmap2, detections2)

        assertEquals(mockBitmap2, viewModel.capturedData.value?.bitmap)
        assertEquals(detections2, viewModel.capturedData.value?.detections)
    }

    @Test
    fun `clearCapturedData on already null state should remain null`() {
        assertNull(viewModel.capturedData.value)
        viewModel.clearCapturedData()
        assertNull(viewModel.capturedData.value)
    }

    @Test
    fun `setCapturedData after clear should work correctly`() {
        val mockBitmap = mockk<Bitmap>()
        val detections = listOf(Detection(RectF(0f, 0f, 100f, 100f), "person", 0.9f))

        viewModel.setCapturedData(mockBitmap, detections)
        viewModel.clearCapturedData()
        viewModel.setCapturedData(mockBitmap, detections)

        assertNotNull(viewModel.capturedData.value)
        assertEquals(mockBitmap, viewModel.capturedData.value?.bitmap)
    }
}
