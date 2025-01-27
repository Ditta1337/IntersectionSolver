package org.example.intersection.model

import com.fasterxml.jackson.annotation.JsonCreator

enum class Direction {
    NORTH, SOUTH, EAST, WEST;

    companion object {
        @JvmStatic
        @JsonCreator
        fun fromString(direction: String): Direction {
            return entries.find { it.name.equals(direction, ignoreCase = true) }
                ?: throw IllegalArgumentException("Cannot map: \"$direction\" to a Direction Enum")
        }
    }

    fun left(): Direction {
        return when (this) {
            NORTH -> EAST
            SOUTH -> WEST
            EAST -> SOUTH
            WEST -> NORTH
        }
    }

    fun opposite(): Direction {
        return when (this) {
            NORTH -> SOUTH
            SOUTH -> NORTH
            EAST -> WEST
            WEST -> EAST
        }
    }

    fun right(): Direction {
        return when (this) {
            NORTH -> WEST
            SOUTH -> EAST
            EAST -> NORTH
            WEST -> SOUTH
        }
    }
}