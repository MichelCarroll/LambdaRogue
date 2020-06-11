package ui

import game.World
import org.scalajs.dom.raw.CanvasRenderingContext2D
import ui.layout.{UIGamePanel, UIObject, UITextButton}
import game._

class Canvas()(
  implicit ctx: CanvasRenderingContext2D,
  layoutContext: LayoutContext,
  world: World) {

  var uiState: UIState = GenderSelection()
  val drawBoxes = false
  var hoveringClickableElement: Option[UIObject] = None
  val clickMap = new ClickMap()
  var lastMousePosition: Coordinates = Coordinates(0, 0)


  def click(coordinates: Coordinates): Unit = {
    for {
      elem <- hoveringClickableElement
      action <- elem.onClick
    } {
      executeAction(action)
    }
  }

  def mouseMove(coordinates: Coordinates): Unit = {
    lastMousePosition = coordinates
    recalculateMouseState()
  }

  def clear(): Unit = {
    ctx.fillStyle = "black"
    ctx.fillRect(0, 0, 400, 400)
  }

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

        case elem: UIGamePanel =>

          world.renderMap.foreach { case (zonePosition, instructions) =>
            val tileSize = 40
            val tileX = zonePosition.x * tileSize
            val tileY = zonePosition.y * tileSize

            instructions.foreach {
              case FullSquare(color) =>
                ctx.fillStyle = color
                ctx.fillRect(
                  x = tileX,
                  y = tileY,
                  w = tileSize,
                  h = tileSize
                )

              case MediumSquare(color) =>
                ctx.fillStyle = color
                ctx.fillRect(
                  x = tileX + tileSize / 4,
                  y = tileY + tileSize / 4,
                  w = tileSize / 2,
                  h = tileSize / 2
                )
            }
          }

        case _ =>

      }


      obj.children.foreach(drawUIObject)
    }

    drawUIObject(uiState.rootUIElement)
  }


  private def recalculateMouseState(): Unit = {
    hoveringClickableElement = clickMap.testClick(lastMousePosition)
    if(hoveringClickableElement.nonEmpty) {
      ctx.canvas.style.cursor = "pointer"
    } else {
      ctx.canvas.style.cursor = "auto"
    }
  }

  private def recalculateUI(): Unit = {
    uiState.rootUIElement.relayout()
    clickMap.recompute(uiState.rootUIElement)
    recalculateMouseState()
  }

  private def executeAction(action: UIAction): Unit = {
    uiState = uiState.execute(action)
    recalculateUI()
  }

  ctx.canvas.width = 400
  ctx.canvas.height = 400

  recalculateUI()
}
