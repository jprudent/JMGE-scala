package org.liprudent.majiang.engine

import messages.{RoomJoined, RoomDenied}
import org.scalatest.{BeforeAndAfterAll, FlatSpec}
import org.scalatest.matchers.ShouldMatchers
import akka.actor.{ActorRef, Props, ActorSystem}
import akka.testkit.{ImplicitSender, TestKit, TestActorRef}
import concurrent.{Await, Future}
import concurrent.duration._

class MajiangEngineSpec(_system: ActorSystem)
  extends TestKit(_system)
  with ImplicitSender
  with ShouldMatchers
  with FlatSpec
  with BeforeAndAfterAll {

  def this() = this(ActorSystem("MajiangEngine"))

  override def afterAll: Unit = system.shutdown()

  "A player" should "be able to join a table" in {

    val tableWelcomerActor = TestActorRef(Props[TableWelcomer])
    val tableWelcomer = tableWelcomerActor.underlyingActor.asInstanceOf[TableWelcomerDomain]

    val playerActor = TestActorRef(Props(classOf[Player],
      tableWelcomerActor, "player 1"))
    val player = playerActor.underlyingActor.asInstanceOf[PlayerDomain]

    player.joinTable

    player.roomContext.joined should be (true)

    tableWelcomer.players.contains(player) should be(true)
  }

  it should "not be able to join a table twice" in {
    val tableWelcomerActor = TestActorRef(Props[TableWelcomer])
    val tableWelcomer = tableWelcomerActor.underlyingActor.asInstanceOf[TableWelcomerDomain]

    val playerActor = TestActorRef(Props(classOf[Player],
      tableWelcomerActor, "player 1"))
    val player = playerActor.underlyingActor.asInstanceOf[PlayerDomain]

    player.joinTable
    player.joinTable

    player.roomContext.joined should be (true)

    tableWelcomer.players.count(_ == player) should be(1)

  }


}
