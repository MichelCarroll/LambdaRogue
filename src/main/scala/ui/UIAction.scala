package ui

import game.{CharacterBackground, Gender}
import common._

sealed trait UIAction
case class ChooseGender(gender: Gender) extends UIAction
case class ChoosePlayerBackground(characterBackground: CharacterBackground) extends UIAction
case class InspectGameWorld(zonePosition: Coordinates) extends UIAction

case class Move(direction: Direction) extends UIAction