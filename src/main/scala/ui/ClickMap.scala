package ui

import scala.collection.mutable

class ClickMap(resolution: Int = 5) {

  val elements = mutable.Map[Cell, UIObject]()

  case class Cell(x: Int, y: Int)

  private def objectCells(obj: UIObject): Seq[Cell] = {
    val minX = obj.boundingBox.coordinates.x / resolution
    val maxX = ((obj.boundingBox.coordinates.x + obj.boundingBox.size.width) / resolution.toFloat - 1).ceil.toInt
    val minY = obj.boundingBox.coordinates.y / resolution
    val maxY = ((obj.boundingBox.coordinates.y + obj.boundingBox.size.height) / resolution.toFloat - 1).ceil.toInt

    for {
      cellX <- minX to maxX
      cellY <- minY to maxY
    } yield Cell(cellX, cellY)
  }

  def recompute(rootObject: UIObject): Unit = {
    elements.clear()

    def recurAddObjects(obj: UIObject): Unit = {
      val cells = objectCells(obj)
      if(obj.clickable)
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
