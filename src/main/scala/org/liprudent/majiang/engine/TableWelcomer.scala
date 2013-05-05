package org.liprudent.majiang.engine

import akka.actor.{ActorRef, Actor}
import messages.{RoomDenied, RoomJoined, JoinRoomRequested}
import collection.mutable
import akka.event.Logging
import collection.immutable.ListSet
import akka.pattern.pipe
import concurrent.Future

trait TableWelcomerDomain {
    var players:ListSet[PlayerDomain] = ListSet.empty

    def addPlayer(player:PlayerDomain):Either[RoomDenied, RoomJoined] = {
      if (players.contains(player)) {
        Left(RoomDenied("already in"))
      } else if (players.size >= 4) {
        Left(RoomDenied("packed!"))
      } else {
        players = players + player
        Right(RoomJoined())
      }
    }
}

/**
 * a TableWelcomer is an actor that can welcome up to 4 players.
 * it can also welcolme spectators.
 * when the room is packed, it will deny access to players.
 */
class TableWelcomer extends Actor with TableWelcomerDomain{

  val log = Logging(context.system,this)

  import context.dispatcher // The ExecutionContext that will be used

  def receive = {
    case JoinRoomRequested(playerDomain) => addPlayer(sender, playerDomain )
  }

  private def addPlayer(sender: ActorRef, playerDomain:PlayerDomain) {
    Future(addPlayer(playerDomain)) pipeTo sender
  }

}
