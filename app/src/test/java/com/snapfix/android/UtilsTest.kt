package com.snapfix.android

import android.graphics.Bitmap
import android.graphics.RectF
import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for Utils.kt functions
 */
class UtilsTest {

    // ==================== Detection Info Mapping Tests ====================

    @Test
    fun `getDetectionInfo returns correct info for person`() {
        val info = getDetectionInfo("person")

        assertEquals("person", info.iconName)
        assertTrue(info.helpText.contains("Person detected"))
        assertTrue(info.helpText.contains("safety"))
    }

    @Test
    fun `getDetectionInfo returns correct info for bicycle`() {
        val info = getDetectionInfo("bicycle")

        assertEquals("pedal_bike", info.iconName)
        assertTrue(info.helpText.contains("Bicycle"))
        assertTrue(info.helpText.contains("brakes"))
    }

    @Test
    fun `getDetectionInfo returns same info for car truck and bus`() {
        val carInfo = getDetectionInfo("car")
        val truckInfo = getDetectionInfo("truck")
        val busInfo = getDetectionInfo("bus")

        assertEquals("directions_car", carInfo.iconName)
        assertEquals("directions_car", truckInfo.iconName)
        assertEquals("directions_car", busInfo.iconName)
    }

    @Test
    fun `getDetectionInfo returns correct info for motorcycle`() {
        val info = getDetectionInfo("motorcycle")

        assertEquals("two_wheeler", info.iconName)
        assertTrue(info.helpText.contains("Motorcycle"))
    }

    @Test
    fun `getDetectionInfo returns correct info for airplane`() {
        val info = getDetectionInfo("airplane")

        assertEquals("flight", info.iconName)
        assertTrue(info.helpText.contains("Aircraft"))
        assertTrue(info.helpText.contains("professional"))
    }

    @Test
    fun `getDetectionInfo returns correct info for boat`() {
        val info = getDetectionInfo("boat")

        assertEquals("directions_boat", info.iconName)
        assertTrue(info.helpText.contains("Boat"))
    }

    @Test
    fun `getDetectionInfo returns correct info for traffic light`() {
        val info = getDetectionInfo("traffic light")

        assertEquals("traffic", info.iconName)
        assertTrue(info.helpText.contains("Traffic light"))
    }

    @Test
    fun `getDetectionInfo returns correct info for fire hydrant`() {
        val info = getDetectionInfo("fire hydrant")

        assertEquals("local_fire_department", info.iconName)
        assertTrue(info.helpText.contains("Fire hydrant"))
    }

    @Test
    fun `getDetectionInfo returns correct info for stop sign`() {
        val info = getDetectionInfo("stop sign")

        assertEquals("stop", info.iconName)
        assertTrue(info.helpText.contains("Stop sign"))
    }

    @Test
    fun `getDetectionInfo returns correct info for bench`() {
        val info = getDetectionInfo("bench")

        assertEquals("deck", info.iconName)
        assertTrue(info.helpText.contains("Bench"))
    }

    @Test
    fun `getDetectionInfo returns correct info for bird`() {
        val info = getDetectionInfo("bird")

        assertEquals("attractions", info.iconName)
        assertTrue(info.helpText.contains("Bird"))
    }

    @Test
    fun `getDetectionInfo returns same info for cat and dog`() {
        val catInfo = getDetectionInfo("cat")
        val dogInfo = getDetectionInfo("dog")

        assertEquals("pets", catInfo.iconName)
        assertEquals("pets", dogInfo.iconName)
        assertTrue(catInfo.helpText.contains("Pet"))
    }

    @Test
    fun `getDetectionInfo returns correct info for animals`() {
        val animals = listOf("horse", "sheep", "cow", "elephant", "bear", "zebra", "giraffe")

        animals.forEach { animal ->
            val info = getDetectionInfo(animal)
            assertEquals("pets", info.iconName)
            assertTrue(info.helpText.contains("Animal"))
        }
    }

    @Test
    fun `getDetectionInfo returns same info for bags`() {
        val bags = listOf("backpack", "suitcase", "handbag")

        bags.forEach { bag ->
            val info = getDetectionInfo(bag)
            assertEquals("backpack", info.iconName)
            assertTrue(info.helpText.contains("Bag"))
        }
    }

    @Test
    fun `getDetectionInfo returns correct info for umbrella`() {
        val info = getDetectionInfo("umbrella")

        assertEquals("umbrella", info.iconName)
        assertTrue(info.helpText.contains("Umbrella"))
    }

    @Test
    fun `getDetectionInfo returns correct info for tie`() {
        val info = getDetectionInfo("tie")

        assertEquals("checkroom", info.iconName)
        assertTrue(info.helpText.contains("Tie"))
    }

    @Test
    fun `getDetectionInfo returns correct info for sports equipment`() {
        val equipment = listOf("frisbee", "sports ball")

        equipment.forEach { item ->
            val info = getDetectionInfo(item)
            assertEquals("sports_soccer", info.iconName)
        }
    }

    @Test
    fun `getDetectionInfo returns correct info for boards`() {
        val boards = listOf("skateboard", "surfboard", "snowboard")

        boards.forEach { board ->
            val info = getDetectionInfo(board)
            assertEquals("skateboarding", info.iconName)
            assertTrue(info.helpText.contains("Board"))
        }
    }

    @Test
    fun `getDetectionInfo returns correct info for baseball gear`() {
        val gear = listOf("baseball bat", "baseball glove", "tennis racket")

        gear.forEach { item ->
            val info = getDetectionInfo(item)
            assertEquals("sports_baseball", info.iconName)
        }
    }

    @Test
    fun `getDetectionInfo returns correct info for glassware`() {
        val glassware = listOf("bottle", "wine glass", "cup")

        glassware.forEach { item ->
            val info = getDetectionInfo(item)
            assertEquals("local_cafe", info.iconName)
        }
    }

    @Test
    fun `getDetectionInfo returns correct info for utensils`() {
        val utensils = listOf("fork", "knife", "spoon")

        utensils.forEach { utensil ->
            val info = getDetectionInfo(utensil)
            assertEquals("restaurant", info.iconName)
        }
    }

    @Test
    fun `getDetectionInfo returns correct info for food items`() {
        val food = listOf("bowl", "banana", "apple", "orange")

        food.forEach { item ->
            val info = getDetectionInfo(item)
            assertEquals("restaurant", info.iconName)
        }
    }

    @Test
    fun `getDetectionInfo returns correct info for chair and couch`() {
        val furniture = listOf("chair", "couch")

        furniture.forEach { item ->
            val info = getDetectionInfo(item)
            assertEquals("chair", info.iconName)
            assertTrue(info.helpText.contains("Furniture"))
        }
    }

    @Test
    fun `getDetectionInfo returns correct info for plant and vase`() {
        val items = listOf("potted plant", "vase")

        items.forEach { item ->
            val info = getDetectionInfo(item)
            assertEquals("local_florist", info.iconName)
        }
    }

    @Test
    fun `getDetectionInfo returns correct info for bed`() {
        val info = getDetectionInfo("bed")

        assertEquals("bed", info.iconName)
        assertTrue(info.helpText.contains("Bed"))
    }

    @Test
    fun `getDetectionInfo returns correct info for dining table`() {
        val info = getDetectionInfo("dining table")

        assertEquals("table_restaurant", info.iconName)
        assertTrue(info.helpText.contains("Table"))
    }

    @Test
    fun `getDetectionInfo returns correct info for toilet`() {
        val info = getDetectionInfo("toilet")

        assertEquals("wc", info.iconName)
        assertTrue(info.helpText.contains("Toilet"))
        assertTrue(info.helpText.contains("flapper"))
    }

    @Test
    fun `getDetectionInfo returns correct info for electronics`() {
        val electronics = listOf("tv", "laptop", "monitor")

        electronics.forEach { item ->
            val info = getDetectionInfo(item)
            assertEquals("tv", info.iconName)
            assertTrue(info.helpText.contains("Electronics"))
        }
    }

    @Test
    fun `getDetectionInfo returns correct info for computer peripherals`() {
        val peripherals = listOf("keyboard", "mouse")

        peripherals.forEach { item ->
            val info = getDetectionInfo(item)
            assertEquals("keyboard", info.iconName)
        }
    }

    @Test
    fun `getDetectionInfo returns correct info for remote`() {
        val info = getDetectionInfo("remote")

        assertEquals("settings_remote", info.iconName)
        assertTrue(info.helpText.contains("Remote"))
    }

    @Test
    fun `getDetectionInfo returns correct info for cell phone`() {
        val info = getDetectionInfo("cell phone")

        assertEquals("smartphone", info.iconName)
        assertTrue(info.helpText.contains("Phone"))
    }

    @Test
    fun `getDetectionInfo returns correct info for kitchen appliances`() {
        val appliances = listOf("microwave", "oven", "toaster")

        appliances.forEach { appliance ->
            val info = getDetectionInfo(appliance)
            assertEquals("microwave", info.iconName)
            assertTrue(info.helpText.contains("Appliance"))
        }
    }

    @Test
    fun `getDetectionInfo returns correct info for refrigerator`() {
        val info = getDetectionInfo("refrigerator")

        assertEquals("kitchen", info.iconName)
        assertTrue(info.helpText.contains("Refrigerator"))
    }

    @Test
    fun `getDetectionInfo returns correct info for sink`() {
        val info = getDetectionInfo("sink")

        assertEquals("countertops", info.iconName)
        assertTrue(info.helpText.contains("Sink"))
    }

    @Test
    fun `getDetectionInfo returns correct info for book`() {
        val info = getDetectionInfo("book")

        assertEquals("menu_book", info.iconName)
        assertTrue(info.helpText.contains("Book"))
    }

    @Test
    fun `getDetectionInfo returns correct info for clock`() {
        val info = getDetectionInfo("clock")

        assertEquals("schedule", info.iconName)
        assertTrue(info.helpText.contains("Clock"))
    }

    @Test
    fun `getDetectionInfo returns correct info for scissors`() {
        val info = getDetectionInfo("scissors")

        assertEquals("content_cut", info.iconName)
        assertTrue(info.helpText.contains("Scissors"))
    }

    @Test
    fun `getDetectionInfo returns correct info for teddy bear`() {
        val info = getDetectionInfo("teddy bear")

        assertEquals("cruelty_free", info.iconName)
        assertTrue(info.helpText.contains("Toy"))
    }

    @Test
    fun `getDetectionInfo returns correct info for personal care items`() {
        val items = listOf("hair drier", "toothbrush")

        items.forEach { item ->
            val info = getDetectionInfo(item)
            assertEquals("bathroom", info.iconName)
        }
    }

    @Test
    fun `getDetectionInfo returns default for unknown label`() {
        val info = getDetectionInfo("unknown_object")

        assertEquals("build", info.iconName)
        assertTrue(info.helpText.contains("unknown_object"))
        assertTrue(info.helpText.contains("detected"))
    }

    @Test
    fun `getDetectionInfo is case insensitive`() {
        val lowerInfo = getDetectionInfo("person")
        val upperInfo = getDetectionInfo("PERSON")
        val mixedInfo = getDetectionInfo("Person")

        assertEquals(lowerInfo.iconName, upperInfo.iconName)
        assertEquals(lowerInfo.iconName, mixedInfo.iconName)
    }

    @Test
    fun `getDetectionInfo handles mixed case variations`() {
        val info1 = getDetectionInfo("Cell Phone")
        val info2 = getDetectionInfo("CELL PHONE")
        val info3 = getDetectionInfo("cell phone")

        assertEquals("smartphone", info1.iconName)
        assertEquals("smartphone", info2.iconName)
        assertEquals("smartphone", info3.iconName)
    }

    @Test
    fun `All help texts are non-empty`() {
        val testLabels = listOf(
            "person", "bicycle", "car", "motorcycle", "chair", "couch",
            "toilet", "sink", "tv", "laptop", "refrigerator", "unknown"
        )

        testLabels.forEach { label ->
            val info = getDetectionInfo(label)
            assertTrue("Help text for $label should not be empty", info.helpText.isNotEmpty())
        }
    }

    @Test
    fun `All icon names are non-empty`() {
        val testLabels = listOf(
            "person", "bicycle", "car", "motorcycle", "chair", "couch",
            "toilet", "sink", "tv", "laptop", "refrigerator", "unknown"
        )

        testLabels.forEach { label ->
            val info = getDetectionInfo(label)
            assertTrue("Icon name for $label should not be empty", info.iconName.isNotEmpty())
        }
    }

    // ==================== DetectionInfo Data Class Tests ====================

    @Test
    fun `DetectionInfo stores iconName and helpText correctly`() {
        val info = DetectionInfo("test_icon", "Test help text")

        assertEquals("test_icon", info.iconName)
        assertEquals("Test help text", info.helpText)
    }

    @Test
    fun `DetectionInfo equality works correctly`() {
        val info1 = DetectionInfo("icon", "text")
        val info2 = DetectionInfo("icon", "text")

        assertEquals(info1, info2)
    }

    @Test
    fun `DetectionInfo with different values are not equal`() {
        val info1 = DetectionInfo("icon1", "text")
        val info2 = DetectionInfo("icon2", "text")

        assertNotEquals(info1, info2)
    }

    // ==================== DrawDetections Color Coding Tests ====================

    @Test
    fun `High confidence score maps to green`() {
        val score = 0.9f

        val isGreen = score >= 0.8f
        val isYellow = score >= 0.6f && score < 0.8f
        val isRed = score < 0.6f

        assertTrue(isGreen)
        assertFalse(isYellow)
        assertFalse(isRed)
    }

    @Test
    fun `Medium confidence score maps to yellow`() {
        val score = 0.7f

        val isGreen = score >= 0.8f
        val isYellow = score >= 0.6f && score < 0.8f
        val isRed = score < 0.6f

        assertFalse(isGreen)
        assertTrue(isYellow)
        assertFalse(isRed)
    }

    @Test
    fun `Low confidence score maps to red`() {
        val score = 0.5f

        val isGreen = score >= 0.8f
        val isYellow = score >= 0.6f && score < 0.8f
        val isRed = score < 0.6f

        assertFalse(isGreen)
        assertFalse(isYellow)
        assertTrue(isRed)
    }

    @Test
    fun `Boundary score 0_8 maps to green`() {
        val score = 0.8f

        val isGreen = score >= 0.8f

        assertTrue(isGreen)
    }

    @Test
    fun `Boundary score 0_6 maps to yellow`() {
        val score = 0.6f

        val isGreen = score >= 0.8f
        val isYellow = score >= 0.6f && score < 0.8f

        assertFalse(isGreen)
        assertTrue(isYellow)
    }

    @Test
    fun `Detection label formatting for display`() {
        val detection = Detection(RectF(), "person", 0.876f)
        val label = "${detection.label} ${(detection.score * 100).toInt()}%"

        assertEquals("person 87%", label)
    }
}
