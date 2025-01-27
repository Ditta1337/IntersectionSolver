package org.example.intersection.simulation.configuration

data class Configuration(
    val numberOfLanes: Int = 1,
    val greenLightDuration: Int = 6,
    val greenLightExtension: Int = 3,
    val turnLanesPercentage: Double = 0.5,
    val addVehicleCommandString: String = "addVehicle",
    val simulationStepCommandString: String = "step",
)