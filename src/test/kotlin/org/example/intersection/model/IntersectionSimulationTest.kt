import org.example.intersection.exception.InvalidConfigurationException
import org.example.intersection.model.Direction
import org.example.intersection.simulation.IntersectionSimulation
import org.example.intersection.simulation.configuration.Command
import org.example.intersection.simulation.configuration.Configuration
import org.example.intersection.simulation.configuration.Input
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class IntersectionSimulationTest {

    @Test
    fun `run processes commands correctly`() {
        val input = Input(
            commands = listOf(
                Command("addVehicle", "vehicle1", Direction.NORTH, Direction.SOUTH),
                Command("step", null, null, null)
            )
        )
        val configuration = Configuration()

        val simulation = IntersectionSimulation(input, configuration)
        val output = simulation.runSimulation()

        assertEquals(1, output.stepStatuses.size, "Output should have one step status.")
        assertEquals(listOf("vehicle1"), output.stepStatuses[0].vehiclesLeft, "Vehicle1 should have moved in the step.")
    }

    @Test
    fun `constructor throws exception for invalid configuration`() {
        val invalidConfiguration = Configuration(numberOfLanes = 0)
        val correctInput = Input(
            commands = listOf(
                Command("addVehicle", "vehicle1", Direction.NORTH, Direction.SOUTH),
            )
        )
        val exception = assertThrows<InvalidConfigurationException> {
            IntersectionSimulation(correctInput, invalidConfiguration)
        }

        assertEquals("Number of lanes must be greater than 0", exception.message)
    }

    @Test
    fun `constructor throws exception for invalid command strings`() {
        val invalidConfiguration = Configuration(
            addVehicleCommandString = "addVehicle",
            simulationStepCommandString = "addVehicle"
        )
        val correctInput = Input(
            commands = listOf(
                Command("addVehicle", "vehicle1", Direction.NORTH, Direction.SOUTH),
            )
        )

        val exception = assertThrows<InvalidConfigurationException> {
            IntersectionSimulation(correctInput, invalidConfiguration)
        }

        assertEquals("Command strings must be different", exception.message)
    }

    @Test
    fun `run throws exception for unknown command`() {
        val input = Input(
            commands = listOf(
                Command("unknownCommand", null, null, null),
                Command("addVehicle", "vehicle1", Direction.NORTH, Direction.SOUTH)
            )
        )
        val configuration = Configuration()

        val simulation = IntersectionSimulation(input, configuration)

        val exception = assertThrows<IllegalArgumentException> {
            simulation.runSimulation()
        }

        assertEquals("Unknown command type: unknownCommand", exception.message)
    }


    @Test
    fun `run handles multiple vehicles correctly`() {
        val input = Input(
            commands = listOf(
                Command("addVehicle", "vehicle1", Direction.NORTH, Direction.SOUTH),
                Command("addVehicle", "vehicle2", Direction.SOUTH, Direction.EAST),
                Command("step", null, null, null)
            )
        )
        val configuration = Configuration()

        val simulation = IntersectionSimulation(input, configuration)
        val output = simulation.runSimulation()

        assertEquals(1, output.stepStatuses.size, "Output should have one step status.")
        assertEquals(
            listOf("vehicle1", "vehicle2"),
            output.stepStatuses[0].vehiclesLeft,
            "Vehicles should have moved in the step."
        )
    }

    @Test
    fun `constructor throws exception for missing start road`() {
        val input = Input(
            commands = listOf(
                Command("addVehicle", "vehicle1", null, Direction.SOUTH)
            )
        )
        val configuration = Configuration()

        val exception = assertThrows<IllegalArgumentException> {
            IntersectionSimulation(input, configuration)
        }

        assertEquals(
            "First addVehicle command must have startRoad",
            exception.message
        )
    }

    @Test
    fun `validateConfiguration throws exception for invalid green light duration`() {
        val invalidConfiguration = Configuration(greenLightDuration = 3)
        val correctInput = Input(
            commands = listOf(
                Command("addVehicle", "vehicle1", Direction.NORTH, Direction.SOUTH),
            )
        )

        val exception = assertThrows<InvalidConfigurationException> {
            IntersectionSimulation(correctInput, invalidConfiguration)
        }

        assertEquals("Green light duration must be greater than 3", exception.message)
    }

    @Test
    fun `validateConfiguration throws exception for negative green light extension`() {
        val invalidConfiguration = Configuration(greenLightExtension = -1)
        val correctInput = Input(
            commands = listOf(
                Command("addVehicle", "vehicle1", Direction.NORTH, Direction.SOUTH),
            )
        )

        val exception = assertThrows<InvalidConfigurationException> {
            IntersectionSimulation(correctInput, invalidConfiguration)
        }

        assertEquals("Green light extension must be greater or equal to 0", exception.message)
    }
}
