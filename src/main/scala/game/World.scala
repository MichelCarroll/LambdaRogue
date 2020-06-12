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

  val renderMap: mutable.Map[ZonePosition, mutable.ArrayBuffer[RenderLayer]] =
    mutable.Map[ZonePosition, mutable.ArrayBuffer[RenderLayer]]()

  var currentZoneId: Option[NodeID] = None
  var currentCharacter: Option[NodeID] = None

  def initialize(): Unit = {
    updateGraph { graph =>

      val builder = new ZoneBuilder(graph)
      builder.square(Coordinates(2,2), Size(5,5))
      currentZoneId = Some(builder.zoneId)

      graph.queryFrom(builder.zoneId) {
        case graph.To(_, PositionsAt(ZonePosition(4,4)), tileId, _) =>
          val character = graph.add(characterCreation.build())
          currentCharacter = Some(character)
          graph.add(character, On, tileId)
      }
    }
  }

  def execute(gameAction: GameAction): Unit = gameAction match {
    case MoveCharacter(direction) =>
      updateGraph { graph =>
        (currentCharacter, currentZoneId) match {
          case (Some(charId), Some(zoneId)) =>
            graph.queryFrom(charId) {
              case graph.To(edgeId, On, oldTileId, _) =>
                graph.remove(edgeId)
                graph.queryFrom(zoneId) {
                  case graph.To(_, PositionsAt(zonePosition), `oldTileId`, _) =>
                    val newPosition = zonePosition.displace(direction)
                    graph.queryFrom(zoneId) {
                      case graph.To(_, PositionsAt(`newPosition`), newTileId, _) =>
                        graph.add(charId, On, newTileId)
                    }
                }
            }
          case _ =>
        }
      }
  }

  private def updateRenderMap(): Unit = {
    currentZoneId.foreach { zoneId =>

      renderMap.clear()

      def updateAtPosition(zonePosition: ZonePosition, tileId: NodeID): Unit = {
        graph.queryTo(tileId) {
          case graph.From(_ , visible: Visible, _, On) =>
            renderMap(zonePosition).append(visible.renderLayer)
        }
      }

      graph.queryFrom(zoneId) {
        case graph.To( _, PositionsAt(zonePosition), id, visible: Visible) =>
          val buffer = new mutable.ArrayBuffer[RenderLayer]()
          buffer.append(visible.renderLayer)
          renderMap.update(zonePosition, buffer)
          updateAtPosition(zonePosition, id)
      }
    }
  }

  def updateGraph(f: WorldGraph => Unit): Unit = {
    f(graph)
    updateRenderMap()
  }

}
