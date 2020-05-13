package ui

import scala.collection.mutable

case class Coordinates(x: Int, y: Int)
case class Size(width: Int, height: Int)

case class BoundingBox(coordinates: Coordinates, size: Size)

sealed trait UIObject {
  def boundingBox: BoundingBox
  def children: mutable.Set[UIObject]
  def clickable: Boolean = false
}

class UIButton(
  var text: String,
  var boundingBox: BoundingBox,
  var children: mutable.Set[UIObject] = mutable.Set.empty
) extends UIObject {
  override def clickable: Boolean = true
}

class UIPanel(
  var boundingBox: BoundingBox,
  var children: mutable.Set[UIObject] = mutable.Set.empty
) extends UIObject