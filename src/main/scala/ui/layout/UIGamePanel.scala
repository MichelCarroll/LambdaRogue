package ui.layout

import game.{FullSquare, MediumSquare}
import org.scalajs.dom.raw.CanvasRenderingContext2D
import common._
import game.world.World
import ui.{InspectGameWorld, UIAction}


class UIGamePanel(tileSize: Int, var naturalSize: Size)(implicit val world: World) extends UIObject {
  val children: List[UIObject] = List.empty

  override def onClick(coordinates: Coordinates): Option[UIAction] = {
    zonePosition(coordinates).map(InspectGameWorld)
  }

  private def zonePosition(coordinates: Coordinates): Option[Coordinates] = {
    val tileX = coordinates.x / tileSize
    val tileY = coordinates.y / tileSize
    Some(Coordinates(tileX, tileY))
  }

  override def draw(debug: Boolean, hoveringClickableElement: Option[UIObject])
                   (implicit ctx: CanvasRenderingContext2D, world: World): Unit = {

    world.renderMap.foreach { case (zonePosition, instructions) =>
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
