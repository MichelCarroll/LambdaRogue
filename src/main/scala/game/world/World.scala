package game.world

import common._
import game._
import game.actions.{GameAction, MoveCharacter}
import game.world.command.Move
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

class World(implicit gameSettings: GameSettings) extends GraphQuerying {

  val renderMap: RenderMap = mutable.Map[Coordinates, mutable.ArrayBuffer[RenderLayer]]()
  private val graph = new CachedWorldGraph(renderMap)
  var characterCreation = new CharacterCreation
  val R = graph.Result

  var currentZoneId: Option[NodeID] = None
  var currentCharacter: Option[NodeID] = None

  def initialize(): Unit = {
    val builder = new ZoneBuilder(graph)
    builder.square(Area(Coordinates(0,0), gameSettings.zoneSize))
    currentZoneId = Some(builder.zoneId)

    graph.queryTo(builder.zoneId).collect {
      case R(tileId, _, _, PositionedAt(Coordinates(4,4)), _, _) =>
        val character = graph.add(characterCreation.build())
        currentCharacter = Some(character)
        graph.add(character, StandingOn, tileId)
    }
    graph.updateWholeRenderMap(builder.zoneId)
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
