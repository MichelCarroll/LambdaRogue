package ui

import game.World
import org.scalajs.dom.ext.KeyCode
import org.scalajs.dom.raw.CanvasRenderingContext2D
import ui.layout.UIObject
import common._

class Canvas()(
  implicit ctx: CanvasRenderingContext2D,
  gameSettings: GameSettings,
  world: World) {

  var uiState: UIState = GenderSelection()
  val drawBoxes = false
  var hoveringClickableElement: Option[UIObject] = None
  val clickMap = new ClickMap()
  var lastMousePosition: Coordinates = Coordinates(0, 0)

  def processKeyTouches(keyCodes: Set[Int]): Unit = {
    keyCodes.foreach {
      case KeyCode.Up => executeAction(Move(Up))
      case KeyCode.Down => executeAction(Move(Down))
      case KeyCode.Right => executeAction(Move(Right))
      case KeyCode.Left => executeAction(Move(Left))
      case _ =>
    }
  }

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
    ctx.fillRect(0, 0, gameSettings.canvasSize.width, gameSettings.canvasSize.height)
  }

  def draw(): Unit = {
    uiState.rootUIElement.draw(debug = false, hoveringClickableElement)
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
    draw()
  }

  private def executeAction(action: UIAction): Unit = {
    uiState = uiState.execute(action)
    recalculateUI()
  }

  ctx.canvas.width = gameSettings.canvasSize.width
  ctx.canvas.height = gameSettings.canvasSize.height

  recalculateUI()
}
