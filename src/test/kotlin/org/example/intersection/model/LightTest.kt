import org.example.intersection.model.*
import org.example.intersection.model.lightstate.RightArrowLightState
import org.example.intersection.model.lightstate.TrafficLightState
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class LightTest {


    @Test
    fun `nextState cycles through traffic light states correctly`() {
        val light = Light(TrafficLightState.RED)

        light.nextState()
        assertEquals(TrafficLightState.GREEN, light.currentState)

        light.nextState()
        assertEquals(TrafficLightState.YELLOW, light.currentState)

        light.nextState()
        assertEquals(TrafficLightState.RED, light.currentState)
    }

    @Test
    fun `nextState cycles through right arrow light states correctly`() {
        val light = Light(RightArrowLightState.OFF)

        light.nextState()
        assertEquals(RightArrowLightState.ON, light.currentState)

        light.nextState()
        assertEquals(RightArrowLightState.OFF, light.currentState)
    }

}
