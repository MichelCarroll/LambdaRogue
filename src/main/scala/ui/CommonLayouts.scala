package ui

import ui.layout._

object CommonLayouts {

  def simpleList(options: List[(String, UIAction)])(implicit layoutContext: LayoutContext) =
    new UIStackPanel(options.foldLeft((1, List[UIObject]())) { case ((num, xs), (text, action)) =>
      (num + 1, xs :+ new UITextButton(Text(s"$num. $text", layoutContext.defaultTextColor, layoutContext.defaultFont), action))
    }._2)

}
