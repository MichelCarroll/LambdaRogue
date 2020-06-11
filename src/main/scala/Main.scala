import org.scalajs.dom
import org.scalajs.dom.ext.Color
import org.scalajs.dom.html.{Canvas => HtmlCanvas}
import org.scalajs.dom.raw.MouseEvent
import ui._
import game._

import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}

@JSExportTopLevel("Main")
object Main {

  @JSExport
  def start(htmlCanvasElement: HtmlCanvas): Unit = {

    implicit val ctx: dom.CanvasRenderingContext2D =
      htmlCanvasElement.getContext("2d")
        .asInstanceOf[dom.CanvasRenderingContext2D]

    implicit val relayoutContext: LayoutContext = new LayoutContext {
      override val textSizeCache: TextSizeCache = new TextSizeCache(ctx)
      override val width: Int = ctx.canvas.width
      override val height: Int = ctx.canvas.height
      override val defaultFont: Font = Font(12, "Verdana")
      override val defaultTextColor: TextColor = TextColor(
        normal = Color.Red,
        highlighted = Color.Yellow
      )
    }

    implicit val world: World = new World

    val canvas = new Canvas()

    dom.window.onmousemove = { e: MouseEvent =>
      canvas.mouseMove(Coordinates(e.clientX.toInt, e.clientY.toInt))
    }

    dom.window.onclick = { e: MouseEvent =>
      canvas.click(Coordinates(e.clientX.toInt, e.clientY.toInt))
    }

    dom.window.setInterval(() => {
      canvas.clear()
      canvas.draw()
    }, 50)
  }

}

