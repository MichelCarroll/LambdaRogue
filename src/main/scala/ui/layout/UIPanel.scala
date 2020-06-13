package ui.layout

import common._

class UIPanel(
               var naturalSize: Size,
               val children: List[UIObject] = List.empty
             ) extends UIObject
