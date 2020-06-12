package game

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

  def initialize(): Unit = {
    updateGraph { graph =>

      val builder = new ZoneBuilder(graph)
      builder.square(Coordinates(2,2), Size(5,5))
      currentZoneId = Some(builder.zoneId)

      graph.queryFrom(builder.zoneId) {
        case graph.To(_, PositionsAt(ZonePosition(4,4)), tileId, _) =>
          val character = graph.add(characterCreation.build())
          graph.add(character, On, tileId)
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
