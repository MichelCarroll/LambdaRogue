package game.world

import common.Coordinates
import game._
import graph.{EdgeID, GraphQuerying, NodeID}

import scala.collection.mutable

class CachedWorldGraph(renderMap: RenderMap) extends WorldGraph with GraphQuerying {

  val R = this.Result

  private def traverse(node: NodeID): Unit = {
    this.queryFrom(node).collect {
      case R(_,_,_,StandingOn,containerId,_) =>
        traverse(containerId)
      case R(tileId,_,_,PositionedAt(position),_,_) =>
        tileToUpdate.add((tileId, position))
    }
  }

  override def add(from: NodeID, attributes: Relationship, to: NodeID): EdgeID = {
    val edgeId = super.add(from, attributes, to)
    traverse(from)
    traverse(to)
    edgeId
  }

  override def remove(id: EdgeID): Unit = {
    val edge = this.at(id)
    super.remove(id)
    traverse(edge.to)
    traverse(edge.from)
  }

  var tileToUpdate = new mutable.HashSet[(NodeID, Coordinates)]()

  def executeRenderMapTransaction(): Unit = {
    tileToUpdate.foreach { case (tileId, zonePosition) =>
      val visible = this.at(tileId).asInstanceOf[Tile]
      updateRenderMap(visible, tileId, zonePosition)
    }
    tileToUpdate.clear()
  }


  private def updateRenderMap(tile: Tile, tileId: NodeID, zonePosition: Coordinates): Unit = {
    val buffer = new mutable.ArrayBuffer[RenderLayer]()
    buffer.append(tile.renderLayer)
    renderMap.update(zonePosition, buffer)
    this.queryTo(tileId)
      .filter(_.edge == StandingOn)
      .map(_.from)
      .collect {
        case visible: Visible =>
          renderMap(zonePosition).append(visible.renderLayer)
      }
  }

  def updateWholeRenderMap(zoneId: NodeID): Unit = {
    renderMap.clear()

    this.queryTo(zoneId).collect {
      case R(tileId, tile: Tile,_,PositionedAt(zonePosition), _, _) =>
        updateRenderMap(tile, tileId, zonePosition)
    }
  }

}
