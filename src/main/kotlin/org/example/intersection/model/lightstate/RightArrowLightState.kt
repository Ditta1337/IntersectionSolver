package org.example.intersection.model.lightstate

enum class RightArrowLightState : LightState {
    ON, OFF;

    override fun next(): RightArrowLightState {
        return when (this) {
            ON -> OFF
            OFF -> ON
        }
    }
}