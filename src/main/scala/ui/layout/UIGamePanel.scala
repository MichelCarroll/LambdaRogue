package ui.layout

import game.{FullSquare, MediumSquare, World}
import org.scalajs.dom.raw.CanvasRenderingContext2D
import common._


class UIGamePanel(var naturalSize: Size) extends UIObject {
  val children: List[UIObject] = List.empty

  override def draw(debug: Boolean, hoveringClickableElement: Option[UIObject])
                   (implicit ctx: CanvasRenderingContext2D, world: World): Unit = {

    world.renderMap.foreach { case (zonePosition, instructions) =>
      val tileSize = 10
      val tileX = zonePosition.x * tileSize
      val tileY = zonePosition.y * tileSize

      if(tileX + tileSize >= 0
          && tileX < naturalSize.width
          && tileY + tileSize >= 0
          && tileY < naturalSize.height)
        {
          instructions.foreach {
            case FullSquare(color) =>
              ctx.fillStyle = color.toString()
              ctx.fillRect(
                x = tileX,
                y = tileY,
                w = tileSize,
                h = tileSize
              )

            case MediumSquare(color) =>
              ctx.fillStyle = color.toString()
              ctx.fillRect(
                x = tileX + tileSize / 4,
                y = tileY + tileSize / 4,
                w = tileSize / 2,
                h = tileSize / 2
              )
          }
        }

    }

    super.draw(debug, hoveringClickableElement)
  }
}
