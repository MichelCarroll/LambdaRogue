package ui.layout

import ui.{Coordinates, Edges, LayoutContext, Size}


class UIStackPanel(
                    val children: List[UIObject],
                    override val padding: Edges = Edges(5),
                    override val margin: Edges = Edges.none
                  ) extends UIObject {

  var naturalSize = Size(0,0)

  override def resize()(implicit context: LayoutContext): Unit = {
    super.resize()

    naturalSize = Size(
      width =
        if(children.isEmpty) 0
        else children.map(_.outerWidth).max,

      height = children.map(_.outerHeight).sum
    )
  }

  override def relayout()(implicit context: LayoutContext): Unit = {
    var accumY = padding.top
    children.foreach { child =>
      child.coordinates = Coordinates(
        coordinates.x + padding.left + child.margin.left,
        accumY + child.margin.top
      )
      accumY = accumY + child.outerHeight
    }
  }
}