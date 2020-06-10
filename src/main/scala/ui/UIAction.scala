package ui

import game.{CharacterBackground, Gender}

sealed trait UIAction
case class ChooseGender(gender: Gender) extends UIAction
case class ChoosePlayerBackground(characterBackground: CharacterBackground) extends UIAction