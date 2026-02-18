package com.example.data.repo

import com.example.data.remote.ApiService
import com.example.data.remote.toDomainCharacter
import com.example.domain.models.CharacterModel
import com.example.domain.models.CharacterResponse
import com.example.domain.repo.CharacterRepo

class CharacterRepoImpl(private val apiService: ApiService) : CharacterRepo {

    override suspend fun getCharacterByPage(page: Int): CharacterResponse {
        // 1. جلب البيانات الخام من الشبكة
        val remoteResponse = apiService.getCharacterByPage(page)

        // 2. تحويل قائمة RemoteCharacter إلى قائمة CharacterModel
        val domainCharacters = remoteResponse.results.map { remoteCharacter ->
            remoteCharacter.toDomainCharacter()
        }

        // 3. إعادة كائن CharacterResponse مع قائمة نماذج النطاق المحولة
        return CharacterResponse(
            info = remoteResponse.info,
            results = domainCharacters
        )
    }

    override suspend fun getCharacterById(id: Int): CharacterModel {
        val remoteCharacter = apiService.getCharacterById(id)
        return remoteCharacter.toDomainCharacter()
    }

    override suspend fun searchAllCharactersByName(characterName: String): CharacterResponse {
        // 1. جلب البيانات من الـ API
        val remoteResponse = apiService.searchAllCharactersByName(characterName)

        // 2. تحويل قائمة RemoteCharacter إلى قائمة CharacterModel
        val domainCharacters = remoteResponse.results.map { remoteCharacter ->
            remoteCharacter.toDomainCharacter()
        }

        // 3. إرجاع النتيجة بصيغة CharacterResponse
        return CharacterResponse(
            info = remoteResponse.info,
            results = domainCharacters
        )
    }

}