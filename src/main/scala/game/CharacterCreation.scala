package game

class CharacterCreation {

  var gender: Gender = Male
  var characterBackground: CharacterBackground = Soldier

  def build(): Being =
    Being(gender, characterBackground)

}
