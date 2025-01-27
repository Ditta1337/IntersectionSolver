package org.example.intersection.model

import org.example.intersection.model.lightstate.LightState

class Light(private val initialState : LightState) {
    private var state = initialState

    val currentState: LightState get() = state

    fun nextState() {
        state = state.next()
    }
}