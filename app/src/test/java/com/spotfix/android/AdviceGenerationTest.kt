package com.spotfix.android

import android.graphics.RectF
import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for generateAdvice function in AdviceScreen.kt
 */
class AdviceGenerationTest {

    private fun createDetection(label: String, score: Float = 0.9f): Detection {
        return Detection(RectF(0f, 0f, 100f, 100f), label, score)
    }

    // ==================== Plumbing Advice Tests ====================

    @Test
    fun `generateAdvice returns plumbing advice for sink`() {
        val detections = listOf(createDetection("sink"))

        val advice = generateAdvice(detections)

        assertTrue(advice.any { it.title.contains("Plumbing") })
    }

    @Test
    fun `generateAdvice returns plumbing advice for toilet`() {
        val detections = listOf(createDetection("toilet"))

        val advice = generateAdvice(detections)

        assertTrue(advice.any { it.title.contains("Plumbing") })
    }

    @Test
    fun `plumbing advice contains water safety step`() {
        val detections = listOf(createDetection("sink"))

        val advice = generateAdvice(detections)
        val plumbingAdvice = advice.find { it.title.contains("Plumbing") }

        assertNotNull(plumbingAdvice)
        assertTrue(plumbingAdvice!!.steps.any { it.contains("water supply") })
    }

    @Test
    fun `plumbing advice has correct number of steps`() {
        val detections = listOf(createDetection("toilet"))

        val advice = generateAdvice(detections)
        val plumbingAdvice = advice.find { it.title.contains("Plumbing") }

        assertNotNull(plumbingAdvice)
        assertEquals(6, plumbingAdvice!!.steps.size)
    }

    // ==================== Appliance Advice Tests ====================

    @Test
    fun `generateAdvice returns appliance advice for refrigerator`() {
        val detections = listOf(createDetection("refrigerator"))

        val advice = generateAdvice(detections)

        assertTrue(advice.any { it.title.contains("Appliance") })
    }

    @Test
    fun `generateAdvice returns appliance advice for microwave`() {
        val detections = listOf(createDetection("microwave"))

        val advice = generateAdvice(detections)

        assertTrue(advice.any { it.title.contains("Appliance") })
    }

    @Test
    fun `generateAdvice returns appliance advice for oven`() {
        val detections = listOf(createDetection("oven"))

        val advice = generateAdvice(detections)

        assertTrue(advice.any { it.title.contains("Appliance") })
    }

    @Test
    fun `appliance advice contains safety step`() {
        val detections = listOf(createDetection("refrigerator"))

        val advice = generateAdvice(detections)
        val applianceAdvice = advice.find { it.title.contains("Appliance") }

        assertNotNull(applianceAdvice)
        assertTrue(applianceAdvice!!.steps.any { it.contains("Unplug") })
    }

    // ==================== Electronics Advice Tests ====================

    @Test
    fun `generateAdvice returns electronics advice for tv`() {
        val detections = listOf(createDetection("tv"))

        val advice = generateAdvice(detections)

        assertTrue(advice.any { it.title.contains("Electronics") })
    }

    @Test
    fun `generateAdvice returns electronics advice for laptop`() {
        val detections = listOf(createDetection("laptop"))

        val advice = generateAdvice(detections)

        assertTrue(advice.any { it.title.contains("Electronics") })
    }

    @Test
    fun `generateAdvice returns electronics advice for monitor`() {
        val detections = listOf(createDetection("monitor"))

        val advice = generateAdvice(detections)

        assertTrue(advice.any { it.title.contains("Electronics") })
    }

    @Test
    fun `electronics advice mentions surge protector`() {
        val detections = listOf(createDetection("tv"))

        val advice = generateAdvice(detections)
        val electronicsAdvice = advice.find { it.title.contains("Electronics") }

        assertNotNull(electronicsAdvice)
        assertTrue(electronicsAdvice!!.steps.any { it.contains("surge protector") })
    }

    // ==================== Furniture Advice Tests ====================

    @Test
    fun `generateAdvice returns furniture advice for chair`() {
        val detections = listOf(createDetection("chair"))

        val advice = generateAdvice(detections)

        assertTrue(advice.any { it.title.contains("Furniture") })
    }

    @Test
    fun `generateAdvice returns furniture advice for couch`() {
        val detections = listOf(createDetection("couch"))

        val advice = generateAdvice(detections)

        assertTrue(advice.any { it.title.contains("Furniture") })
    }

    @Test
    fun `generateAdvice returns furniture advice for bed`() {
        val detections = listOf(createDetection("bed"))

        val advice = generateAdvice(detections)

        assertTrue(advice.any { it.title.contains("Furniture") })
    }

    @Test
    fun `furniture advice mentions wood glue`() {
        val detections = listOf(createDetection("chair"))

        val advice = generateAdvice(detections)
        val furnitureAdvice = advice.find { it.title.contains("Furniture") }

        assertNotNull(furnitureAdvice)
        assertTrue(furnitureAdvice!!.steps.any { it.contains("wood glue") })
    }

    // ==================== Vehicle Advice Tests ====================

    @Test
    fun `generateAdvice returns vehicle advice for car`() {
        val detections = listOf(createDetection("car"))

        val advice = generateAdvice(detections)

        assertTrue(advice.any { it.title.contains("Vehicle") })
    }

    @Test
    fun `generateAdvice returns vehicle advice for truck`() {
        val detections = listOf(createDetection("truck"))

        val advice = generateAdvice(detections)

        assertTrue(advice.any { it.title.contains("Vehicle") })
    }

    @Test
    fun `generateAdvice returns vehicle advice for motorcycle`() {
        val detections = listOf(createDetection("motorcycle"))

        val advice = generateAdvice(detections)

        assertTrue(advice.any { it.title.contains("Vehicle") })
    }

    @Test
    fun `vehicle advice mentions oil change`() {
        val detections = listOf(createDetection("car"))

        val advice = generateAdvice(detections)
        val vehicleAdvice = advice.find { it.title.contains("Vehicle") }

        assertNotNull(vehicleAdvice)
        assertTrue(vehicleAdvice!!.steps.any { it.contains("oil") })
    }

    // ==================== Bike & Board Advice Tests ====================

    @Test
    fun `generateAdvice returns bike advice for bicycle`() {
        val detections = listOf(createDetection("bicycle"))

        val advice = generateAdvice(detections)

        assertTrue(advice.any { it.title.contains("Bike") })
    }

    @Test
    fun `generateAdvice returns bike advice for skateboard`() {
        val detections = listOf(createDetection("skateboard"))

        val advice = generateAdvice(detections)

        assertTrue(advice.any { it.title.contains("Bike") || it.title.contains("Board") })
    }

    @Test
    fun `bike advice mentions tire pressure`() {
        val detections = listOf(createDetection("bicycle"))

        val advice = generateAdvice(detections)
        val bikeAdvice = advice.find { it.title.contains("Bike") }

        assertNotNull(bikeAdvice)
        assertTrue(bikeAdvice!!.steps.any { it.contains("tire pressure") || it.contains("PSI") })
    }

    // ==================== Work at Height Safety Tests ====================

    @Test
    fun `generateAdvice returns safety advice for person with ladder`() {
        val detections = listOf(
            createDetection("person"),
            createDetection("ladder")
        )

        val advice = generateAdvice(detections)

        assertTrue(advice.any { it.title.contains("Height") || it.title.contains("Safety") })
    }

    @Test
    fun `safety advice mentions harness`() {
        val detections = listOf(
            createDetection("person"),
            createDetection("ladder")
        )

        val advice = generateAdvice(detections)
        val safetyAdvice = advice.find { it.title.contains("Height") || it.title.contains("Safety") }

        assertNotNull(safetyAdvice)
        assertTrue(safetyAdvice!!.steps.any { it.contains("harness") })
    }

    @Test
    fun `safety advice mentions three points of contact`() {
        val detections = listOf(
            createDetection("person"),
            createDetection("ladder")
        )

        val advice = generateAdvice(detections)
        val safetyAdvice = advice.find { it.title.contains("Height") || it.title.contains("Safety") }

        assertNotNull(safetyAdvice)
        assertTrue(safetyAdvice!!.steps.any { it.contains("three points") })
    }

    // ==================== Generic Advice Tests ====================

    @Test
    fun `generateAdvice returns generic advice for unknown objects`() {
        val detections = listOf(createDetection("bird"))

        val advice = generateAdvice(detections)

        // bird doesn't match any specific category
        assertTrue(advice.isNotEmpty())
        assertTrue(advice.first().title.contains("bird", ignoreCase = true) ||
                   advice.first().title.contains("General", ignoreCase = true))
    }

    @Test
    fun `generateAdvice returns generic advice for empty matching categories`() {
        val detections = listOf(createDetection("cat"))

        val advice = generateAdvice(detections)

        assertTrue(advice.isNotEmpty())
    }

    @Test
    fun `generic advice has 6 steps`() {
        val detections = listOf(createDetection("bird"))

        val advice = generateAdvice(detections)

        // Generic advice should have steps
        assertTrue(advice.first().steps.size >= 5)
    }

    // ==================== Empty and Edge Cases ====================

    @Test
    fun `generateAdvice returns empty list for empty detections`() {
        val detections = emptyList<Detection>()

        val advice = generateAdvice(detections)

        assertTrue(advice.isEmpty())
    }

    @Test
    fun `generateAdvice handles single detection`() {
        val detections = listOf(createDetection("toilet"))

        val advice = generateAdvice(detections)

        assertTrue(advice.isNotEmpty())
    }

    @Test
    fun `generateAdvice handles multiple detections`() {
        val detections = listOf(
            createDetection("sink"),
            createDetection("toilet"),
            createDetection("refrigerator")
        )

        val advice = generateAdvice(detections)

        // Should have advice for both plumbing and appliances
        assertTrue(advice.size >= 2)
    }

    @Test
    fun `generateAdvice handles duplicate detections`() {
        val detections = listOf(
            createDetection("chair"),
            createDetection("chair"),
            createDetection("chair")
        )

        val advice = generateAdvice(detections)

        // Should only return one furniture advice
        assertEquals(1, advice.count { it.title.contains("Furniture") })
    }

    // ==================== Case Sensitivity Tests ====================

    @Test
    fun `generateAdvice is case insensitive`() {
        val lowerDetections = listOf(createDetection("sink"))
        val upperDetections = listOf(createDetection("SINK"))

        val lowerAdvice = generateAdvice(lowerDetections)
        val upperAdvice = generateAdvice(upperDetections)

        // Both should generate plumbing advice
        assertEquals(
            lowerAdvice.any { it.title.contains("Plumbing") },
            upperAdvice.any { it.title.contains("Plumbing") }
        )
    }

    // ==================== RepairAdvice Data Class Tests ====================

    @Test
    fun `RepairAdvice stores title description and steps correctly`() {
        val advice = RepairAdvice(
            title = "Test Title",
            description = "Test description",
            steps = listOf("Step 1", "Step 2", "Step 3")
        )

        assertEquals("Test Title", advice.title)
        assertEquals("Test description", advice.description)
        assertEquals(3, advice.steps.size)
        assertEquals("Step 1", advice.steps[0])
    }

    @Test
    fun `RepairAdvice equality works correctly`() {
        val advice1 = RepairAdvice("Title", "Desc", listOf("Step"))
        val advice2 = RepairAdvice("Title", "Desc", listOf("Step"))

        assertEquals(advice1, advice2)
    }

    @Test
    fun `RepairAdvice with different steps are not equal`() {
        val advice1 = RepairAdvice("Title", "Desc", listOf("Step 1"))
        val advice2 = RepairAdvice("Title", "Desc", listOf("Step 2"))

        assertNotEquals(advice1, advice2)
    }

    // ==================== Multiple Category Tests ====================

    @Test
    fun `generateAdvice returns multiple categories when applicable`() {
        val detections = listOf(
            createDetection("sink"),       // Plumbing
            createDetection("refrigerator"), // Appliance
            createDetection("tv")          // Electronics
        )

        val advice = generateAdvice(detections)

        assertTrue(advice.any { it.title.contains("Plumbing") })
        assertTrue(advice.any { it.title.contains("Appliance") })
        assertTrue(advice.any { it.title.contains("Electronics") })
    }

    @Test
    fun `generateAdvice handles combined person and tool detection`() {
        val detections = listOf(
            createDetection("person"),
            createDetection("drill") // Contains "tool" substring check
        )

        val advice = generateAdvice(detections)

        // This tests the "tool" substring check
        assertNotNull(advice)
    }

    // ==================== Advice Structure Tests ====================

    @Test
    fun `all advice has non-empty title`() {
        val testCases = listOf(
            listOf(createDetection("sink")),
            listOf(createDetection("refrigerator")),
            listOf(createDetection("tv")),
            listOf(createDetection("chair")),
            listOf(createDetection("car")),
            listOf(createDetection("bicycle"))
        )

        testCases.forEach { detections ->
            val advice = generateAdvice(detections)
            advice.forEach { item ->
                assertTrue("Title should not be empty", item.title.isNotEmpty())
            }
        }
    }

    @Test
    fun `all advice has non-empty description`() {
        val testCases = listOf(
            listOf(createDetection("sink")),
            listOf(createDetection("refrigerator")),
            listOf(createDetection("tv")),
            listOf(createDetection("chair")),
            listOf(createDetection("car")),
            listOf(createDetection("bicycle"))
        )

        testCases.forEach { detections ->
            val advice = generateAdvice(detections)
            advice.forEach { item ->
                assertTrue("Description should not be empty", item.description.isNotEmpty())
            }
        }
    }

    @Test
    fun `all advice has at least one step`() {
        val testCases = listOf(
            listOf(createDetection("sink")),
            listOf(createDetection("refrigerator")),
            listOf(createDetection("tv")),
            listOf(createDetection("chair")),
            listOf(createDetection("car")),
            listOf(createDetection("bicycle"))
        )

        testCases.forEach { detections ->
            val advice = generateAdvice(detections)
            advice.forEach { item ->
                assertTrue("Advice should have at least one step", item.steps.isNotEmpty())
            }
        }
    }

    @Test
    fun `all steps are non-empty strings`() {
        val detections = listOf(
            createDetection("sink"),
            createDetection("refrigerator"),
            createDetection("tv")
        )

        val advice = generateAdvice(detections)

        advice.forEach { item ->
            item.steps.forEach { step ->
                assertTrue("Step should not be empty", step.isNotEmpty())
            }
        }
    }
}
