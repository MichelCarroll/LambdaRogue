package graph

import scala.collection.{Iterable, Set}

trait GraphQuerying {

  implicit class GraphPimp[NodeAttr, EdgeAttr](graph: Graph[NodeAttr, EdgeAttr]) {

    def nodeCount: Int = graph.nodes.size
    def edgeCount: Int = graph.edges.size

    def at(id: NodeID): NodeAttr = {
      graph.nodes(id).attributes
    }

    def at(id: EdgeID): EdgeAttr = {
      graph.edges(id).attributes
    }

    def to(id: NodeID): Set[(NodeID, EdgeAttr)] = {
      graph.edgeToIndex(id)
        .map { edgeId =>
          val edge = graph.edges(edgeId)
          edge.from -> edge.attributes
        }
    }

    def from(id: NodeID): Set[(NodeID, EdgeAttr)] = {
      graph.edgeFromIndex(id)
        .map { edgeId =>
          val edge = graph.edges(edgeId)
          edge.to -> edge.attributes
        }
    }


    private def mapToResult(edgeId: EdgeID): Result = {
      val edge = graph.edges(edgeId)
      Result(
        edge.from,
        graph.nodes(edge.from).attributes,
        edgeId,
        edge.attributes,
        edge.to,
        graph.nodes(edge.to).attributes
      )
    }

    def queryFrom(nodeId: NodeID): Iterable[Result] = {
      graph.edgeFromIndex(nodeId).map(mapToResult)
    }

    def queryTo(nodeId: NodeID): Iterable[Result] = {
      graph.edgeToIndex(nodeId).map(mapToResult)
    }

    case class Result(
       fromId: NodeID,
       from: NodeAttr,
       edgeId: EdgeID,
       edge: EdgeAttr,
       toId: NodeID,
       to: NodeAttr,
     )

  }

}
