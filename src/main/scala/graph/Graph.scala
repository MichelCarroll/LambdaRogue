package graph

import java.util.UUID

import scala.collection._

case class NodeID(value: UUID = UUID.randomUUID())
case class EdgeID(value: UUID = UUID.randomUUID())

class Graph[NodeAttr, EdgeAttr] {

  case class Node(attributes: NodeAttr)
  case class Edge(from: NodeID, to: NodeID, attributes: EdgeAttr)

  private[graph] val nodes = mutable.Map[NodeID, Node]()
  private[graph] val edges = mutable.Map[EdgeID, Edge]()

  private[graph] val edgeFromIndex = mutable.Map[NodeID, mutable.Set[EdgeID]]()
  private[graph] val edgeToIndex = mutable.Map[NodeID, mutable.Set[EdgeID]]()

  def add(attributes: NodeAttr): NodeID = {
    val id = NodeID()
    nodes += id -> Node(attributes)
    edgeFromIndex += id -> mutable.HashSet.empty
    edgeToIndex += id -> mutable.HashSet.empty
    id
  }

  def remove(id: NodeID): Unit = {
    nodes -= id
    edgeFromIndex -= id
    edgeToIndex -= id
  }

  def add(from: NodeID, attributes: EdgeAttr, to: NodeID): EdgeID = {
    val id = EdgeID()
    edges += id -> Edge(from, to, attributes)
    edgeFromIndex(from).add(id)
    edgeToIndex(to).add(id)
    id
  }

  def remove(id: EdgeID): Unit = {
    val from = edges(id).from
    val to = edges(id).to
    edgeFromIndex(from).remove(id)
    edgeToIndex(to).remove(id)
    edges -= id
  }
}