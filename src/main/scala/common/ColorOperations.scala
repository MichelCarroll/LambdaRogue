package common

import org.scalajs.dom.ext.Color

trait ColorOperations {

  def lerp(c1: Color, c2: Color, frac: Double): Color = {
    Color(
      (c1.r + frac * (c2.r - c1.r)).toInt,
      (c1.g + frac * (c2.g - c1.g)).toInt,
      (c1.b + frac * (c2.b - c1.b)).toInt
    )
  }

}
