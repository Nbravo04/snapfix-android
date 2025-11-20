package com.snapfix.android

import android.graphics.Bitmap
import android.graphics.Matrix
import io.mockk.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class BitmapExtensionsTest {

    @Before
    fun setup() {
        // Mock static Bitmap methods
        mockkStatic(Bitmap::class)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `rotate with 0 degrees returns same bitmap`() {
        val mockBitmap = mockk<Bitmap>()

        val result = mockBitmap.rotate(0f)

        assertSame(mockBitmap, result)
    }

    @Test
    fun `rotate with non-zero degrees creates rotated bitmap`() {
        val mockBitmap = mockk<Bitmap>()
        val resultBitmap = mockk<Bitmap>()

        every { mockBitmap.width } returns 100
        every { mockBitmap.height } returns 200
        every {
            Bitmap.createBitmap(mockBitmap, 0, 0, 100, 200, any<Matrix>(), true)
        } returns resultBitmap

        val result = mockBitmap.rotate(90f)

        assertEquals(resultBitmap, result)
        verify {
            Bitmap.createBitmap(mockBitmap, 0, 0, 100, 200, any<Matrix>(), true)
        }
    }

    @Test
    fun `rotate with 180 degrees calls createBitmap`() {
        val mockBitmap = mockk<Bitmap>()
        val resultBitmap = mockk<Bitmap>()

        every { mockBitmap.width } returns 640
        every { mockBitmap.height } returns 480
        every {
            Bitmap.createBitmap(mockBitmap, 0, 0, 640, 480, any<Matrix>(), true)
        } returns resultBitmap

        val result = mockBitmap.rotate(180f)

        assertEquals(resultBitmap, result)
    }

    @Test
    fun `rotate with 270 degrees calls createBitmap`() {
        val mockBitmap = mockk<Bitmap>()
        val resultBitmap = mockk<Bitmap>()

        every { mockBitmap.width } returns 1920
        every { mockBitmap.height } returns 1080
        every {
            Bitmap.createBitmap(mockBitmap, 0, 0, 1920, 1080, any<Matrix>(), true)
        } returns resultBitmap

        val result = mockBitmap.rotate(270f)

        assertEquals(resultBitmap, result)
    }

    @Test
    fun `rotate with negative degrees calls createBitmap`() {
        val mockBitmap = mockk<Bitmap>()
        val resultBitmap = mockk<Bitmap>()

        every { mockBitmap.width } returns 100
        every { mockBitmap.height } returns 100
        every {
            Bitmap.createBitmap(mockBitmap, 0, 0, 100, 100, any<Matrix>(), true)
        } returns resultBitmap

        val result = mockBitmap.rotate(-90f)

        assertEquals(resultBitmap, result)
    }

    @Test
    fun `rotate with small angle calls createBitmap`() {
        val mockBitmap = mockk<Bitmap>()
        val resultBitmap = mockk<Bitmap>()

        every { mockBitmap.width } returns 100
        every { mockBitmap.height } returns 100
        every {
            Bitmap.createBitmap(mockBitmap, 0, 0, 100, 100, any<Matrix>(), true)
        } returns resultBitmap

        val result = mockBitmap.rotate(45f)

        assertEquals(resultBitmap, result)
    }
}
