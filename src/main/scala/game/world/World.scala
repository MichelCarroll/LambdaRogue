package game.world

import common._
import game._
import game.actions.{GameAction, MoveCharacter}
import game.world.command.{Inspect, InspectionResult, Materialize, Move}
import graph.{GraphQuerying, NodeID}

import scala.collection.mutable

class World(implicit gameSettings: GameSettings) extends GraphQuerying {

  val renderMap: RenderMap = mutable.Map[Coordinates, mutable.ArrayBuffer[RenderLayer]]()
  private val graph = new CachedWorldGraph(renderMap)
  var characterCreation = new CharacterCreation

  var currentZoneId: Option[NodeID] = None
  var currentCharacter: Option[NodeID] = None

  def initialize(): Unit = {
    val builder = new ZoneBuilder(graph)
    builder.square(Area(Coordinates(0,0), gameSettings.zoneSize))
    currentZoneId = Some(builder.zoneId)
    currentCharacter = new Materialize(graph, builder.zoneId, Coordinates(4,4), characterCreation.build()).execute()
    graph.updateWholeRenderMap(builder.zoneId)
  }

  def inspect(coordinates: Coordinates): Option[InspectionResult] = {
    currentZoneId.map { zoneId =>
      new Inspect(graph, zoneId, coordinates).execute()
    }
  }

  def execute(gameAction: GameAction): Unit = {
    gameAction match {
      case MoveCharacter(direction) =>
        (currentCharacter, currentZoneId) match {
          case (Some(charId), Some(zoneId)) =>
            new Move(graph, charId, zoneId, direction).execute()
          case _ =>
        }

    }
    graph.executeRenderMapTransaction()
  }

}
