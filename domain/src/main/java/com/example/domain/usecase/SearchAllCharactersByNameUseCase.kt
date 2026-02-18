package com.example.domain.usecase

import com.example.domain.repo.CharacterRepo

class SearchAllCharactersByNameUseCase(private val characterRepo: CharacterRepo) {

    suspend operator fun invoke(name: String) = characterRepo.searchAllCharactersByName(name)

}