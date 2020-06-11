package ui

import game._
import ui.layout._

sealed trait UIState {
  def execute(action: UIAction)(implicit world: World): UIState
  val rootUIElement: UIObject
}

case class GenderSelection(implicit val layoutContext: LayoutContext) extends UIState {

  override def execute(action: UIAction)(implicit world: World): UIState = action match {
    case ChooseGender(gender) =>
      world.characterCreation.gender = gender
      BackgroundSelection()
    case _ =>
      this
  }

  val rootUIElement = new UIPanel(Size(layoutContext.width, layoutContext.height), List(
    CommonLayouts.simpleList(List(
      "Male" -> ChooseGender(Male),
      "Female" -> ChooseGender(Female)
    ))
  ))

}

case class BackgroundSelection(implicit val layoutContext: LayoutContext) extends UIState {
  override def execute(action: UIAction)(implicit world: World): UIState = action match {
    case ChoosePlayerBackground(background) =>
      world.characterCreation.characterBackground = background
      world.initialize()
      WorldView()
    case _ =>
      this
  }

  val rootUIElement = new UIPanel(Size(layoutContext.width, layoutContext.height), List(
    CommonLayouts.simpleList(List(
      "Soldier" -> ChoosePlayerBackground(Soldier),
      "Nomad" -> ChoosePlayerBackground(Nomad),
      "Merchant" -> ChoosePlayerBackground(Merchant)
    ))
  ))

}

case class WorldView(implicit val layoutContext: LayoutContext) extends UIState {
  override def execute(action: UIAction)(implicit world: World): UIState = action match {
    case _ => this
  }
  val rootUIElement = new UIGamePanel(Size(layoutContext.width, layoutContext.height))
}