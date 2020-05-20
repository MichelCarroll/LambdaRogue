package ui

import game.Gender
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

trait LayoutContext {
  val textSizeCache: TextSizeCache
}

sealed trait UIAction
case class ChooseGender(gender: Gender) extends UIAction

sealed trait UIObject {

  def children: List[UIObject]
  def padding: Edges = Edges.none
  def margin: Edges = Edges.none
  def onClick: Option[UIAction] = None

  var coordinates: Coordinates = Coordinates(0,0)
  var naturalSize: Size

  def relayout(context: LayoutContext): Unit = {
    resize(context)
    children.foreach { child =>
      child.coordinates = Coordinates(
        coordinates.x + padding.left + child.margin.left,
        coordinates.y + padding.top + child.margin.top
      )
      child.relayout(context)
    }
  }

  private[ui] def resize(context: LayoutContext): Unit = {
    children.foreach { child =>
      child.resize(context)
    }
  }

  lazy val outerWidth: Int = innerWidth + margin.left + margin.right
  lazy val outerHeight: Int = innerHeight + margin.top + margin.bottom
  lazy val innerWidth: Int = naturalSize.width + padding.left + padding.right
  lazy val innerHeight: Int = naturalSize.height + padding.top + padding.bottom
}

class UITextButton(
                    val text: Text,
                    val action: UIAction,
                    override val padding: Edges = Edges(5),
                    override val margin: Edges = Edges.none
) extends UIObject {
  override def children: List[UIObject] = List.empty

  override def onClick: Option[UIAction] = Some(action)

  override var naturalSize: Size = Size(0,0)

  override def resize(context: LayoutContext): Unit = {
    super.resize(context)
    naturalSize = context.textSizeCache.get(text)
  }
}

class UIPanel(
               var naturalSize: Size,
               val children: List[UIObject] = List.empty
) extends UIObject

class UIStackPanel(
  val children: List[UIObject],
  override val padding: Edges = Edges(5),
  override val margin: Edges = Edges.none
) extends UIObject {

  var naturalSize = Size(0,0)

  override def resize(context: LayoutContext): Unit = {
    super.resize(context)

    naturalSize = Size(
      width =
        if(children.isEmpty) 0
        else children.map(_.outerWidth).max,

      height = children.map(_.outerHeight).sum
    )
  }

  override def relayout(context: LayoutContext): Unit = {
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