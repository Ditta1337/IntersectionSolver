package org.example.intersection

import com.fasterxml.jackson.core.exc.StreamReadException
import com.fasterxml.jackson.core.exc.StreamWriteException
import com.fasterxml.jackson.databind.DatabindException
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.example.intersection.exception.InvalidConfigurationException
import org.example.intersection.simulation.IntersectionSimulation
import org.example.intersection.simulation.configuration.Configuration
import org.example.intersection.simulation.configuration.Input
import org.example.intersection.simulation.configuration.Output
import java.io.File
import java.io.IOException


fun main(args: Array<String>) {
    if (args.size < 2) {
        println("Usage: java -jar IntersectionSolver.jar <inputPath> <outputPath> (optional: <configurationPath>)")
        return
    }

    val inputPath = args[0]
    val outputPath = args[1]
    val configurationPath = args.getOrNull(2)
    val objectMapper = jacksonObjectMapper()

    try {
        val configuration: Configuration =
            configurationPath?.let { objectMapper.readValue(File(it), Configuration::class.java) } ?: Configuration()
        val input: Input = objectMapper.readValue(File(inputPath))
        val output: Output = IntersectionSimulation(input, configuration).runSimulation()

        objectMapper.writeValue(File(outputPath), output)
    } catch (e: IOException) {
        println("Error reading input file at path '$inputPath': ${e.message}")
        return
    } catch (e: StreamReadException) {
        println("Error parsing input file at path '$inputPath': ${e.message}")
        return
    } catch (e: StreamWriteException) {
        println("Error writing output file at path '$outputPath': ${e.message}")
        return
    } catch (e: DatabindException) {
        println("Error mapping data from input file at path '$inputPath': ${e.message}")
        return
    } catch (e: IllegalArgumentException) {
        println("Error: ${e.message}")
        return
    } catch (e: InvalidConfigurationException) {
        println("Configuration file is invalid: ${e.message}")
        return
    }


}