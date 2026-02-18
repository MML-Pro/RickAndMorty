package com.example.domain.models


import com.google.gson.annotations.SerializedName


data class CharacterResponse(
    @SerializedName("info")
    val info: Info,
    @SerializedName("results")
    val results: List<CharacterModel>
)
