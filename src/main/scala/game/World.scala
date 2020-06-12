package game

import game.actions.{GameAction, MoveCharacter}
import graph.{GraphQuerying, NodeID}
import ui.{Coordinates, Size}

import scala.collection.mutable

class ZoneBuilder(graph: WorldGraph) {

  val zoneId: NodeID = graph.add(Zone)

  def square(position: Coordinates, size: Size): Unit = {
    for {
      _x <- position.x until position.x + size.width
      _y <- position.y until position.y + size.height
    } {
      graph.add(
        zoneId,
        PositionsAt(ZonePosition(_x,_y)),
        graph.add(Tile(Grass))
      )
    }
  }

}

class World extends GraphQuerying {

  private val graph = new WorldGraph
  var characterCreation = new CharacterCreation
  val R = graph.Result
  val renderMap: mutable.Map[ZonePosition, mutable.ArrayBuffer[RenderLayer]] =
    mutable.Map[ZonePosition, mutable.ArrayBuffer[RenderLayer]]()

  var currentZoneId: Option[NodeID] = None
  var currentCharacter: Option[NodeID] = None

  def initialize(): Unit = {
    updateGraph { () =>
      val builder = new ZoneBuilder(graph)
      builder.square(Coordinates(2,2), Size(5,5))
      currentZoneId = Some(builder.zoneId)

      graph.queryFrom(builder.zoneId).collect {
        case R(_, _, _, PositionsAt(ZonePosition(4,4)), tileId, _) =>
          val character = graph.add(characterCreation.build())
          currentCharacter = Some(character)
          graph.add(character, On, tileId)
      }
    }
  }

  def execute(gameAction: GameAction): Unit = gameAction match {
    case MoveCharacter(direction) =>
      updateGraph { () =>

        (currentCharacter, currentZoneId) match {
          case (Some(charId), Some(zoneId)) =>
            for {
              R(_,_,edgeId,_,oldTileId,_) <- graph
                .queryFrom(charId)
                .filter(_.edge == On)

              R(_,_,_,PositionsAt(zonePosition),_,_) <- graph
                .queryFrom(zoneId)
                .filter(_.toId == oldTileId)

              newPosition = zonePosition.displace(direction)

              R(_,_,_,_,newTileId,_) <- graph
                .queryFrom(zoneId)
                .filter(_.edge == PositionsAt(newPosition))

            } yield {
              graph.remove(edgeId)
              graph.add(charId, On, newTileId)
            }
          case _ =>
        }
      }
  }

  private def updateRenderMap(): Unit = {
    currentZoneId.foreach { zoneId =>

      renderMap.clear()

      def updateAtPosition(zonePosition: ZonePosition, tileId: NodeID): Unit = {
        graph.queryTo(tileId)
          .filter(_.edge == On)
          .map(_.from)
          .collect {
            case visible: Visible =>
              renderMap(zonePosition).append(visible.renderLayer)
          }
      }

      graph.queryFrom(zoneId).collect {
        case R(_,_,_,PositionsAt(zonePosition), id, visible: Visible) =>
          val buffer = new mutable.ArrayBuffer[RenderLayer]()
          buffer.append(visible.renderLayer)
          renderMap.update(zonePosition, buffer)
          updateAtPosition(zonePosition, id)
      }
    }
  }

  def updateGraph(f: () => Unit): Unit = {
    f()
    updateRenderMap()
  }

}
