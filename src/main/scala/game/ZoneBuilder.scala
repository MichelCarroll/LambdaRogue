package game

import common.Area
import generator.TerrainGenerator
import graph.NodeID
import random.{Cloudy, PerlinNoise2D}

class ZoneBuilder(graph: WorldGraph) {

  val zoneId: NodeID = graph.add(Zone)

  def square(area: Area): Unit = {
    val terrainGenerator = new TerrainGenerator()
    val perlinNoise2D = new PerlinNoise2D(Cloudy, resolution = 500)
    terrainGenerator.make(area, perlinNoise2D, zoneId, graph)
  }

}
