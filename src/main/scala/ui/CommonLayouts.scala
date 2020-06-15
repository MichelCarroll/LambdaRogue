package ui

import common._
import ui.layout._

object CommonLayouts {

  def simpleList(options: List[(String, UIAction)])(implicit gameSettings: GameSettings) =
    new UIStackPanel(options.foldLeft((1, List[UIObject]())) { case ((num, xs), (text, action)) =>
      (num + 1, xs :+ new UITextButton(Text(s"$num. $text", gameSettings.defaultTextColor, gameSettings.defaultFont), action))
    }._2)

}
