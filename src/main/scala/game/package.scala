package object game {

  sealed trait Gender
  case object Male extends Gender
  case object Female extends Gender

  sealed trait CharacterBackground
  case object Soldier extends CharacterBackground
  case object Nomad extends CharacterBackground
  case object Merchant extends CharacterBackground


  case class Character(gender: Gender, characterBackground: CharacterBackground)
}
