package game.world.command

import common._
import game.{PositionedAt, StandingOn, WorldGraph, Entity}
import graph.NodeID


class Materialize(val worldGraph: WorldGraph, zoneId: NodeID, coordinates: Coordinates, entity: Entity) extends WorldCommand[Option[NodeID]] {

  def execute(): Option[NodeID] = {
    worldGraph.queryTo(zoneId).collectFirst {
      case R(tileId, _, _, PositionedAt(`coordinates`), _, _) =>
        val character = worldGraph.add(entity)
        worldGraph.add(character, StandingOn, tileId)
        character
    }
  }

}
