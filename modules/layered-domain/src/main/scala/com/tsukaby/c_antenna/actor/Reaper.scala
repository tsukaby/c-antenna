package com.tsukaby.c_antenna.actor

import akka.actor.{ActorRef, Terminated}
import scala.collection.mutable.ArrayBuffer

object Reaper {
  // Used by others to register an Actor for watching
  case class WatchMe(ref: ActorRef)
}

abstract class Reaper extends BaseActor {
  import Reaper._

  // Keep track of what we're watching
  val watched = ArrayBuffer.empty[ActorRef]

  // Derivations need to implement this method. It's the
  // hook that's called when everything's dead
  def allSoulsReaped(): Unit

  // Watch and check for termination
  final def receive = {
    case WatchMe(ref) =>
      context.watch(ref)
      watched += ref
    case Terminated(ref) =>
      watched -= ref
      if (watched.isEmpty) allSoulsReaped()
  }
}

class ShutdownReaper extends Reaper {
  // Shutdown
  def allSoulsReaped(): Unit = {
    log.info("actor systemを終了します。")
    context.system.shutdown()
  }
}