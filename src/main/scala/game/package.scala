import graph.Graph
import common._
import org.scalajs.dom.ext.Color

import scala.collection.mutable

package object game extends ColorOperations {

  sealed trait RenderLayer
  case class MediumSquare(color: Color) extends RenderLayer
  case class FullSquare(color: Color) extends RenderLayer

  trait Visible {
    def renderLayer: RenderLayer
  }

  sealed trait Gender
  case object Male extends Gender
  case object Female extends Gender

  sealed trait CharacterBackground
  case object Soldier extends CharacterBackground
  case object Nomad extends CharacterBackground
  case object Merchant extends CharacterBackground

  sealed trait Entity
  case object Zone extends Entity

  sealed trait TileDensity {
    val percentage: Double
  }
  case object Low extends TileDensity {
    val percentage = 0.3
  }
  case object Medium extends TileDensity {
    val percentage = 0.5
  }
  case object High extends TileDensity {
    val percentage = 0.8
  }
  case object Full extends TileDensity {
    val percentage = 1.0
  }

  sealed trait TileMaterial {
    val color: Color
  }
  case object Dirt extends TileMaterial {
    val color: Color = Color(86,67,52)
  }
  case class Grass(density: TileDensity) extends TileMaterial {
    val color: Color = lerp(Dirt.color, Color(117,137,24), density.percentage)
  }

  case class Tile(tileMaterial: TileMaterial) extends Entity with Visible {
    def renderLayer: RenderLayer = FullSquare(tileMaterial.color)
  }

  case class Being(gender: Gender, characterBackground: CharacterBackground) extends Entity with Visible {
    def renderLayer: RenderLayer = gender match {
      case Male => MediumSquare(Color.Blue)
      case Female => MediumSquare(Color.Red)
    }
  }

  sealed trait Relationship
  case object StandingOn extends Relationship
  case class PositionedAt(zonePosition: Coordinates) extends Relationship

  type WorldGraph = Graph[Entity, Relationship]
  type RenderMap = mutable.Map[Coordinates, mutable.ArrayBuffer[RenderLayer]]
}
