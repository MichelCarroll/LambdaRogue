package game.actions

import common._

sealed trait GameAction
case class MoveCharacter(direction: Direction) extends GameAction
