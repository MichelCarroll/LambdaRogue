package ui

import org.scalajs.dom.ext.Color

case class Coordinates(x: Int, y: Int)
case class Size(width: Int, height: Int)

case class Font(size: Int, family: String) {
  val css = s"${size}pt $family"
}

case class TextColor(normal: Color, highlighted: Color, background: Option[Color] = None)

case class Text(text: String, color: TextColor, font: Font)

case class BoundingBox(coordinates: Coordinates, size: Size)

sealed trait UIObject {
  def boundingBox: BoundingBox
  def children: Set[UIObject]
  def clickable: Boolean = false
}

case class UITextButton(
  text: Text,
  boundingBox: BoundingBox
) extends UIObject {
  override def clickable: Boolean = true
  override def children: Set[UIObject] = Set.empty
}

object UITextButton {
  def apply(text: Text, coordinates: Coordinates, textSizeCache: TextSizeCache): UITextButton =
    new UITextButton(text, BoundingBox(coordinates, textSizeCache.get(text)))
}

case class UIPanel(
  boundingBox: BoundingBox,
  children: Set[UIObject] = Set.empty
) extends UIObject