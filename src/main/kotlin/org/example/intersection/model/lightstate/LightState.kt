package org.example.intersection.model.lightstate

interface LightState {
    fun next(): LightState
}