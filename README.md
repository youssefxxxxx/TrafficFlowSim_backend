# Traffic Simulator

**Java-based traffic simulation app** to model vehicles, roads, junctions, and events via a textual interface.


**instructions are here :https://github.com/genaim/TP2_2425/blob/master/practicas/P2/enunciado_en.md.

## JSON Example

```json
{
  "type": "new_vehicle",
  "data": {
    "time": 1,
    "id": "v1",
    "maxspeed": 100,
    "class": 3,
    "itinerary": ["j3", "j1", "j5", "j4"]
  }
}
