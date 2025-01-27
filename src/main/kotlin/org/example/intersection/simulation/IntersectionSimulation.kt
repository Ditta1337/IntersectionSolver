package org.example.intersection.simulation

import org.example.intersection.exception.InvalidConfigurationException
import org.example.intersection.model.Direction
import org.example.intersection.model.Intersection
import org.example.intersection.model.Road
import org.example.intersection.model.Vehicle
import org.example.intersection.simulation.configuration.Configuration
import org.example.intersection.simulation.configuration.Input
import org.example.intersection.simulation.configuration.Output
import org.example.intersection.simulation.configuration.StepStatus

/**
 * Class that simulates the intersection.
 *
 * @property input input data for the simulation
 * @property configuration configuration of the simulation
 *
 * @constructor Creates a new instance of IntersectionSimulation with the provided input and configuration.
 * @throws IllegalArgumentException
 * @throws InvalidConfigurationException
 */
class IntersectionSimulation(private val input: Input, private val configuration: Configuration) {
    private val output = Output(mutableListOf())
    private val firstDirection = input.commands.firstOrNull { it.type == configuration.addVehicleCommandString }?.startRoad
        ?: throw IllegalArgumentException("First ${configuration.addVehicleCommandString} command must have startRoad")


    init {
        validateConfiguration()
    }

    private val roadOrder: Map<Direction, Road> = linkedMapOf(
        firstDirection to Road(firstDirection, configuration.numberOfLanes, configuration.turnLanesPercentage),
        firstDirection.right() to Road(
            firstDirection.right(),
            configuration.numberOfLanes,
            configuration.turnLanesPercentage
        ),
        firstDirection.opposite() to Road(
            firstDirection.opposite(),
            configuration.numberOfLanes,
            configuration.turnLanesPercentage
        ),
        firstDirection.left() to Road(
            firstDirection.left(),
            configuration.numberOfLanes,
            configuration.turnLanesPercentage
        )
    )

    private val intersection = Intersection(
        roadOrder.values.toList(),
        configuration.greenLightDuration,
        configuration.greenLightExtension,
        configuration.numberOfLanes
    )


    /**
     * Runs the simulation based on the input data and configuration.
     *
     * @return the output data of the simulation
     * @throws IllegalArgumentException
     */
    fun runSimulation(): Output {
        input.commands.forEach { command ->
            when (command.type) {
                configuration.addVehicleCommandString -> {
                    val vehicle = Vehicle(command.vehicleId!!, command.endRoad!!)
                    roadOrder[command.startRoad]!!.addVehicle(vehicle)
                }

                configuration.simulationStepCommandString -> {
                    val vehiclesMoved: List<String> = intersection.performStep().map { it.vehicleId }
                    output.stepStatuses.add(StepStatus(vehiclesMoved))
                }

                else -> throw IllegalArgumentException("Unknown command type: ${command.type}")
            }
        }

        return output
    }

    private fun validateConfiguration() {
        if (configuration.numberOfLanes < 1) {
            throw InvalidConfigurationException("Number of lanes must be greater than 0")
        } else if (configuration.numberOfLanes > 2 && configuration.numberOfLanes * configuration.turnLanesPercentage <= 0.0) {
            throw InvalidConfigurationException("Number of turn lanes must be at least 1")
        } else if (configuration.greenLightDuration < 4) {
            throw InvalidConfigurationException("Green light duration must be greater than 3")
        } else if (configuration.greenLightExtension < 0) {
            throw InvalidConfigurationException("Green light extension must be greater or equal to 0")
        } else if (configuration.addVehicleCommandString == configuration.simulationStepCommandString) {
            throw InvalidConfigurationException("Command strings must be different")
        }
    }

}