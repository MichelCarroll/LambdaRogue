import org.scalajs.dom
import org.scalajs.dom.ext.Color
import org.scalajs.dom.html.Canvas
import org.scalajs.dom.raw.MouseEvent
import ui._
import game._

import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}

@JSExportTopLevel("Main")
object Main {

  @JSExport
  def start(canvas: Canvas): Unit = {

    val ctx = canvas.getContext("2d")
      .asInstanceOf[dom.CanvasRenderingContext2D]

    ctx.canvas.width = 400
    ctx.canvas.height = 400

    implicit val relayoutContext = new LayoutContext {
      override val textSizeCache: TextSizeCache = new TextSizeCache(ctx)
      override val width: Int = ctx.canvas.width
      override val height: Int = ctx.canvas.height
      override val defaultFont: Font = Font(12, "Verdana")
      override val defaultTextColor: TextColor = TextColor(
        normal = Color.Red,
        highlighted = Color.Yellow
      )
    }

    implicit var world = new World

    var uiState: UIState = GenderSelection()
    val clickMap = new ClickMap()

    var lastMousePosition: Coordinates = Coordinates(0, 0)

    var hoveringClickableElement: Option[UIObject] = None

    def recalculateMouseState(): Unit = {
      hoveringClickableElement = clickMap.testClick(lastMousePosition)
      if(hoveringClickableElement.nonEmpty) {
        ctx.canvas.style.cursor = "pointer"
      } else {
        ctx.canvas.style.cursor = "auto"
      }
    }

    def recalculateUI(): Unit = {
      uiState.rootUIElement.relayout()
      clickMap.recompute(uiState.rootUIElement)
      recalculateMouseState()
    }

    def executeAction(action: UIAction): Unit = {
      uiState = uiState.execute(action)
      recalculateUI()
    }

    recalculateUI()

    def clear(): Unit = {
      ctx.fillStyle = "black"
      ctx.fillRect(0, 0, 400, 400)
    }


    val drawBoxes = false

    def draw(): Unit = {

      def drawUIObject(obj: UIObject): Unit = {
        val Coordinates(x,y) = obj.coordinates
        val Size(w,h) = Size(obj.innerWidth, obj.innerHeight)

        if(drawBoxes) {
          ctx.strokeStyle = "red"
          ctx.strokeRect(x,y,w,h)
        }

        obj match {
          case elem: UITextButton =>
            val text = elem.text
            ctx.font = text.font.css

            text.color.background.foreach { bgColor =>
              ctx.fillStyle = bgColor.toString()
              ctx.fillRect(x,y,w,h)
            }

            if(hoveringClickableElement.contains(elem)) {
              ctx.fillStyle = text.color.highlighted.toString()
            } else {
              ctx.fillStyle = text.color.normal.toString()
            }

            ctx.fillText(text.text, x + obj.padding.left, y + obj.padding.top + obj.naturalSize.height)
          case _ =>
        }


        obj.children.foreach(drawUIObject)
      }

      drawUIObject(uiState.rootUIElement)
    }


    dom.window.onmousemove = { e: MouseEvent =>
      lastMousePosition = Coordinates(e.clientX.toInt, e.clientY.toInt)
      recalculateMouseState()
    }

    dom.window.onclick = { e: MouseEvent =>
      for {
        elem <- hoveringClickableElement
        action <- elem.onClick
      } {
        executeAction(action)
      }
    }

    dom.window.setInterval(() => {
      clear()
      draw()
    }, 50)
  }

}

