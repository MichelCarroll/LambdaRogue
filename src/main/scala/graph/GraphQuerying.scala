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


    def query(f: PartialFunction[(NodeID, EdgeID, NodeID), Unit]): Unit = {
      graph.edges.foreach(edge => f.lift(edge._2.from, edge._1, edge._2.to))
    }

    def queryFrom(nodeId: NodeID)(f: PartialFunction[(EdgeID, NodeID), Unit]): Unit = {
      graph.edgeFromIndex(nodeId).toSeq.foreach(edgeId => f.lift(edgeId, graph.edges(edgeId).to))
    }

    def queryTo(nodeId: NodeID)(f: PartialFunction[(NodeID, EdgeID), Unit]): Unit = {
      graph.edgeToIndex(nodeId).toSeq.foreach(edgeId => f.lift(graph.edges(edgeId).from, edgeId))
    }

    object From {
      def unapply[Edge](arg: (NodeID, EdgeID)): Option[(NodeID, NodeAttr, EdgeID, EdgeAttr)] = {
        Some(
          arg._1,
          graph.at(arg._1),
          arg._2,
          graph.at(arg._2)
        )
      }
    }

    object To {
      def unapply[Edge](arg: (EdgeID, NodeID)): Option[(EdgeID, EdgeAttr, NodeID, NodeAttr)] = {
        Some(
          arg._1,
          graph.at(arg._1),
          arg._2,
          graph.at(arg._2)
        )
      }
    }

    object FromTo {
      def unapply[Edge](arg: (NodeID, EdgeID, NodeID)): Option[(NodeID, NodeAttr, EdgeID, EdgeAttr, NodeID, NodeAttr)] = {
        Some(
          arg._1,
          graph.at(arg._1),
          arg._2,
          graph.at(arg._2),
          arg._3,
          graph.at(arg._3)
        )
      }
    }

  }

}
