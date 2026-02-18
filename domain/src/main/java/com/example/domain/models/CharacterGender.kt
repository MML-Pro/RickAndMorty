package com.example.domain.models

sealed class CharacterGender(val displayName: String) {

    object Male : CharacterGender("Male")
    object Female : CharacterGender("Female")
    object Genderless : CharacterGender("No Gender")
    object Unknown : CharacterGender("Not specified")

}