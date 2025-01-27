package org.example.intersection.model

import org.example.intersection.model.lightstate.RightArrowLightState
import org.example.intersection.model.lightstate.TrafficLightState
import kotlin.math.ceil
import kotlin.math.max

/**
 * Represents a road with multiple lanes.
 *
 * @property sourceDirection the direction from which the road starts
 * @property numberOfLanes the number of lanes on the road
 * @property turnLanesPercentage the percentage of lanes that are turn lanes
 *
 * @constructor Creates a road with the given source direction, number of lanes, and turn lanes percentage
 * @throws IllegalArgumentException
 */
class Road(
    private val sourceDirection: Direction,
    private val numberOfLanes: Int,
    private val turnLanesPercentage: Double = 0.5
) {
    private val _leftLanes = mutableListOf<Lane>()
    private val _straightLanes = mutableListOf<Lane>()
    private val _rightLanes = mutableListOf<Lane>()

    private val leftTurnBuffer = mutableListOf<Vehicle>()

    val activeLanes = mutableListOf<Lane>()
    val activeRightArrowLanes = mutableListOf<Lane>()

    init {
        if (numberOfLanes == 1) {
            val lights = mutableListOf(Light(TrafficLightState.RED), Light(RightArrowLightState.OFF))
            val lane = Lane("$sourceDirection-0", sourceDirection, lights)

            _leftLanes.add(lane)
            _straightLanes.add(lane)
            _rightLanes.add(lane)

        } else if (numberOfLanes == 2) {
            val leftLights = mutableListOf(Light(TrafficLightState.RED))
            val leftLane = Lane("$sourceDirection-0", sourceDirection, leftLights)

            _leftLanes.add(leftLane)

            val straightAndRightLights = mutableListOf(Light(TrafficLightState.RED), Light(RightArrowLightState.OFF))
            val straightAndRightLane = Lane("$sourceDirection-1", sourceDirection, straightAndRightLights)

            _straightLanes.add(straightAndRightLane)
            _rightLanes.add(straightAndRightLane)

        } else if (numberOfLanes > 2) {
            val turnLanesNumber = max(ceil(numberOfLanes * turnLanesPercentage).toInt() / 2, 1)

            for (i in 0..<turnLanesNumber) {
                val leftLights = mutableListOf(Light(TrafficLightState.RED))
                val rightLights = mutableListOf(Light(TrafficLightState.RED))

                val leftLane = Lane("$sourceDirection-$i", sourceDirection, leftLights)
                val rightLane = Lane("$sourceDirection-${numberOfLanes - i - 1}", sourceDirection, rightLights)

                _leftLanes.add(leftLane)
                _rightLanes.add(rightLane)
            }

            for (i in turnLanesNumber..<numberOfLanes - turnLanesNumber) {
                val straightAndRightLights = mutableListOf(Light(TrafficLightState.RED))
                val straightAndRightLane = Lane("$sourceDirection-$i", sourceDirection, straightAndRightLights)

                _straightLanes.add(straightAndRightLane)
            }
        } else {
            throw IllegalArgumentException("Number of lanes must be greater than 0")
        }
    }

    val leftLanes: List<Lane> get() = _leftLanes
    val straightLanes: List<Lane> get() = _straightLanes
    val rightLanes: List<Lane> get() = _rightLanes

    fun addVehicle(vehicle: Vehicle) {
        when (vehicle.destination) {
            sourceDirection.left() -> _leftLanes.minByOrNull { it.vehiclesCount }!!.addVehicle(vehicle)
            sourceDirection.opposite() -> _straightLanes.minByOrNull { it.vehiclesCount }!!.addVehicle(vehicle)
            sourceDirection.right() -> _rightLanes.minByOrNull { it.vehiclesCount }!!.addVehicle(vehicle)
            else -> throw IllegalArgumentException("U-turn not allowed")
        }
    }

    fun addToActiveLanes(turns: List<Turn>, isRightArrowTurn: Boolean = false) {
        val lanes = turns.flatMap { turn ->
            when (turn) {
                Turn.LEFT -> _leftLanes
                Turn.STRAIGHT -> _straightLanes
                Turn.RIGHT -> _rightLanes
            }
        }.toSet()

        if (isRightArrowTurn) {
            activeRightArrowLanes.addAll(lanes)
        } else {
            activeLanes.addAll(lanes)
        }
    }

    fun clearActiveLanes() {
        activeLanes.clear()
        activeRightArrowLanes.clear()
    }

    fun activeTrafficLightsNextState() {
        activeLanes.forEach(Lane::trafficLightNextState)
    }

    fun activeRightArrowLightsNextState() {
        activeRightArrowLanes.forEach(Lane::rightArrowLightNextState)
    }

    fun shouldExtendGreenLight(threshold: Int): Boolean {
        return activeLanes.any { it.vehiclesCount > threshold }
    }

    fun moveVehicles(canReleaseBuffer: Boolean): List<Vehicle> {
        val movedVehicles = mutableListOf<Vehicle>()
        processLanes(activeLanes, movedVehicles, isArrowTurn = false, canReleaseBuffer)
        processLanes(activeRightArrowLanes, movedVehicles, isArrowTurn = true, canReleaseBuffer)

        return movedVehicles
    }

    private fun processLanes(
        lanes: List<Lane>,
        movedVehicles: MutableList<Vehicle>,
        isArrowTurn: Boolean,
        canReleaseBuffer: Boolean
    ) {
        for (lane in lanes) {
            lane.moveVehicle(leftTurnBuffer, isArrowTurn)?.let { movedVehicles.add(it) }
            if (leftTurnBuffer.isNotEmpty() && canReleaseBuffer) {
                movedVehicles.add(leftTurnBuffer.removeFirst())
            }
        }
    }

    fun finalizeRemovingMovedVehicles() {
        activeLanes.forEach(Lane::removeVehicles)
        activeRightArrowLanes.forEach(Lane::removeVehicles)
    }

    fun canReleaseOppositeRoadBuffer(): Boolean {
        return _leftLanes[0].nextVehicleTurnsLeft()
    }

}
