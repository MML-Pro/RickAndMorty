package com.example.data.remote

import com.example.domain.models.Info
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class RemoteCharacterResponse(
    @Contextual
    val info: Info,
    val results: List<RemoteCharacter>
)