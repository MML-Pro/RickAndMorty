package com.example.data.remote


import com.example.domain.models.Info

data class RemoteEpisodeResponse(
    val info: Info,
    val results: List<RemoteEpisode>
)