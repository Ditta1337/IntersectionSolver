package org.example.intersection.model

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

class DirectionTest {
    @Test
    fun testLeft() {
        assertEquals(Direction.EAST, Direction.NORTH.left())
        assertEquals(Direction.WEST, Direction.SOUTH.left())
        assertEquals(Direction.SOUTH, Direction.EAST.left())
        assertEquals(Direction.NORTH, Direction.WEST.left())
    }

    @Test
    fun testOpposite() {
        assertEquals(Direction.SOUTH, Direction.NORTH.opposite())
        assertEquals(Direction.NORTH, Direction.SOUTH.opposite())
        assertEquals(Direction.WEST, Direction.EAST.opposite())
        assertEquals(Direction.EAST, Direction.WEST.opposite())
    }

    @Test
    fun testRight() {
        assertEquals(Direction.WEST, Direction.NORTH.right())
        assertEquals(Direction.EAST, Direction.SOUTH.right())
        assertEquals(Direction.NORTH, Direction.EAST.right())
        assertEquals(Direction.SOUTH, Direction.WEST.right())
    }

    @Test
    fun testFromString() {
        assertEquals(Direction.NORTH, Direction.fromString("north"))
        assertEquals(Direction.SOUTH, Direction.fromString("sOuth"))
        assertEquals(Direction.EAST, Direction.fromString("easT"))
        assertEquals(Direction.WEST, Direction.fromString("weSt"))
    }
}