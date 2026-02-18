package com.example.domain.models


import com.google.gson.annotations.SerializedName

data class EpisodeModel(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,

    val seasonNumber: Int,
    val episodeNumber: Int,

    @SerializedName("air_date")
    val airDate: String,
    @SerializedName("characters")
    val characters: List<Int>

)