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
    graph.queryTo(node).collect {
      case R(containerId,_,_,Contains,_,_) =>
        traverse(containerId)
      case R(_,_,_,Positions(position),tileId,_) =>
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
      graph.queryFrom(tileId)
        .filter(_.edge == Contains)
        .map(_.to)
        .collect {
          case visible: Visible =>
            renderMap(zonePosition).append(visible.renderLayer)
        }
    }
    tileToUpdate.clear()
  }

  def updateWholeRenderMap(): Unit = {
    renderMap.clear()

    graph.queryFrom(zoneId).collect {
      case R(_,_,_,Positions(zonePosition), tileId, visible: Visible) =>
        val buffer = new mutable.ArrayBuffer[RenderLayer]()
        buffer.append(visible.renderLayer)
        renderMap.update(zonePosition, buffer)
        graph.queryFrom(tileId)
          .filter(_.edge == Contains)
          .map(_.to)
          .collect {
            case visible: Visible =>
              renderMap(zonePosition).append(visible.renderLayer)
          }
    }
  }

}

class World extends GraphQuerying {


  val renderMap: RenderMap = mutable.Map[Coordinates, mutable.ArrayBuffer[RenderLayer]]()

  private val graph = new WorldGraph
  var graphOperator: Option[SmartGraphOperator] = None
  var characterCreation = new CharacterCreation
  val R = graph.Result


  var currentZoneId: Option[NodeID] = None
  var currentCharacter: Option[NodeID] = None

  def initialize(): Unit = {
    val builder = new ZoneBuilder(graph)
    builder.square(Area(Coordinates(0,0), Size(100,100)))
    currentZoneId = Some(builder.zoneId)

    graph.queryFrom(builder.zoneId).collect {
      case R(_, _, _, Positions(Coordinates(4,4)), tileId, _) =>
        val character = graph.add(characterCreation.build())
        currentCharacter = Some(character)
        graph.add(tileId, Contains, character)
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
              R(oldTileId,_,edgeId,_,_,_) <- graph
                .queryTo(charId)
                .filter(_.edge == Contains)

              R(_,_,_,Positions(zonePosition),_,_) <- graph
                .queryFrom(zoneId)
                .filter(_.toId == oldTileId)

              newPosition = zonePosition.displaced(direction)

              R(_,_,_,_,newTileId,_) <- graph
                .queryFrom(zoneId)
                .filter(_.edge == Positions(newPosition))

            } yield {
              graphOperator.foreach { g =>
                g.remove(edgeId)
                g.add(newTileId, Contains, charId)
              }
            }
          case _ =>
        }
    }
    graphOperator.foreach(_.executeRenderMapTransaction())
  }

}
