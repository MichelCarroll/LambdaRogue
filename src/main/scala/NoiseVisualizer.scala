import org.scalajs.dom
import org.scalajs.dom.ext.Color
import org.scalajs.dom.html.{Canvas => HtmlCanvas}
import random.{Cloudy, PerlinNoise2D}
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}


@JSExportTopLevel("NoiseVisualizer")
object NoiseVisualizer {

  @JSExport
  def start(htmlCanvasElement: HtmlCanvas): Unit = {

    implicit val ctx: dom.CanvasRenderingContext2D =
      htmlCanvasElement.getContext("2d")
        .asInstanceOf[dom.CanvasRenderingContext2D]

    val noise = new PerlinNoise2D(Cloudy, resolution = 500)

    val canvasSize = 1000

    ctx.canvas.width = canvasSize
    ctx.canvas.height = canvasSize

    def draw(): Unit = {
      for {
        x <- 0 to canvasSize
        y <- 0 to canvasSize
      } {
        val a = (noise.valueAt(x,y)  * 255).toInt
        ctx.fillStyle = Color(a,a,a).toString()
        ctx.fillRect(x,y,1,1)
      }
    }

    draw()
  }

}

