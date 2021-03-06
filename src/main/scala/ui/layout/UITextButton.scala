package ui.layout

import org.scalajs.dom.raw.CanvasRenderingContext2D
import ui.{Edges, Text, UIAction}
import common._
import game.world.World

class UITextButton(
                    val text: Text,
                    val action: UIAction,
                    override val padding: Edges = Edges(5),
                    override val margin: Edges = Edges.none
                  ) extends UIObject {
  override def children: List[UIObject] = List.empty

  override def onClick(coordinates: Coordinates): Option[UIAction] = Some(action)

  override var naturalSize: Size = Size(0,0)

  override def resize()(implicit context: GameSettings): Unit = {
    super.resize()
    naturalSize = context.textSizeCache.get(text)
  }

  override def draw(debug: Boolean, hoveringClickableElement: Option[UIObject])
                   (implicit ctx: CanvasRenderingContext2D, world: World): Unit = {
    val Coordinates(x,y) = coordinates
    val Size(w,h) = Size(innerWidth, innerHeight)

    ctx.font = text.font.css

    text.color.background.foreach { bgColor =>
      ctx.fillStyle = bgColor.toString()
      ctx.fillRect(x,y,w,h)
    }

    if(hoveringClickableElement.contains(this)) {
      ctx.fillStyle = text.color.highlighted.toString()
    } else {
      ctx.fillStyle = text.color.normal.toString()
    }

    ctx.fillText(text.text, x + padding.left, y + padding.top + naturalSize.height)

    super.draw(debug, hoveringClickableElement)
  }
}
