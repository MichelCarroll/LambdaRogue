package ui

import org.scalajs.dom.CanvasRenderingContext2D
import common._
import scala.collection.mutable


class TextSizeCache(ctx: CanvasRenderingContext2D) {

  implicit class CanvasRenderingContext2DPimp(ctx: CanvasRenderingContext2D) {
    def textBoundingBox(text: Text): Size = {
      ctx.font = text.font.css
      Size(ctx.measureText(text.text).width.toInt, text.font.size)
    }
  }

  private val cache: mutable.Map[Text, Size] = mutable.Map.empty

  private def compute(text: Text): Size = {
    val size = ctx.textBoundingBox(text)
    cache += text -> size
    size
  }

  def get(text: Text): Size = cache.getOrElse(text, compute(text))

}