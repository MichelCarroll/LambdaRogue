package game.world.command

import common._
import game._
import graph._

case class InspectionResult(entities: Iterable[Visible])

class Inspect(val worldGraph: WorldGraph, zoneId: NodeID, coordinates: Coordinates) extends WorldCommand[InspectionResult] {

  def execute(): InspectionResult = {

    worldGraph
      .queryTo(zoneId)
      .find(_.edge == PositionedAt(`coordinates`))
      .collectFirst {
        case R(tileId, tile: Visible, _, _, _, _) =>
          val otherEntities =
            worldGraph
              .queryTo(tileId)
              .filter(_.edge == StandingOn)
              .collect { case R(_, visible: Visible, _, _, _, _) => visible }
              .toSeq

          InspectionResult(otherEntities :+ tile)
      }
      .getOrElse(InspectionResult(Seq()))
  }

}
