package common

import ui.{Font, TextColor, TextSizeCache}


case class GameSettings(
                         textSizeCache: TextSizeCache,
                         defaultFont: Font,
                         defaultTextColor: TextColor,
                         tileSize: Int,
                         zoneSize: Size
                       ) {

  def canvasSize: Size = Size(
    width = tileSize * zoneSize.width,
    height = tileSize * zoneSize.height
  )
}