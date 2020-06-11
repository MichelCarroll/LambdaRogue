package ui.layout

import ui.{Edges, LayoutContext, Size, Text, UIAction}


class UITextButton(
                    val text: Text,
                    val action: UIAction,
                    override val padding: Edges = Edges(5),
                    override val margin: Edges = Edges.none
                  ) extends UIObject {
  override def children: List[UIObject] = List.empty

  override def onClick: Option[UIAction] = Some(action)

  override var naturalSize: Size = Size(0,0)

  override def resize()(implicit context: LayoutContext): Unit = {
    super.resize()
    naturalSize = context.textSizeCache.get(text)
  }
}
