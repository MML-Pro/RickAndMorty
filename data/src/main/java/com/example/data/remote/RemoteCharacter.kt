package com.example.data.remote

import com.example.domain.models.CharacterGender
import com.example.domain.models.CharacterModel
import com.example.domain.models.CharacterStatus
import kotlinx.serialization.Serializable


@Serializable
data class RemoteCharacter(
    val created: String,
    val episode: List<String>,
    val gender: String,
    val id: Int,
    val image: String,
    val location: Location,
    val name: String,
    val origin: Origin,
    val species: String,
    val status: String,
    val type: String,
    val url: String
) {
    @Serializable
    data class Location(
        val name: String,
        val url: String
    )

    @Serializable
    data class Origin(
        val name: String,
        val url: String
    )
}

fun RemoteCharacter.toDomainCharacter(): CharacterModel {
    val characterGender = when (gender.lowercase()) {
        "female" -> CharacterGender.Female
        "male" -> CharacterGender.Male
        "genderless" -> CharacterGender.Genderless
        else -> CharacterGender.Unknown
    }
    val characterStatus = when (status.lowercase()) {
        "alive" -> CharacterStatus.Alive
        "dead" -> CharacterStatus.Dead
        else -> CharacterStatus.Unknown
    }
    return CharacterModel(
        created = created,
        episodeIds = episode.map { it.substring(it.lastIndexOf("/") + 1).toInt() },
        gender = characterGender,
        id = id,
        image = image,
        location = com.example.domain.models.Location(name = location.name, url = location.url),
        name = name,
        origin = com.example.domain.models.Origin(name = origin.name, url = origin.url),
        species = species,
        status = characterStatus,
        type = type,
        url = url
    )
}