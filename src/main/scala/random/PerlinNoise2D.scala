package random

import scala.collection.mutable
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

sealed trait PerlinNoise2DStyle {
  def octaves: List[(Double, Double)]
}
case object Cloudy extends PerlinNoise2DStyle {
  def octaves: List[(Double, Double)] = List(
    (10, 0.5),
    (20, 0.25),
    (40, 0.125),
    (80, 0.0625),
  )
}

class PerlinNoise2D(style: PerlinNoise2DStyle, resolution: Int = 1000) extends NoiseGenerator {

  private case class GradientFieldCoordinate(x: Int, y: Int)

  private var gradient = mutable.Map[GradientFieldCoordinate, Vector2D]()

  private def generateGradient(): Unit = {
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

  private def lerp(a: Double, b: Double, frac: Double): Double = a + frac * (b - a)

  private def fade(t: Double): Double =
    t * t * t * (t * (t * 6 - 15) + 10)

  private def getIntensityAt(x: Double, y: Double): Double = {
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

  private def adjustedIntensityAt(x: Double, y: Double, frequency: Double, amplitude: Double): Double = {
    getIntensityAt(
      (x/resolution.toDouble*frequency) % resolution,
      (y/resolution.toDouble*frequency) % resolution
    ) * amplitude
  }

  def valueAt(x: Double, y: Double): Double = {
    var intensity: Double = 0.0
    style.octaves.foreach { case (frequency, amplitude) =>
      intensity = intensity + adjustedIntensityAt(x,y,frequency,amplitude)
    }
    intensity + 0.5
  }

  generateGradient()

}
