import org.scalajs.dom
import org.scalajs.dom.ext.Color
import org.scalajs.dom.html.Canvas
import org.scalajs.dom.raw.MouseEvent
import ui._

import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}

sealed trait Gender
case object Male extends Gender
case object Female extends Gender

sealed trait UIAction
case class ChooseGender(gender: Gender) extends UIAction

@JSExportTopLevel("Main")
object Main {

  @JSExport
  def start(canvas: Canvas): Unit = {

    val ctx = canvas.getContext("2d")
      .asInstanceOf[dom.CanvasRenderingContext2D]

    val textSizeCache = new TextSizeCache(ctx)

    var uiActions: List[UIAction] = List()

    val width = 400
    val height = 400

    ctx.canvas.width = width
    ctx.canvas.height = height

    val font = Font(12, "Verdana")
    val textStyle = TextColor(
      normal = Color.Red,
      highlighted = Color.Yellow
    )

    var rootUIElement = new UIPanel(Size(width, height), List(
      new UIStackPanel(List(
        UITextButton(Text("1. Male", textStyle, font), textSizeCache),
        UITextButton(Text("2. Female", textStyle, font), textSizeCache)
      ))
    ))
    rootUIElement.relayout()

    val clickMap = new ClickMap()
    clickMap.recompute(rootUIElement)

    def clear(): Unit = {
      ctx.fillStyle = "black"
      ctx.fillRect(0, 0, 400, 400)
    }

    var hoveringClickableElement: Option[UIObject] = None

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

    dom.window.onclick = { e: MouseEvent =>

    }

    dom.window.setInterval(() => {
      clear()
      draw()
    }, 50)
  }

}

