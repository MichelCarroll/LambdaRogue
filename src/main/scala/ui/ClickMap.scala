package ui

import ui.layout.UIObject
import scala.collection.mutable
import common._

class ClickMap(resolution: Int = 5) {

  val elements = mutable.Map[Cell, UIObject]()

  case class Cell(x: Int, y: Int)

  private def objectCells(obj: UIObject): Seq[Cell] = {
    val minX = obj.coordinates.x / resolution
    val maxX = ((obj.coordinates.x + obj.innerWidth) / resolution.toFloat - 1).ceil.toInt
    val minY = obj.coordinates.y / resolution
    val maxY = ((obj.coordinates.y + obj.innerHeight) / resolution.toFloat - 1).ceil.toInt

    for {
      cellX <- minX to maxX
      cellY <- minY to maxY
    } yield Cell(cellX, cellY)
  }

  def recompute(rootObject: UIObject): Unit = {
    elements.clear()

    def recurAddObjects(obj: UIObject): Unit = {
      val cells = objectCells(obj)
      if(obj.onClick.nonEmpty)
        cells.foreach { cell => elements += cell -> obj }
      obj.children.foreach(recurAddObjects)
    }

    recurAddObjects(rootObject)
  }

  def testClick(coordinates: Coordinates): Option[UIObject] = {
    val cell = Cell(coordinates.x / resolution, coordinates.y / resolution)
    elements.get(cell)
  }

}
