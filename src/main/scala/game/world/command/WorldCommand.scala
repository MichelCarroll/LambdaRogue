package game.world.command

import game.WorldGraph
import graph.GraphQuerying

trait WorldCommand[T] extends GraphQuerying {
  val worldGraph: WorldGraph
  val R = worldGraph.Result
  def execute(): T
}
