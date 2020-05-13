import org.scalajs.dom
import org.scalajs.dom.ext.Color
import org.scalajs.dom.html.Canvas
import org.scalajs.dom.raw.MouseEvent
import ui._

import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}



@JSExportTopLevel("Main")
object Main {


  @JSExport
  def start(canvas: Canvas): Unit = {

    val ctx = canvas.getContext("2d")
      .asInstanceOf[dom.CanvasRenderingContext2D]

    val textSizeCache = new TextSizeCache(ctx)

    val width = 400
    val height = 400

    ctx.canvas.width = width
    ctx.canvas.height = height

    val font = Font(12, "Verdana")
    val textStyle = TextColor(
      normal = Color.Red,
      highlighted = Color.Yellow
    )

    var rootUIElement = UIPanel(BoundingBox(Coordinates(0, 0), Size(width, height)), Set(
      UITextButton(Text("Hello World", textStyle, font), Coordinates(50, 50), textSizeCache),
      UITextButton(Text("Hello World", textStyle, font), Coordinates(300, 300), textSizeCache)
    ))

    val clickMap = new ClickMap()
    clickMap.recompute(rootUIElement)

    def clear(): Unit = {
      ctx.fillStyle = "black"
      ctx.fillRect(0, 0, 400, 400)
    }

    var hoveringClickableElement: Option[UIObject] = None

    def draw(): Unit = {

      def drawUIObject(obj: UIObject): Unit = {
        val Coordinates(x,y) = obj.boundingBox.coordinates
        val Size(w,h) = obj.boundingBox.size

        obj match {
          case UITextButton(text, _) =>
            ctx.font = text.font.css

            text.color.background.foreach { bgColor =>
              ctx.fillStyle = bgColor.toString()
              ctx.fillRect(x,y,w,h)
            }

            if(hoveringClickableElement.contains(obj)) {
              ctx.fillStyle = text.color.highlighted.toString()
            } else {
              ctx.fillStyle = text.color.normal.toString()
            }

            ctx.fillText(text.text, x, y + h)
          case UIPanel(_,_) =>
            ctx.strokeStyle = "red"
            ctx.strokeRect(x,y,w,h)
        }

        obj.children.foreach(drawUIObject)
      }

      drawUIObject(rootUIElement)
    }


    dom.window.onmousemove = { e: MouseEvent =>
      val coordinates = Coordinates(e.clientX.toInt, e.clientY.toInt)
      hoveringClickableElement = clickMap.testClick(coordinates)

      if(hoveringClickableElement.nonEmpty) {
        ctx.canvas.style.cursor = "pointer"
      } else {
        ctx.canvas.style.cursor = "auto"
      }
    }

    dom.window.setInterval(() => {
      clear()
      draw()
    }, 50)
  }

}

