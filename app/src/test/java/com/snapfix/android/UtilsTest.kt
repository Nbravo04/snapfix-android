package com.snapfix.android

import org.junit.Assert.*
import org.junit.Test

class UtilsTest {

    // Tests for getDetectionInfo function

    @Test
    fun `getDetectionInfo returns correct info for person`() {
        val info = getDetectionInfo("person")
        assertEquals("person", info.iconName)
        assertTrue(info.helpText.contains("safety"))
    }

    @Test
    fun `getDetectionInfo is case insensitive`() {
        val infoLower = getDetectionInfo("person")
        val infoUpper = getDetectionInfo("PERSON")
        val infoMixed = getDetectionInfo("Person")

        assertEquals(infoLower.iconName, infoUpper.iconName)
        assertEquals(infoLower.iconName, infoMixed.iconName)
    }

    @Test
    fun `getDetectionInfo returns correct info for bicycle`() {
        val info = getDetectionInfo("bicycle")
        assertEquals("pedal_bike", info.iconName)
        assertTrue(info.helpText.contains("Bicycle"))
    }

    @Test
    fun `getDetectionInfo returns correct info for car`() {
        val info = getDetectionInfo("car")
        assertEquals("directions_car", info.iconName)
        assertTrue(info.helpText.contains("Vehicle"))
    }

    @Test
    fun `getDetectionInfo returns correct info for truck`() {
        val info = getDetectionInfo("truck")
        assertEquals("directions_car", info.iconName)
    }

    @Test
    fun `getDetectionInfo returns correct info for bus`() {
        val info = getDetectionInfo("bus")
        assertEquals("directions_car", info.iconName)
    }

    @Test
    fun `getDetectionInfo returns correct info for motorcycle`() {
        val info = getDetectionInfo("motorcycle")
        assertEquals("two_wheeler", info.iconName)
    }

    @Test
    fun `getDetectionInfo returns correct info for airplane`() {
        val info = getDetectionInfo("airplane")
        assertEquals("flight", info.iconName)
        assertTrue(info.helpText.contains("professional"))
    }

    @Test
    fun `getDetectionInfo returns correct info for boat`() {
        val info = getDetectionInfo("boat")
        assertEquals("directions_boat", info.iconName)
    }

    @Test
    fun `getDetectionInfo returns correct info for traffic light`() {
        val info = getDetectionInfo("traffic light")
        assertEquals("traffic", info.iconName)
    }

    @Test
    fun `getDetectionInfo returns correct info for fire hydrant`() {
        val info = getDetectionInfo("fire hydrant")
        assertEquals("local_fire_department", info.iconName)
    }

    @Test
    fun `getDetectionInfo returns correct info for stop sign`() {
        val info = getDetectionInfo("stop sign")
        assertEquals("stop", info.iconName)
    }

    @Test
    fun `getDetectionInfo returns correct info for bench`() {
        val info = getDetectionInfo("bench")
        assertEquals("deck", info.iconName)
    }

    @Test
    fun `getDetectionInfo returns correct info for bird`() {
        val info = getDetectionInfo("bird")
        assertEquals("attractions", info.iconName)
    }

    @Test
    fun `getDetectionInfo returns correct info for cat`() {
        val info = getDetectionInfo("cat")
        assertEquals("pets", info.iconName)
    }

    @Test
    fun `getDetectionInfo returns correct info for dog`() {
        val info = getDetectionInfo("dog")
        assertEquals("pets", info.iconName)
    }

    @Test
    fun `getDetectionInfo returns correct info for horse`() {
        val info = getDetectionInfo("horse")
        assertEquals("pets", info.iconName)
        assertTrue(info.helpText.contains("safe distance"))
    }

    @Test
    fun `getDetectionInfo returns correct info for backpack`() {
        val info = getDetectionInfo("backpack")
        assertEquals("backpack", info.iconName)
    }

    @Test
    fun `getDetectionInfo returns correct info for suitcase`() {
        val info = getDetectionInfo("suitcase")
        assertEquals("backpack", info.iconName)
    }

    @Test
    fun `getDetectionInfo returns correct info for umbrella`() {
        val info = getDetectionInfo("umbrella")
        assertEquals("umbrella", info.iconName)
    }

    @Test
    fun `getDetectionInfo returns correct info for tie`() {
        val info = getDetectionInfo("tie")
        assertEquals("checkroom", info.iconName)
    }

    @Test
    fun `getDetectionInfo returns correct info for frisbee`() {
        val info = getDetectionInfo("frisbee")
        assertEquals("sports_soccer", info.iconName)
    }

    @Test
    fun `getDetectionInfo returns correct info for sports ball`() {
        val info = getDetectionInfo("sports ball")
        assertEquals("sports_soccer", info.iconName)
    }

    @Test
    fun `getDetectionInfo returns correct info for skateboard`() {
        val info = getDetectionInfo("skateboard")
        assertEquals("skateboarding", info.iconName)
    }

    @Test
    fun `getDetectionInfo returns correct info for bottle`() {
        val info = getDetectionInfo("bottle")
        assertEquals("local_cafe", info.iconName)
    }

    @Test
    fun `getDetectionInfo returns correct info for fork`() {
        val info = getDetectionInfo("fork")
        assertEquals("restaurant", info.iconName)
    }

    @Test
    fun `getDetectionInfo returns correct info for bowl`() {
        val info = getDetectionInfo("bowl")
        assertEquals("restaurant", info.iconName)
    }

    @Test
    fun `getDetectionInfo returns correct info for chair`() {
        val info = getDetectionInfo("chair")
        assertEquals("chair", info.iconName)
    }

    @Test
    fun `getDetectionInfo returns correct info for couch`() {
        val info = getDetectionInfo("couch")
        assertEquals("chair", info.iconName)
    }

    @Test
    fun `getDetectionInfo returns correct info for potted plant`() {
        val info = getDetectionInfo("potted plant")
        assertEquals("local_florist", info.iconName)
    }

    @Test
    fun `getDetectionInfo returns correct info for bed`() {
        val info = getDetectionInfo("bed")
        assertEquals("bed", info.iconName)
    }

    @Test
    fun `getDetectionInfo returns correct info for dining table`() {
        val info = getDetectionInfo("dining table")
        assertEquals("table_restaurant", info.iconName)
    }

    @Test
    fun `getDetectionInfo returns correct info for toilet`() {
        val info = getDetectionInfo("toilet")
        assertEquals("wc", info.iconName)
        assertTrue(info.helpText.contains("flapper valve"))
    }

    @Test
    fun `getDetectionInfo returns correct info for tv`() {
        val info = getDetectionInfo("tv")
        assertEquals("tv", info.iconName)
    }

    @Test
    fun `getDetectionInfo returns correct info for laptop`() {
        val info = getDetectionInfo("laptop")
        assertEquals("tv", info.iconName)
    }

    @Test
    fun `getDetectionInfo returns correct info for keyboard`() {
        val info = getDetectionInfo("keyboard")
        assertEquals("keyboard", info.iconName)
    }

    @Test
    fun `getDetectionInfo returns correct info for mouse`() {
        val info = getDetectionInfo("mouse")
        assertEquals("keyboard", info.iconName)
    }

    @Test
    fun `getDetectionInfo returns correct info for remote`() {
        val info = getDetectionInfo("remote")
        assertEquals("settings_remote", info.iconName)
    }

    @Test
    fun `getDetectionInfo returns correct info for cell phone`() {
        val info = getDetectionInfo("cell phone")
        assertEquals("smartphone", info.iconName)
    }

    @Test
    fun `getDetectionInfo returns correct info for microwave`() {
        val info = getDetectionInfo("microwave")
        assertEquals("microwave", info.iconName)
    }

    @Test
    fun `getDetectionInfo returns correct info for oven`() {
        val info = getDetectionInfo("oven")
        assertEquals("microwave", info.iconName)
    }

    @Test
    fun `getDetectionInfo returns correct info for refrigerator`() {
        val info = getDetectionInfo("refrigerator")
        assertEquals("kitchen", info.iconName)
    }

    @Test
    fun `getDetectionInfo returns correct info for sink`() {
        val info = getDetectionInfo("sink")
        assertEquals("countertops", info.iconName)
    }

    @Test
    fun `getDetectionInfo returns correct info for book`() {
        val info = getDetectionInfo("book")
        assertEquals("menu_book", info.iconName)
    }

    @Test
    fun `getDetectionInfo returns correct info for clock`() {
        val info = getDetectionInfo("clock")
        assertEquals("schedule", info.iconName)
    }

    @Test
    fun `getDetectionInfo returns correct info for scissors`() {
        val info = getDetectionInfo("scissors")
        assertEquals("content_cut", info.iconName)
    }

    @Test
    fun `getDetectionInfo returns correct info for teddy bear`() {
        val info = getDetectionInfo("teddy bear")
        assertEquals("cruelty_free", info.iconName)
    }

    @Test
    fun `getDetectionInfo returns correct info for hair drier`() {
        val info = getDetectionInfo("hair drier")
        assertEquals("bathroom", info.iconName)
    }

    @Test
    fun `getDetectionInfo returns correct info for toothbrush`() {
        val info = getDetectionInfo("toothbrush")
        assertEquals("bathroom", info.iconName)
    }

    @Test
    fun `getDetectionInfo returns default info for unknown label`() {
        val info = getDetectionInfo("unknown_object")
        assertEquals("build", info.iconName)
        assertTrue(info.helpText.contains("unknown_object"))
    }

    @Test
    fun `getDetectionInfo returns default info with label name in text`() {
        val info = getDetectionInfo("mystery_item")
        assertTrue(info.helpText.contains("mystery_item detected"))
    }

    @Test
    fun `DetectionInfo data class has correct properties`() {
        val info = DetectionInfo("test_icon", "test_help")
        assertEquals("test_icon", info.iconName)
        assertEquals("test_help", info.helpText)
    }

    @Test
    fun `getDetectionInfo helpText is not empty for all known labels`() {
        val knownLabels = listOf(
            "person", "bicycle", "car", "motorcycle", "airplane", "bus", "boat",
            "traffic light", "fire hydrant", "stop sign", "bench", "bird", "cat", "dog",
            "backpack", "umbrella", "tie", "frisbee", "skateboard", "bottle", "fork",
            "bowl", "chair", "bed", "toilet", "tv", "laptop", "keyboard", "remote",
            "cell phone", "microwave", "refrigerator", "sink", "book", "clock", "scissors"
        )

        knownLabels.forEach { label ->
            val info = getDetectionInfo(label)
            assertTrue("Help text should not be empty for $label", info.helpText.isNotEmpty())
            assertTrue("Icon name should not be empty for $label", info.iconName.isNotEmpty())
        }
    }
}
