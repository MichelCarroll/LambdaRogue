package ui

import game._

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
    new UIStackPanel(List(
      new UITextButton(Text("1. Male", layoutContext.defaultTextColor, layoutContext.defaultFont), ChooseGender(Male)),
      new UITextButton(Text("2. Female", layoutContext.defaultTextColor, layoutContext.defaultFont), ChooseGender(Female))
    ))
  ))

}

case class BackgroundSelection(implicit val layoutContext: LayoutContext) extends UIState {
  override def execute(action: UIAction)(implicit world: World): UIState = action match {
    case ChoosePlayerBackground(background) =>
      world.characterCreation.characterBackground = background
      WorldView()
    case _ =>
      this
  }

  val rootUIElement = new UIPanel(Size(layoutContext.width, layoutContext.height), List(
    new UIStackPanel(List(
      new UITextButton(Text("1. Soldier", layoutContext.defaultTextColor, layoutContext.defaultFont), ChoosePlayerBackground(Soldier)),
      new UITextButton(Text("2. Nomad", layoutContext.defaultTextColor, layoutContext.defaultFont), ChoosePlayerBackground(Nomad)),
      new UITextButton(Text("3. Merchant", layoutContext.defaultTextColor, layoutContext.defaultFont), ChoosePlayerBackground(Merchant))
    ))
  ))

}

case class WorldView(implicit val layoutContext: LayoutContext) extends UIState {
  override def execute(action: UIAction)(implicit world: World): UIState = action match {
    case _ => this
  }
  val rootUIElement = new UIPanel(Size(layoutContext.width, layoutContext.height), List())
}