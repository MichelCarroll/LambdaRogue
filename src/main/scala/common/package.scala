
package object common {

  sealed trait Direction
  case object Up extends Direction
  case object Down extends Direction
  case object Right extends Direction
  case object Left extends Direction

  case class Coordinates(x: Int, y: Int) {
    def displaced(direction: Direction): Coordinates =
      direction match {
        case Up => copy(y = y - 1)
        case Down => copy(y = y + 1)
        case Right => copy(x = x + 1)
        case Left => copy(x = x - 1)
      }
  }

  case class Size(width: Int, height: Int)

  case class Area(position: Coordinates, size: Size) {
    def points: Seq[Coordinates] =
      for {
        x <- position.x until position.x + size.width
        y <- position.y until position.y + size.height
      } yield Coordinates(x,y)
  }
}
