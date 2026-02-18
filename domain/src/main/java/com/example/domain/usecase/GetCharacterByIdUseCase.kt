package com.example.domain.usecase

import com.example.domain.models.CharacterModel
import com.example.domain.repo.CharacterRepo


class GetCharacterByIdUseCase(private val characterRepo: CharacterRepo) {


    suspend operator fun invoke(id: Int): CharacterModel {
        return characterRepo.getCharacterById(id)
    }
}