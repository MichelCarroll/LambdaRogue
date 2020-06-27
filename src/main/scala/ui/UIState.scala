package ui

import game._
import game.actions.MoveCharacter
import ui.layout._
import common._
import game.world.World

sealed trait UIState {
  def execute(action: UIAction): UIState
  val rootUIElement: UIObject
}

case class GenderSelection(implicit val gameSettings: GameSettings, world: World) extends UIState {

  override def execute(action: UIAction): UIState = action match {
    case ChooseGender(gender) =>
      world.characterCreation.gender = gender
      BackgroundSelection()
    case _ =>
      this
  }

  val rootUIElement = new UIPanel(gameSettings.canvasSize, List(
    CommonLayouts.simpleList(List(
      "Male" -> ChooseGender(Male),
      "Female" -> ChooseGender(Female)
    ))
  ))

}

case class BackgroundSelection(implicit val gameSettings: GameSettings, world: World) extends UIState {
  override def execute(action: UIAction): UIState = action match {
    case ChoosePlayerBackground(background) =>
      world.characterCreation.characterBackground = background
      world.initialize()
      WorldView()
    case _ =>
      this
  }

  val rootUIElement = new UIPanel(gameSettings.canvasSize, List(
    CommonLayouts.simpleList(List(
      "Soldier" -> ChoosePlayerBackground(Soldier),
      "Nomad" -> ChoosePlayerBackground(Nomad),
      "Merchant" -> ChoosePlayerBackground(Merchant)
    ))
  ))

}

case class WorldView(implicit val gameSettings: GameSettings, world: World) extends UIState {
  override def execute(action: UIAction): UIState = action match {
    case Move(dir) =>
      world.execute(MoveCharacter(dir))
      this
    case InspectGameWorld(zonePosition) =>
      this
    case _ => this
  }
  val rootUIElement = new UIGamePanel(gameSettings.tileSize, gameSettings.canvasSize)
}