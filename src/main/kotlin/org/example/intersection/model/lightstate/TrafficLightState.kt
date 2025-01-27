package org.example.intersection.model.lightstate

enum class TrafficLightState : LightState {
    GREEN, YELLOW, RED;

    override fun next(): TrafficLightState {
        return when (this) {
            GREEN -> YELLOW
            YELLOW -> RED
            RED -> GREEN
        }
    }
}