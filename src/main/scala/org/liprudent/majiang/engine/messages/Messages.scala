package org.liprudent.majiang.engine.messages

import org.liprudent.majiang.engine.PlayerDomain

case class JoinRoomRequested(player:PlayerDomain)
case class RoomJoined()
case class RoomDenied(reason:String)
