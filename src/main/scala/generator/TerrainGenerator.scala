package generator

import common._
import game._
import graph.NodeID
import random.NoiseGenerator

class TerrainGenerator {

  def make(area: Area, noiseGenerator: NoiseGenerator, zoneId: NodeID, graph: WorldGraph): Unit = {
    for { point <- area.points } {
      val grassContent = noiseGenerator.valueAt(point.x, point.y)
      val tileMaterial = grassContent match {
        case x if x < 0.5 => Dirt
        case x if x < 0.55 => Grass(Low)
        case x if x < 0.6 => Grass(Medium)
        case x if x < 0.65 => Grass(High)
        case x if x <= 1.0 => Grass(Full)
      }
      graph.add(
        graph.add(Tile(tileMaterial)),
        PositionedAt(point),
        zoneId
      )
    }
  }

}
