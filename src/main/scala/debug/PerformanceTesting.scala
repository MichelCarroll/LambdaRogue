package debug

import scala.scalajs.js.Date

trait PerformanceTesting {

  var lastFrameTime: Double = 0

  def measure[E](tag: String)(f: => E): E = {
    val before = Date.now()
    val result = f
    val after = Date.now()
    val delta = after - before
    println(s"$tag - $delta ms")
    result
  }

  def measureFps(f: => Unit): Unit = {
    val before = lastFrameTime
    val now = Date.now()
    lastFrameTime = now
    val delta = now - before
    if(delta != 0) {
      println(s"FPS: ${1 / delta * 1000}")
    }
    f
  }

}
