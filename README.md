# IntersectionSolver

IntersectionSolver is a simulation tool designed to optimize traffic flow at cross intersections by dynamically controlling
traffic lights and vehicle movements.

## Additional simulation features

- Multiple lanes:
    - Each lane has its own traffic light.
    - Percentage of turning lanes can be set in the configuration JSON.
    - Vehicles choose the lane with the shortest queue.
- Dynamic green light duration:
    - Green light can be extended if in any of active lanes there are more vehicles than green light time left.
    - Green light extension can be set in the configuration JSON.
- Right turn arrow:
    - When right turn arrow light is turned on, vehicles can turn right even if the main light is red (with lowest
      priority).

## Solution

1. Input, and optional configuration are parsed from JSON files.
2. Roads are created based on the number of lanes specified in the configuration (default 1)
3. Intersection is initialized with initial order (based on the first vehicle added to the intersection).
4. For every command from input:
    - addVehicle: Vehicle is created and added to the correct starting road.
    - step:
        1. Based on the current green light duration, we either:
            - Turn on the green lights for lanes on current road in queue based on current light mode. Light modes:
                - (left, straight, right) + perpendicular roads right arrows (1+ lanes, if number of lanes is 1, also
                  turn on opposite road lights)
                - (straight, right) + opposite road (straight, right) + perpendicular roads right arrows (2+ lanes)
                - (left) + opposite road (left) + perpendicular roads right arrows (3+ lanes)
            - Extend the current green light duration if condition is met.
            - Turn on yellow light and turn off right arrows on last but one green light duration.
            - Turn on red light on last green light duration, switch the light mode. If all the modes were used, set the
              current rode to the one right of the current road.

        2. On every step that light is not yellow or red, move the vehicles with according priority.
        3. If the vehicle reaches the end road, remove it from the intersection, and add to the output.
5. Output is written to specified output JSON file.

For one lane road, there is implemented a single car buffer on the "middle" of the intersection, which allows vehicles
drive straight or right, when vehicle in front of that wants to turn left and waits for empty opposite road.

## Prerequisites
- JDK 21
- Gradle (for running tests)

## How to run

1. Clone the repository or download the IntersectionSolver.jar file.
2. Run the following command:

```shell
java -jar <path_to_IntersectionSolver.jar> <path_to_input_json> <path_to_output_json> (optional: <path_to_configuration_json>)
```

## How to run tests

1. Clone the repository.
2. Run the following command in the project root directory:

```shell
./gradlew test
```

## Example configuration

```json
{
  "numberOfLanes": 1,
  "greenLightDuration": 6,
  "greenLightExtension": 2,
  "turnLanesPercentage": 0.5,
  "addVehicleCommandString": "addVehicle",
  "simulationStepCommandString": "step"
}
```

## Example input

```json
{
  "commands": [
    {
      "type": "addVehicle",
      "vehicleId": "vehicle1",
      "startRoad": "south",
      "endRoad": "north"
    },
    {
      "type": "addVehicle",
      "vehicleId": "vehicle2",
      "startRoad": "north",
      "endRoad": "south"
    },
    {
      "type": "step"
    },
    {
      "type": "step"
    },
    {
      "type": "addVehicle",
      "vehicleId": "vehicle3",
      "startRoad": "west",
      "endRoad": "south"
    },
    {
      "type": "addVehicle",
      "vehicleId": "vehicle4",
      "startRoad": "west",
      "endRoad": "south"
    },
    {
      "type": "step"
    },
    {
      "type": "step"
    }
  ]
}
```

## Example output

```json
{
  "stepStatuses": [
    {
      "vehiclesLeft": [
        "vehicle1",
        "vehicle2"
      ]
    },
    {
      "vehiclesLeft": []
    },
    {
      "vehiclesLeft": [
        "vehicle3"
      ]
    },
    {
      "vehiclesLeft": [
        "vehicle4"
      ]
    }
  ]
}
```

## Future plans

Originally, in addition to this solution, I wanted to create simple backend API with Spring Boot and visualization with
React, but due to time constrains (I am currently in the middle of my exam period), I had to skip this (for now). 