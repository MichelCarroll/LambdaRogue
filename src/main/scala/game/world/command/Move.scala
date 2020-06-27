package game.world.command

import game.{PositionedAt, StandingOn, WorldGraph}
import graph.NodeID
import common._


class Move(val worldGraph: WorldGraph, targetId: NodeID, zoneId: NodeID, direction: Direction) extends WorldCommand[Unit] {

  def execute(): Unit = {
    for {
      R(_,_,edgeId,_,oldTileId,_) <- worldGraph
        .queryFrom(targetId)
        .filter(_.edge == StandingOn)

      R(_,_,_,PositionedAt(zonePosition),_,_) <- worldGraph
        .queryTo(zoneId)
        .filter(_.fromId == oldTileId)

      newPosition = zonePosition.displaced(direction)

      R(newTileId,_,_,_,_,_) <- worldGraph
        .queryTo(zoneId)
        .filter(_.edge == PositionedAt(newPosition))

    } yield {
      worldGraph.remove(edgeId)
      worldGraph.add(targetId, StandingOn, newTileId)
    }
  }

}
