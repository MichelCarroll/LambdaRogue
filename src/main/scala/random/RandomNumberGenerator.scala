package random

trait RNG {
  def nextInt: (Int, RNG)
}

case class RandomNumberGenerator(seed: Long) extends RNG {
  def nextInt: (Int, RNG) = {
    val newSeed = (seed * 0x5DEECE66DL + 0xBL) & 0xFFFFFFFFFFFFL
    val nextRNG = RandomNumberGenerator(newSeed)
    val n = (newSeed >>> 16).toInt
    (n, nextRNG)
  }
}