package ui

import org.scalajs.dom.ext.Color

case class Coordinates(x: Int, y: Int)
case class Size(width: Int, height: Int)
case class Edges(top: Int = 0, right: Int = 0, bottom: Int = 0, left: Int = 0)
object Edges {
  val none = Edges()
  def apply(all: Int): Edges = Edges(all, all, all, all)
}

case class Font(size: Int, family: String) {
  val css = s"${size}pt $family"
}

case class TextColor(normal: Color, highlighted: Color, background: Option[Color] = None)

case class Text(text: String, color: TextColor, font: Font)

sealed trait UIObject {

  def naturalSize: Size
  def children: List[UIObject]
  def padding: Edges = Edges.none
  def margin: Edges = Edges.none
  def clickable: Boolean = false
  var coordinates: Coordinates = Coordinates(0,0)

  def relayout(): Unit = {
    children.foreach { child =>
      child.coordinates = Coordinates(
        coordinates.x + padding.left + child.margin.left,
        coordinates.y + padding.top + child.margin.top
      )
      child.relayout()
    }
  }

  lazy val outerWidth: Int = innerWidth + margin.left + margin.right
  lazy val outerHeight: Int = innerHeight + margin.top + margin.bottom
  lazy val innerWidth: Int = naturalSize.width + padding.left + padding.right
  lazy val innerHeight: Int = naturalSize.height + padding.top + padding.bottom
}

class UITextButton(
                    val text: Text,
                    val naturalSize: Size,
                    override val padding: Edges = Edges(5),
                    override val margin: Edges = Edges.none
) extends UIObject {
  override def clickable: Boolean = true
  override def children: List[UIObject] = List.empty


}

object UITextButton {
  def apply(text: Text, textSizeCache: TextSizeCache): UITextButton =
    new UITextButton(text, textSizeCache.get(text))
}

class UIPanel(
               val naturalSize: Size,
               val children: List[UIObject] = List.empty
) extends UIObject

class UIStackPanel(
  val children: List[UIObject],
  override val padding: Edges = Edges(5),
  override val margin: Edges = Edges.none
) extends UIObject {

  lazy val naturalSize = Size(
    width =
      if(children.isEmpty) 0
      else children.map(_.outerWidth).max,

    height = children.map(_.outerHeight).sum
  )

  override def relayout(): Unit = {
    var accumY = padding.top
    children.foreach { child =>
      child.coordinates = Coordinates(
        coordinates.x + padding.left + child.margin.left,
        accumY + child.margin.top
      )

      accumY = accumY + child.outerHeight
    }
  }
}