package ui.layout

import ui.Size


class UIPanel(
               var naturalSize: Size,
               val children: List[UIObject] = List.empty
             ) extends UIObject
