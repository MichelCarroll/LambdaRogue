package game

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
        PositionedAt(ZonePosition(_x,_y)),
        graph.add(Tile(Grass))
      )
    }
  }

}

class World {

  private val graph = new WorldGraph
  var characterCreation = new CharacterCreation

  val renderMap: mutable.Map[ZonePosition, mutable.ArrayBuffer[RenderLayer]] =
    mutable.Map[ZonePosition, mutable.ArrayBuffer[RenderLayer]]()

  var currentZoneId: Option[NodeID] = None

  private def tileAt(zoneId: NodeID, zonePosition: ZonePosition): Option[NodeID] =
    graph.from(zoneId).collectFirst {
      case (tileId, PositionedAt(pos)) if pos == zonePosition =>
        tileId
    }

  def initialize(): Unit = {
    updateGraph { graph =>

      val builder = new ZoneBuilder(graph)
      builder.square(Coordinates(2,2), Size(5,5))
      currentZoneId = Some(builder.zoneId)

      tileAt(builder.zoneId, ZonePosition(4,4)).foreach { tileId =>
        val character = graph.add(characterCreation.build())
        graph.add(character, On, tileId)
      }

    }
  }

  private def updateRenderMap(): Unit = {
    currentZoneId.foreach { zoneId =>

      renderMap.clear()

      def updateAtPosition(zonePosition: ZonePosition, parentNodeId: NodeID): Unit = {
        graph.to(parentNodeId).collect {
          case (id, On) =>
            graph.at(id) match {
              case visible: Visible =>
                renderMap(zonePosition).append(visible.renderLayer)
              case _ =>
            }
        }
      }

      graph.from(zoneId).collect {
        case (toId, PositionedAt(zonePosition)) =>
          graph.at(toId) match {
            case visible: Visible =>
              val buffer = new mutable.ArrayBuffer[RenderLayer]()
              buffer.append(visible.renderLayer)
              renderMap.update(zonePosition, buffer)
              updateAtPosition(zonePosition, toId)
            case _ =>
          }
      }
    }
  }

  def updateGraph(f: WorldGraph => Unit): Unit = {
    f(graph)
    updateRenderMap()
  }

}
