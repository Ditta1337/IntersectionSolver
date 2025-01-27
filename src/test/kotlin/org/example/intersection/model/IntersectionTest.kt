package org.example.intersection.model

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class IntersectionTest {

    @Test
    fun `performStep processes vehicle movements correctly`() {
        val road1 = Road(Direction.NORTH, 2)
        val road2 = Road(Direction.SOUTH, 2)
        val road3 = Road(Direction.EAST, 2)
        val road4 = Road(Direction.WEST, 2)

        val roads = listOf(road1, road2, road3, road4)
        val intersection = Intersection(roads, greenLightDuration = 4, greenLightExtension = 2, numberOfLanes = 2)

        road1.addVehicle(Vehicle("vehicle1", Direction.SOUTH))
        road2.addVehicle(Vehicle("vehicle2", Direction.EAST))
        road3.addVehicle(Vehicle("vehicle3", Direction.WEST))
        road4.addVehicle(Vehicle("vehicle4", Direction.NORTH))

        val movedVehicles = intersection.performStep()

        assertEquals(2, movedVehicles.size, "Two vehicles should move in the first step.")
        assertTrue(movedVehicles.any { it.vehicleId == "vehicle1" }, "Vehicle1 should have moved.")
        assertTrue(movedVehicles.any { it.vehicleId == "vehicle2" }, "Vehicle2 should have moved.")
    }

    @Test
    fun `performStep handles green light extension correctly`() {
        val road1 = Road(Direction.SOUTH, 1)
        val road2 = Road(Direction.NORTH, 1)
        val road3 = Road(Direction.EAST, 1)
        val road4 = Road(Direction.WEST, 1)

        val roads = listOf(road1, road2, road3, road4)
        val intersection = Intersection(roads, greenLightDuration = 4, greenLightExtension = 4, numberOfLanes = 1)

        val movedVehicles = mutableListOf<Vehicle>()

        repeat(8) { road1.addVehicle(Vehicle("vehicle$it", Direction.NORTH)) }

        repeat(5) { movedVehicles.addAll(intersection.performStep()) }

        assertEquals(5, movedVehicles.size, "Green light should have been extended and allowed more vehicles to move.")
    }

    @Test
    fun `performStep switches to yellow lights correctly`() {
        val road1 = Road(Direction.SOUTH, 1)
        val road2 = Road(Direction.NORTH, 1)
        val road3 = Road(Direction.EAST, 1)
        val road4 = Road(Direction.WEST, 1)

        val roads = listOf(road1, road2, road3, road4)
        val intersection = Intersection(roads, greenLightDuration = 4, greenLightExtension = 0, numberOfLanes = 2)

        val movedVehicles = mutableListOf<Vehicle>()


        repeat(4) { road1.addVehicle(Vehicle("vehicle$it", Direction.NORTH)) }

        repeat(3) { movedVehicles.addAll(intersection.performStep()) }

        assertEquals(3, movedVehicles.size, "Three vehicles should move before yellow lights.")
    }

    @Test
    fun `performStep resets cycle correctly after yellow lights`() {
        val road1 = Road(Direction.NORTH, 2)
        val road2 = Road(Direction.SOUTH, 2)

        val roads = listOf(road1, road2)
        val intersection = Intersection(roads, greenLightDuration = 2, greenLightExtension = 0, numberOfLanes = 2)

        repeat(4) { intersection.performStep() }

        assertEquals(2, road1.activeLanes.size, "Lanes should be active for the new cycle.")
    }

    @Test
    fun `performStep manages single-lane roads correctly`() {
        val road1 = Road(Direction.NORTH, 1)
        val road2 = Road(Direction.SOUTH, 1)
        val road3 = Road(Direction.EAST, 1)
        val road4 = Road(Direction.WEST, 1)

        val roads = listOf(road1, road2, road3, road4)
        val intersection = Intersection(roads, greenLightDuration = 3, greenLightExtension = 1, numberOfLanes = 1)

        road1.addVehicle(Vehicle("vehicle1", Direction.SOUTH))
        road2.addVehicle(Vehicle("vehicle2", Direction.NORTH))

        val movedVehicles = intersection.performStep()

        assertEquals(1, movedVehicles.size, "Only one vehicle should move due to single-lane constraints.")
    }
}
