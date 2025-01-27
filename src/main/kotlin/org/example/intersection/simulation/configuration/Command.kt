package org.example.intersection.simulation.configuration

import org.example.intersection.model.Direction

data class Command(
    val type: String,
    val vehicleId: String?,
    val startRoad: Direction?,
    val endRoad: Direction?
)