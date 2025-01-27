package org.example.intersection.model

import org.example.intersection.model.lightstate.RightArrowLightState
import org.example.intersection.model.lightstate.TrafficLightState
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*

class LaneTest {

    private lateinit var lane: Lane
    private lateinit var trafficLight: Light
    private lateinit var rightArrowLight: Light

    @BeforeEach
    fun setUp() {
        trafficLight = mock {
            on { currentState } doReturn TrafficLightState.RED
        }
        rightArrowLight = mock {
            on { currentState } doReturn RightArrowLightState.OFF
        }
        lane = Lane("NORTH-0", Direction.NORTH, listOf(trafficLight, rightArrowLight))
    }

    @Test
    fun `addVehicle adds a vehicle to the lane`() {
        val vehicle = Vehicle("V1", Direction.EAST)

        lane.addVehicle(vehicle)

        assertEquals(1, lane.vehiclesCount)
    }

    @Test
    fun `trafficLightNextState calls nextState on the traffic light`() {
        lane.trafficLightNextState()

        verify(trafficLight).nextState()
    }

    @Test
    fun `rightArrowLightNextState calls nextState on the right arrow light`() {
        lane.rightArrowLightNextState()

        verify(rightArrowLight).nextState()
    }

    @Test
    fun `nextVehicleTurnsLeft returns true when the next vehicle turns left`() {
        val vehicle = Vehicle("V1", Direction.EAST)
        lane.addVehicle(vehicle)

        assertTrue(lane.nextVehicleTurnsLeft())
    }

    @Test
    fun `nextVehicleTurnsLeft returns false when the next vehicle does not turn left`() {
        val vehicle = Vehicle("V1", Direction.WEST)
        lane.addVehicle(vehicle)

        assertFalse(lane.nextVehicleTurnsLeft())
    }

    @Test
    fun `moveVehicle does not move a vehicle when no vehicles are present`() {
        val leftTurnBuffer = mutableListOf<Vehicle>()

        val movedVehicle = lane.moveVehicle(leftTurnBuffer)

        assertNull(movedVehicle)
        assertTrue(leftTurnBuffer.isEmpty())
    }

    @Test
    fun `moveVehicle moves a vehicle straight if the light is green`() {
        whenever(trafficLight.currentState).thenReturn(TrafficLightState.GREEN)
        val vehicle = Vehicle("V1", Direction.SOUTH)
        lane.addVehicle(vehicle)

        val leftTurnBuffer = mutableListOf<Vehicle>()
        val movedVehicle = lane.moveVehicle(leftTurnBuffer)
        lane.removeVehicles()

        assertEquals(vehicle, movedVehicle)
        assertEquals(0, lane.vehiclesCount)
        assertTrue(leftTurnBuffer.isEmpty())
    }

    @Test
    fun `moveVehicle moves a vehicle left if the light is green and buffer is empty`() {
        whenever(trafficLight.currentState).thenReturn(TrafficLightState.GREEN)
        val vehicle = Vehicle("V1", Direction.WEST)
        lane.addVehicle(vehicle)

        val leftTurnBuffer = mutableListOf<Vehicle>()
        val movedVehicle = lane.moveVehicle(leftTurnBuffer)
        lane.removeVehicles()


        assertEquals(vehicle, movedVehicle)
        assertEquals(0, leftTurnBuffer.size)
    }

    @Test
    fun `moveVehicle does not move a vehicle if the light is yellow`() {
        whenever(trafficLight.currentState).thenReturn(TrafficLightState.YELLOW)
        val vehicle = Vehicle("V1", Direction.SOUTH)
        lane.addVehicle(vehicle)

        val leftTurnBuffer = mutableListOf<Vehicle>()
        val movedVehicle = lane.moveVehicle(leftTurnBuffer)

        assertNull(movedVehicle)
        assertEquals(1, lane.vehiclesCount)
    }

    @Test
    fun `removeVehicles clears vehicles marked for removal`() {
        val vehicle1 = Vehicle("V1", Direction.SOUTH)
        val vehicle2 = Vehicle("V1", Direction.SOUTH)
        lane.addVehicle(vehicle1)
        lane.addVehicle(vehicle2)

        val leftTurnBuffer = mutableListOf<Vehicle>()
        lane.moveVehicle(leftTurnBuffer)

        lane.removeVehicles()

        assertEquals(1, lane.vehiclesCount)
    }
}
