import org.scalajs.dom
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

    val width = 400
    val height = 400

    ctx.canvas.width = width
    ctx.canvas.height = height

    var rootUIElement = new UIPanel(BoundingBox(Coordinates(0, 0), Size(width, height)))
    rootUIElement.children.add(new UIButton("Hello World", BoundingBox(Coordinates(50, 50), Size(100, 100))))
    rootUIElement.children.add(new UIButton("Hello World", BoundingBox(Coordinates(100, 100), Size(30, 30))))
    rootUIElement.children.add(new UIButton("Hello World", BoundingBox(Coordinates(300, 300), Size(10, 60))))

    val clickMap = new ClickMap()
    clickMap.recompute(rootUIElement)

    def clear(): Unit = {
      ctx.fillStyle = "black"
      ctx.fillRect(0, 0, 400, 400)
    }

    var hoveringClickableElement: Option[UIObject] = None

    def draw(): Unit = {

      def drawUIObject(obj: UIObject): Unit = {
        if(hoveringClickableElement.contains(obj)) {
          ctx.fillStyle = "red"
          ctx.fillRect(
            obj.boundingBox.coordinates.x,
            obj.boundingBox.coordinates.y,
            obj.boundingBox.size.width,
            obj.boundingBox.size.height
          )
        } else {
          ctx.strokeStyle = "red"
          ctx.strokeRect(
            obj.boundingBox.coordinates.x,
            obj.boundingBox.coordinates.y,
            obj.boundingBox.size.width,
            obj.boundingBox.size.height
          )
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

