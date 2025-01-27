import org.example.intersection.model.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.*

class RoadTest {

    @Test
    fun `constructor throws exception for invalid number of lanes`() {
        val exception = assertThrows<IllegalArgumentException> {
            Road(Direction.NORTH, 0)
        }
        assertEquals("Number of lanes must be greater than 0", exception.message)
    }

    @Test
    fun `constructor initializes single lane road correctly`() {
        val road = Road(Direction.NORTH, 1)

        assertEquals(1, road.rightLanes.size)
        assertEquals(1, road.straightLanes.size)
        assertEquals(1, road.leftLanes.size)
    }

    @Test
    fun `constructor initializes multiple lanes correctly`() {
        val road = Road(Direction.NORTH, 4, 0.5)

        assertEquals(1, road.leftLanes.size)
        assertEquals(2, road.straightLanes.size)
        assertEquals(1, road.rightLanes.size)
    }

    @Test
    fun `addVehicle assigns vehicle to correct lane`() {
        val road = Road(Direction.NORTH, 3)
        val vehicle = Vehicle("V1", Direction.NORTH.left())

        road.addVehicle(vehicle)

        assertEquals(1, road.leftLanes[0].vehiclesCount)
    }

    @Test
    fun `addToActiveLanes activates correct lanes`() {
        val road = Road(Direction.NORTH, 3)

        road.addToActiveLanes(listOf(Turn.LEFT))

        assertTrue(road.activeLanes.isNotEmpty())
    }

    @Test
    fun `clearActiveLanes clears all active lanes`() {
        val road = Road(Direction.NORTH, 3)

        road.addToActiveLanes(listOf(Turn.LEFT, Turn.RIGHT))
        road.clearActiveLanes()

        assertTrue(road.activeLanes.isEmpty())
        assertTrue(road.activeRightArrowLanes.isEmpty())
    }

    @Test
    fun `shouldExtendGreenLight returns true if any active lane exceeds threshold`() {
        val road = Road(Direction.NORTH, 3)
        val mockLane = mock<Lane> {
            on { vehiclesCount } doReturn 10
        }
        road.activeLanes.add(mockLane)

        assertTrue(road.shouldExtendGreenLight(5))
    }

    @Test
    fun `shouldExtendGreenLight returns false if no active lane exceeds threshold`() {
        val road = Road(Direction.NORTH, 3)
        val mockLane = mock<Lane> {
            on { vehiclesCount } doReturn 2
        }
        road.activeLanes.add(mockLane)

        assertFalse(road.shouldExtendGreenLight(5))
    }

    @Test
    fun `moveVehicles processes vehicles correctly`() {
        val road = Road(Direction.NORTH, 2)
        val mockLane = mock<Lane> {
            on { moveVehicle(any(), any()) } doReturn Vehicle("V1", Direction.NORTH.left())
        }
        road.activeLanes.add(mockLane)

        val movedVehicles = road.moveVehicles(canReleaseBuffer = true)

        assertEquals(1, movedVehicles.size)
        assertEquals("V1", movedVehicles[0].vehicleId)
    }


    @Test
    fun `canReleaseOppositeRoadBuffer returns true when appropriate`() {
        val road = Road(Direction.NORTH, 3)
        val mockLane = mock<Lane> {
            on { nextVehicleTurnsLeft() } doReturn true
        }
        road.activeLanes.add(mockLane)

        assertTrue(road.canReleaseOppositeRoadBuffer())
    }
}
