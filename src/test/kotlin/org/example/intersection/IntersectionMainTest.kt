import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.example.intersection.main
import org.example.intersection.simulation.IntersectionSimulation
import org.example.intersection.simulation.configuration.Input
import org.example.intersection.simulation.configuration.Output
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.mockito.Mockito
import java.io.File
import java.nio.file.Paths
import kotlin.io.path.createTempFile
import kotlin.io.path.writeText

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class IntersectionMainTest {

    private val resourcePath = Paths.get("src/test/resources/test/main").toAbsolutePath().toString()
    private val outputFilePath = "$resourcePath/output.json"
    private val objectMapper = jacksonObjectMapper()

    @BeforeEach
    fun setup() {
        val outputFile = File(outputFilePath)
        if (outputFile.exists()) {
            outputFile.delete()
        }
    }

    @Test
    fun `main processes input and output files correctly with valid arguments`() {
        val inputFilePath = "$resourcePath/input_valid.json"
        val configurationFilePath = "$resourcePath/configuration_valid.json"

        main(arrayOf(inputFilePath, outputFilePath, configurationFilePath))

        val outputFile = File(outputFilePath)
        assertTrue(outputFile.exists(), "Output file should be created.")
        assertTrue(outputFile.readText().isNotBlank(), "Output file should not be empty.")

        val output: Output = objectMapper.readValue(outputFile)
        assertTrue(output.stepStatuses.isNotEmpty(), "Output should contain step statuses.")
    }

    @Test
    fun `main handles missing configuration file gracefully`() {
        val inputFilePath = "$resourcePath/input_valid.json"

        main(arrayOf(inputFilePath, outputFilePath))

        val outputFile = File(outputFilePath)
        assertTrue(outputFile.exists(), "Output file should be created.")
        assertTrue(outputFile.readText().isNotBlank(), "Output file should not be empty.")
    }

    @Test
    fun `main fails when output file is missing`() {
        val inputFilePath = "$resourcePath/input_valid.json"

        val output = captureConsoleOutput {
            main(arrayOf(inputFilePath))
        }

        assertTrue(
            output.contains("Usage: IntersectionMainKt <inputPath> <outputPath> (optional: <configurationPath>)"),
            "Error message should indicate missing output file."
        )
    }

    @Test
    fun `main fails when input file is missing`() {
        val inputFilePath = "$resourcePath/nonexistent_input.json"

        val output = captureConsoleOutput {
            main(arrayOf(inputFilePath, outputFilePath))
        }

        assertTrue(output.contains("Error reading input file"), "Error message should indicate missing input file.")
    }

    @Test
    fun `main fails with invalid configuration`() {
        val inputFilePath = "$resourcePath/input_valid.json"
        val configurationFilePath = "$resourcePath/configuration_invalid.json"

        val output = captureConsoleOutput {
            main(arrayOf(inputFilePath, outputFilePath, configurationFilePath))
        }

        assertTrue(
            output.contains("Configuration file is invalid"),
            "Error message should indicate invalid configuration."
        )
    }

    @Test
    fun `main writes output correctly with mocked simulation`() {
        val inputFilePath = createTempFile("input", ".json").apply {
            writeText(
                objectMapper.writeValueAsString(
                    Input(commands = listOf())
                )
            )
        }.toAbsolutePath().toString()

        Mockito.mockConstruction(IntersectionSimulation::class.java) { mock, context ->
            Mockito.`when`(mock.runSimulation()).thenReturn(Output(mutableListOf()))
        }


        main(arrayOf(inputFilePath, outputFilePath))

        val outputFile = File(outputFilePath)
        assertTrue(outputFile.exists(), "Output file should be created.")
    }

    @AfterAll
    fun cleanup() {
        File(outputFilePath).delete()
    }

    private fun captureConsoleOutput(block: () -> Unit): String {
        val originalOut = System.out
        val outputStream = java.io.ByteArrayOutputStream()
        System.setOut(java.io.PrintStream(outputStream))
        try {
            block()
        } finally {
            System.setOut(originalOut)
        }
        return outputStream.toString()
    }
}
