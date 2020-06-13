import org.scalajs.dom.ext.Color
import org.scalajs.dom.raw.CanvasRenderingContext2D
import common._

package object ui {

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
    val width: Int
    val height: Int
    val defaultFont: Font
    val defaultTextColor: TextColor
  }

  trait Drawable {
    def draw(area: Area)(implicit ctx: CanvasRenderingContext2D): Unit
  }
}
