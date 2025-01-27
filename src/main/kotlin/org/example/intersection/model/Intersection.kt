package org.example.intersection.model

class Intersection(
    private val roads: List<Road>,
    private var greenLightDuration: Int,
    private val greenLightExtension: Int,
    private val numberOfLanes: Int
) {

    private var currentRoadIndex = 0
    private var currentStrategyIndex = 0
    private var currentGreenLightTime = greenLightDuration
    private var greenLightExtended = false

    private val lightsStrategy = listOf(
        listOf(Turn.LEFT, Turn.STRAIGHT, Turn.RIGHT),
        listOf(Turn.STRAIGHT, Turn.RIGHT),
        listOf(Turn.LEFT)
    )

    fun performStep(): List<Vehicle> {
        println("--------STEP--------")
        val movedVehicles = mutableListOf<Vehicle>()
        when {
            currentGreenLightTime == greenLightDuration && !greenLightExtended -> {
                switchToGreenLights()
                movedVehicles.addAll(roads.flatMap { it.moveVehicles(canReleaseBuffer(it)) })
                roads.forEach { it.finalizeRemovingMovedVehicles() }
            }

            currentGreenLightTime == greenLightDuration / 2 -> {
                maybeExtendGreenLight()
                movedVehicles.addAll(roads.flatMap { it.moveVehicles(canReleaseBuffer(it)) })
                roads.forEach { it.finalizeRemovingMovedVehicles() }
            }

            currentGreenLightTime == 1 -> switchToYellowLights()
            currentGreenLightTime == 0 -> resetCycle()
            else -> {
                movedVehicles.addAll(roads.flatMap { it.moveVehicles(canReleaseBuffer(it)) })
                roads.forEach { it.finalizeRemovingMovedVehicles() }
                currentGreenLightTime--
            }
        }

    return movedVehicles
}

private fun canReleaseBuffer(road: Road): Boolean {
    // this problem is relevant only for 1 lane roads
    if (numberOfLanes == 1) return roads[(roads.indexOf(road) + 2) % roads.size].canReleaseOppositeRoadBuffer()
    return true
}

private fun maybeExtendGreenLight() {
    if (!greenLightExtended && roads.any { it.shouldExtendGreenLight(greenLightDuration / 2) }) {
        currentGreenLightTime += greenLightExtension
        greenLightExtended = true
    }
    currentGreenLightTime--
}

private fun switchToGreenLights() {
    val turns = lightsStrategy[currentStrategyIndex]
    roads[currentRoadIndex].addToActiveLanes(turns)
    if (currentStrategyIndex != 0 || numberOfLanes == 1) {
        roads[(currentRoadIndex + 2) % roads.size].addToActiveLanes(turns) // opposite road
    }

    val leftRoad = roads[((currentRoadIndex - 1 + roads.size) % roads.size)]
    val rightRoad = roads[(currentRoadIndex + 1) % roads.size]
    val isRightArrowTurn = numberOfLanes < 3

    leftRoad.addToActiveLanes(listOf(Turn.RIGHT), isRightArrowTurn)
    rightRoad.addToActiveLanes(listOf(Turn.RIGHT), isRightArrowTurn)


    roads.forEach {
        it.activeTrafficLightsNextState()
        it.activeRightArrowLightsNextState()
    }

    currentGreenLightTime--
}

private fun switchToYellowLights() {
    roads.forEach {
        it.activeTrafficLightsNextState()
        it.activeRightArrowLightsNextState()
    }

    currentGreenLightTime--
}

private fun resetCycle() {
    roads.forEach {
        it.activeTrafficLightsNextState()
        it.clearActiveLanes()
    }

    currentGreenLightTime = greenLightDuration
    greenLightExtended = false

    currentRoadIndex = when {
        numberOfLanes == 1 -> (currentRoadIndex + 1) % roads.size
        numberOfLanes == 2 && currentStrategyIndex == 1 -> (currentRoadIndex + 1) % roads.size
        numberOfLanes > 2 && currentStrategyIndex == 2 -> (currentRoadIndex + 1) % roads.size
        else -> currentRoadIndex
    }

    currentStrategyIndex = when (numberOfLanes) {
        1 -> 0
        2 -> (currentStrategyIndex + 1) % 2
        else -> (currentStrategyIndex + 1) % lightsStrategy.size
    }
}

}