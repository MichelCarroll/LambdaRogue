package ui

import game.World
import org.scalajs.dom.raw.CanvasRenderingContext2D
import ui.layout.UIObject

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
    uiState.rootUIElement.draw(false, hoveringClickableElement, world)
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
