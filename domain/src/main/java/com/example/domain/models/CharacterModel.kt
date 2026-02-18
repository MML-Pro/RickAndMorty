package com.example.domain.models


import com.google.gson.annotations.SerializedName

data class CharacterModel(
    @SerializedName("created")
    val created: String,
    @SerializedName("episode")
    val episodeIds: List<Int>,
    @SerializedName("gender")
    val gender: CharacterGender,
    @SerializedName("id")
    val id: Int,
    @SerializedName("image")
    val image: String,
    @SerializedName("location")
    val location: Location,
    @SerializedName("name")
    val name: String,
    @SerializedName("origin")
    val origin: Origin,
    @SerializedName("species")
    val species: String,
    @SerializedName("status")
    val status: CharacterStatus,
    @SerializedName("type")
    val type: String,
    @SerializedName("url")
    val url: String
)