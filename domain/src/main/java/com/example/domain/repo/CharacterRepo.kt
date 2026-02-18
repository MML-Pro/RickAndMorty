package com.example.domain.repo

import com.example.domain.models.CharacterModel
import com.example.domain.models.CharacterResponse

interface CharacterRepo {

    suspend fun getCharacterByPage(page: Int): CharacterResponse

    suspend fun getCharacterById(id: Int): CharacterModel // 🟢 جديدة

    suspend fun searchAllCharactersByName(characterName: String): CharacterResponse
}