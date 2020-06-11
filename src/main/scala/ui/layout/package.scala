package ui

import game.World
import org.scalajs.dom.raw.CanvasRenderingContext2D

package object layout {


  trait UIObject {

    def children: List[UIObject]
    def padding: Edges = Edges.none
    def margin: Edges = Edges.none
    def onClick: Option[UIAction] = None

    var coordinates: Coordinates = Coordinates(0,0)
    var naturalSize: Size

    def relayout()(implicit context: LayoutContext): Unit = {
      resize()
      children.foreach { child =>
        child.coordinates = Coordinates(
          coordinates.x + padding.left + child.margin.left,
          coordinates.y + padding.top + child.margin.top
        )
        child.relayout()
      }
    }

    private[ui] def resize()(implicit context: LayoutContext): Unit = {
      children.foreach { child =>
        child.resize()
      }
    }

    def draw(debug: Boolean, hoveringClickableElement: Option[UIObject], world: World)
            (implicit ctx: CanvasRenderingContext2D): Unit = {

      if(debug) {
        val Coordinates(x,y) = coordinates
        val Size(w,h) = Size(innerWidth, innerHeight)
        ctx.strokeStyle = "red"
        ctx.strokeRect(x,y,w,h)
      }

      children.foreach(_.draw(debug, hoveringClickableElement, world))
    }

    lazy val outerWidth: Int = innerWidth + margin.left + margin.right
    lazy val outerHeight: Int = innerHeight + margin.top + margin.bottom
    lazy val innerWidth: Int = naturalSize.width + padding.left + padding.right
    lazy val innerHeight: Int = naturalSize.height + padding.top + padding.bottom
  }

}
