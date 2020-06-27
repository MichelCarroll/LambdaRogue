package game

import game.actions.{GameAction, MoveCharacter}
import graph.{EdgeID, GraphLike, GraphQuerying, NodeID}
import common._
import generator.TerrainGenerator
import random.{Cloudy, PerlinNoise2D}

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

class ZoneBuilder(graph: WorldGraph) {

  val zoneId: NodeID = graph.add(Zone)

  def square(area: Area): Unit = {
    val terrainGenerator = new TerrainGenerator()
    val perlinNoise2D = new PerlinNoise2D(Cloudy, resolution = 500)
    terrainGenerator.make(area, perlinNoise2D, zoneId, graph)
  }

}

class SmartGraphOperator(zoneId: NodeID, renderMap: RenderMap, graph: WorldGraph) extends GraphQuerying  {

  val R = graph.Result

  def add(attributes: Entity): NodeID = graph.add(attributes)
  def remove(id: NodeID): Unit = graph.remove(id)

  private def traverse(node: NodeID): Unit = {
    graph.queryFrom(node).collect {
      case R(_,_,_,StandingOn,containerId,_) =>
        traverse(containerId)
      case R(tileId,_,_,PositionedAt(position),_,_) =>
        tileToUpdate.add((tileId, position))
    }
  }

  def add(from: NodeID, attributes: Relationship, to: NodeID): EdgeID = {
    val edgeId = graph.add(from, attributes, to)
    traverse(from)
    traverse(to)
    edgeId
  }

  def remove(id: EdgeID): Unit = {
    val edge = graph.at(id)
    graph.remove(id)
    traverse(edge.to)
    traverse(edge.from)
  }

  var tileToUpdate = new mutable.HashSet[(NodeID, Coordinates)]()

  def executeRenderMapTransaction(): Unit = {
    tileToUpdate.foreach { case (tileId, zonePosition) =>
      val tile = graph.at(tileId).asInstanceOf[Tile]
      val buffer = new mutable.ArrayBuffer[RenderLayer]()
      buffer.append(tile.renderLayer)
      renderMap.update(zonePosition, buffer)
      graph.queryTo(tileId)
        .filter(_.edge == StandingOn)
        .map(_.from)
        .collect {
          case visible: Visible =>
            renderMap(zonePosition).append(visible.renderLayer)
        }
    }
    tileToUpdate.clear()
  }

  def updateWholeRenderMap(): Unit = {
    renderMap.clear()

    graph.queryTo(zoneId).collect {
      case R(tileId, visible: Visible,_,PositionedAt(zonePosition), _, _) =>
        val buffer = new mutable.ArrayBuffer[RenderLayer]()
        buffer.append(visible.renderLayer)
        renderMap.update(zonePosition, buffer)
        graph.queryTo(tileId)
          .filter(_.edge == StandingOn)
          .map(_.from)
          .collect {
            case visible: Visible =>
              renderMap(zonePosition).append(visible.renderLayer)
          }
    }
  }

}

case class InspectionResult()

class World(implicit gameSettings: GameSettings) extends GraphQuerying {

  val renderMap: RenderMap = mutable.Map[Coordinates, mutable.ArrayBuffer[RenderLayer]]()

  private val graph = new WorldGraph
  var graphOperator: Option[SmartGraphOperator] = None
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
    val operator = new SmartGraphOperator(builder.zoneId, renderMap, graph)
    graphOperator = Some(operator)
    operator.updateWholeRenderMap()
  }

  def execute(gameAction: GameAction): Unit = {
    gameAction match {
      case MoveCharacter(direction) =>
        (currentCharacter, currentZoneId) match {
          case (Some(charId), Some(zoneId)) =>
            for {
              R(_,_,edgeId,_,oldTileId,_) <- graph
                .queryFrom(charId)
                .filter(_.edge == StandingOn)

              R(_,_,_,PositionedAt(zonePosition),_,_) <- graph
                .queryTo(zoneId)
                .filter(_.fromId == oldTileId)

              newPosition = zonePosition.displaced(direction)

              R(newTileId,_,_,_,_,_) <- graph
                .queryTo(zoneId)
                .filter(_.edge == PositionedAt(newPosition))

            } yield {
              graphOperator.foreach { g =>
                g.remove(edgeId)
                g.add(charId, StandingOn, newTileId)
              }
            }
          case _ =>
        }
    }
    graphOperator.foreach(_.executeRenderMapTransaction())
  }

}
