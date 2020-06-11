package game

import scala.collection.mutable


class World {

  private val graph = new WorldGraph
  var characterCreation = new CharacterCreation

  val renderMap: mutable.Map[ZonePosition, mutable.ArrayBuffer[RenderLayer]] =
    mutable.Map[ZonePosition, mutable.ArrayBuffer[RenderLayer]]()

  var currentZoneId: Option[NodeID] = None

  def initialize(): Unit = {
    updateGraph { graph =>
      val zone = graph.add(Zone)
      val tile = graph.add(GrassTile)
      graph.add(zone, PositionedAt(ZonePosition(1,1)), tile)

      currentZoneId = Some(zone)

      val character = graph.add(characterCreation.build())
      graph.add(character, On, tile)
    }
  }

  private def updateRenderMap(): Unit = {
    currentZoneId.foreach { zoneId =>

      def updateAtPosition(zonePosition: ZonePosition, parentNodeId: NodeID): Unit = {
        println("CALLING TO")
        println(zonePosition)
        graph.to(parentNodeId).collect {
          case (id, On) =>
            println("TRYING TO MAKE THIS VISIBLE")
            println(id)
            graph.at(id) match {
              case visible: Visible =>
                println("ITS VISIBLE")
                println(visible)
                println(visible.renderLayer)
                renderMap(zonePosition).append(visible.renderLayer)
              case _ =>
            }
        }
      }

      renderMap.clear()
      println("CALLING FROM")
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
