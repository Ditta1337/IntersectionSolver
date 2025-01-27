package org.example.intersection.model

import org.example.intersection.model.lightstate.TrafficLightState
import org.example.intersection.model.lightstate.RightArrowLightState

class Lane(val laneId: String, private val sourceDirection: Direction, val lights: List<Light>) {
    private val vehicles = ArrayDeque<Vehicle>()

    val vehiclesCount: Int get() = vehicles.size
    private val vehiclesToRemove = mutableListOf<Vehicle>()

    fun addVehicle(vehicle: Vehicle) {
        vehicles.add(vehicle)
    }

    fun trafficLightNextState() {
        lights[0].nextState()
        println("Lane $laneId traffic light state: ${lights[0].currentState}")
    }

    fun rightArrowLightNextState() {
        lights[1].nextState()
        println("Lane $laneId right arrow light state: ${lights[1].currentState}")
    }

    fun nextVehicleTurnsLeft(): Boolean {
        return vehicles.firstOrNull()?.let { it.destination == sourceDirection.left() } ?: true
    }

    fun moveVehicle(leftTurnBuffer: MutableList<Vehicle>, isRightArrowTurn: Boolean = false): Vehicle? {
        val light = if (isRightArrowTurn) lights[1] else lights[0]
        val vehicleToMove = vehicles.firstOrNull() ?: return null

        if (isRightArrowTurn && vehicleToMove.destination != sourceDirection.right()) return null

        if (light.currentState == TrafficLightState.YELLOW || light.currentState == RightArrowLightState.OFF) return null

        if (sourceDirection.left() == vehicleToMove.destination) {
            if (leftTurnBuffer.isEmpty()) {
                vehiclesToRemove.add(vehicleToMove)
                leftTurnBuffer.add(vehicleToMove)
                return null
            }
        } else {
            vehiclesToRemove.add(vehicleToMove)
            return vehicleToMove
        }

        return null
    }

    fun removeVehicles() {
        vehicles.removeAll(vehiclesToRemove)
        vehiclesToRemove.clear()
    }
}