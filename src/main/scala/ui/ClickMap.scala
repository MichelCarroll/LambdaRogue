package ui

import ui.layout.UIObject
import scala.collection.mutable
import common._

class ClickMap(resolution: Int = 5) {

  val elements = mutable.Map[Coordinates, UIObject]()

  private def objectCells(obj: UIObject): Seq[Coordinates] = {
    val minX = obj.coordinates.x / resolution
    val maxX = ((obj.coordinates.x + obj.innerWidth) / resolution.toFloat - 1).ceil.toInt
    val minY = obj.coordinates.y / resolution
    val maxY = ((obj.coordinates.y + obj.innerHeight) / resolution.toFloat - 1).ceil.toInt

    for {
      x <- minX to maxX
      y <- minY to maxY
    } yield Coordinates(x, y)
  }

  def recompute(rootObject: UIObject): Unit = {
    elements.clear()

    def recurAddObjects(obj: UIObject): Unit = {
      val coordinates = objectCells(obj)
      coordinates.foreach { coord =>
        obj.onClick(coord).foreach { action =>
          elements += coord -> obj
        }
      }
      obj.children.foreach(recurAddObjects)
    }

    recurAddObjects(rootObject)
  }

  def testClick(coordinates: Coordinates): Option[UIObject] = {
    val coord = Coordinates(coordinates.x / resolution, coordinates.y / resolution)
    elements.get(coord)
  }

}
