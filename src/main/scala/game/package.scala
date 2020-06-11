
package object game {

  case class ZonePosition(x: Int, y: Int)

  sealed trait RenderLayer
  case class MediumSquare(color: String) extends RenderLayer
  case class FullSquare(color: String) extends RenderLayer

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

  case object GrassTile extends Entity with Visible {
    def renderLayer = FullSquare("green")
  }

  case class Being(gender: Gender, characterBackground: CharacterBackground) extends Entity with Visible {
    def renderLayer = MediumSquare("red")
  }

  sealed trait Relationship
  case object On extends Relationship
  case class PositionedAt(zonePosition: ZonePosition) extends Relationship

  type WorldGraph = Graph[Entity, Relationship]
}
