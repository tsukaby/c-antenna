import play.PlayRunHook
import sbt._

import java.net.InetSocketAddress


object Grunt {
  def apply(base: File): PlayRunHook = {

    object GruntProcess extends PlayRunHook {

      var process: Option[Process] = None

      override def beforeStarted(): Unit = {
        Process("grunt", base).run
      }

      override def afterStarted(addr: InetSocketAddress): Unit = {
        process = Some(Process("grunt serve-play", base).run)
      }

      override def afterStopped(): Unit = {
        process.map(p => p.destroy())
        process = None
      }
    }

    GruntProcess
  }
}