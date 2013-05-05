package org.liprudent.majiang.engine

import akka.actor.{ActorRef, Actor}
import akka.pattern.ask
import akka.event.Logging
import messages.{JoinRoomRequested, RoomDenied, RoomJoined}
import akka.util.Timeout
import concurrent.duration._
import concurrent.{Await, Future}


//TODO use value class
case class PlayerName(val value: String)

// extends AnyVal

trait PlayerDomain {

  val name: PlayerName

  def roomContext: TableContext

  def joinTable: Future[Either[RoomDenied, RoomJoined]]
}


/**
 *
 * @param joined Wether a room had been joined or not
 */
case class TableContext(joined: Boolean)

class Player(roomWelcomer: ActorRef, nameStr: String) extends Actor with PlayerDomain {

  val log = Logging(context.system, this)


  import context.dispatcher

  // The ExecutionContext that will be used

  implicit val timeout = Timeout(5 seconds)

  var roomContext = TableContext(false)

  override val name = PlayerName(nameStr)

  def joinTable: Future[Either[RoomDenied, RoomJoined]] = {

    def roomJoined {
      println("qsdfsqfsdfqsdfqsdfqsdfqsdfqsdfdsq")
      log.info("I am in room")
      roomContext = TableContext(true)
    }

    val futureJoinResponse = ask(roomWelcomer, JoinRoomRequested(this)).mapTo[Either[RoomDenied, RoomJoined]]

    //TODO make it async
    Await.result(futureJoinResponse, 5 seconds) match {
      case Left(RoomDenied(reason)) => log.info(s"Room denied because $reason")
      case Right(_) => roomJoined
    }

    return futureJoinResponse

  }

  override def preStart = {
    log.info("Player created")
  }

  def receive = {
    case m => log.info(s"received unkown message : $m")
  }


}

