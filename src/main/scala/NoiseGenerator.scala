import org.scalajs.dom
import org.scalajs.dom.ext.Color
import org.scalajs.dom.html.{Canvas => HtmlCanvas}

import scala.collection.mutable
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}
import scala.util.Random

case class Vector2D(x: Double, y: Double) {
  def -(other: Vector2D): Vector2D = Vector2D(
    x - other.x,
    y - other.y,
  )
}

object Vector2D {

  def vectorFromAngle(angle: Double, mag: Double): Vector2D = Vector2D(
    Math.cos(angle) * mag,
    Math.sin(angle) * mag
  )

  def dotProduct(a: Vector2D, b: Vector2D): Double = {
    a.x * b.x + a.y * b.y
  }
}

@JSExportTopLevel("NoiseGenerator")
object NoiseGenerator {

  @JSExport
  def start(htmlCanvasElement: HtmlCanvas): Unit = {

    implicit val ctx: dom.CanvasRenderingContext2D =
      htmlCanvasElement.getContext("2d")
        .asInstanceOf[dom.CanvasRenderingContext2D]

    val resolution = 1000

    case class GradientFieldCoordinate(x: Int, y: Int)

    var gradient = mutable.Map[GradientFieldCoordinate, Vector2D]()

    def generateGradient(): Unit = {
      for {
        x <- 0 to resolution + 1
        y <- 0 to resolution + 1
      } {
        gradient.update(
          GradientFieldCoordinate(x,y),
          Vector2D.vectorFromAngle(Random.nextDouble() * Math.PI * 2, 1)
        )
      }
    }

    def lerp(a: Double, b: Double, frac: Double): Double = a + frac * (b - a)

    def fade(t: Double): Double =
      t * t * t * (t * (t * 6 - 15) + 10)

    def getIntensityAt(x: Double, y: Double): Double = {
      val vector = Vector2D(x, y)
      val cellX = x.toInt
      val cellY = y.toInt
      val tl = gradient(GradientFieldCoordinate(cellX, cellY))
      val tr = gradient(GradientFieldCoordinate(cellX + 1, cellY))
      val bl = gradient(GradientFieldCoordinate(cellX, cellY + 1))
      val br = gradient(GradientFieldCoordinate(cellX + 1, cellY + 1))
      val vtl = vector - Vector2D(cellX, cellY)
      val vtr = vector - Vector2D(cellX + 1, cellY)
      val vbl = vector - Vector2D(cellX, cellY + 1)
      val vbr = vector - Vector2D(cellX + 1, cellY + 1)
      val intTl = Vector2D.dotProduct(tl, vtl)
      val intTr = Vector2D.dotProduct(tr, vtr)
      val intBl = Vector2D.dotProduct(bl, vbl)
      val intBr = Vector2D.dotProduct(br, vbr)
      val fracX = fade(x - x.toInt)
      val fracY = fade(y - y.toInt)
      val T = lerp(intTl, intTr, fracX)
      val B = lerp(intBl, intBr, fracX)
      val M = lerp(T, B, fracY)
      M / 2
    }

    def adjustedIntensityAt(x: Double, y: Double, frequency: Double, amplitude: Double): Double = {
      getIntensityAt(
        (x/resolution.toDouble*frequency) % resolution,
        (y/resolution.toDouble*frequency) % resolution
      ) * amplitude
    }

    val octaves = List[(Double, Double)](
      (10, 0.5),
      (20, 0.25),
      (40, 0.125),
      (80, 0.0625),
    )

    def octaved(x: Double, y: Double): Double = {
      var intensity: Double = 0.0
      octaves.foreach { case (frequency, amplitude) =>
          intensity = intensity + adjustedIntensityAt(x,y,frequency,amplitude)
      }
      intensity + 0.5
    }

    val canvasSize = 1000

    ctx.canvas.width = canvasSize
    ctx.canvas.height = canvasSize

    def draw(): Unit = {
      for {
        x <- 0 to canvasSize
        y <- 0 to canvasSize
      } {
        val a = (octaved(x,y)  * 255).toInt
        ctx.fillStyle = Color(a,a,a).toString()
        ctx.fillRect(x,y,1,1)
      }
    }

    generateGradient()

    draw()
  }

}

