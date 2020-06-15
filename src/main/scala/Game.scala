import org.scalajs.dom
import org.scalajs.dom.ext.Color
import org.scalajs.dom.html.{Div, Paragraph, Canvas => HtmlCanvas}
import org.scalajs.dom.raw.{KeyboardEvent, MouseEvent}
import ui._
import game._
import common._
import debug.PerformanceTesting

import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}

@JSExportTopLevel("Game")
object Game extends PerformanceTesting {

  @JSExport
  def start(htmlCanvasElement: HtmlCanvas, mainDiv: Div): Unit = {

    implicit val ctx: dom.CanvasRenderingContext2D =
      htmlCanvasElement.getContext("2d")
        .asInstanceOf[dom.CanvasRenderingContext2D]

    implicit val relayoutContext: LayoutContext = new LayoutContext {
      override val textSizeCache: TextSizeCache = new TextSizeCache(ctx)
      override val width: Int = 400
      override val height: Int = 400
      override val defaultFont: Font = Font(12, "Verdana")
      override val defaultTextColor: TextColor = TextColor(
        normal = Color.Red,
        highlighted = Color.Yellow
      )
    }

    implicit val world: World = new World
    val canvas = new Canvas()
    var keysToProcess: Set[Int] = Set()
    val logFps = true

    dom.window.onmousemove = { e: MouseEvent =>
      canvas.mouseMove(Coordinates(e.clientX.toInt, e.clientY.toInt))
    }

    dom.window.onclick = { e: MouseEvent =>
      canvas.click(Coordinates(e.clientX.toInt, e.clientY.toInt))
    }

    dom.window.setInterval(() => {
      measureFps {
        canvas.processKeyTouches(keysToProcess)
        canvas.clear()
        canvas.draw()
      }
    }, 1000 / 60)

    dom.window.onkeydown = { e: KeyboardEvent =>
      keysToProcess += e.keyCode
    }

    dom.window.onkeyup = { e: KeyboardEvent =>
      keysToProcess -= e.keyCode
    }
  }

}

