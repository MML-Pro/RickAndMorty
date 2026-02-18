package com.example.data.remote

import com.example.domain.models.CharacterModel
import com.example.domain.models.CharacterResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("character")
    suspend fun getCharacterByPage(@Query("page") page: Int): RemoteCharacterResponse

    @GET("character/{id}")
    suspend fun getCharacterById(@Path("id") id: Int): RemoteCharacter


    // مثلاً لو الـ API بتاع الحلقة بيكون بالشكل:
    // https://rickandmortyapi.com/api/episode/28
    @GET("episode/{id}")
    suspend fun getEpisode(@Path("id") episodeId: Int): RemoteEpisode

    @GET("episode/{ids}")
    suspend fun getEpisodesByIds(@Path("ids") ids: String): List<RemoteEpisode>


    // جلب جميع الحلقات مع pagination
    @GET("episode")
    suspend fun getAllEpisodes(@Query("page") page: Int): RemoteEpisodeResponse

    @GET("character")
    suspend fun searchAllCharactersByName(
        @Query("name") name: String
    ): RemoteCharacterResponse


}