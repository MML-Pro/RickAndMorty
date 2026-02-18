package com.example.data.remote

import com.example.domain.models.EpisodeModel

data class RemoteEpisode(
    val id: Int,
    val name: String,
    val episode: String,
    val air_date: String,
    val characters: List<String>
)

// تحويل آمن وموثوق
fun RemoteEpisode.toDomainEpisode(): EpisodeModel {
    return EpisodeModel(
        id = id,
        name = name,

        // مثال: "S03E07"
        seasonNumber = episode.filter { it.isDigit() }.take(2).toInt(),
        episodeNumber = episode.filter { it.isDigit() }.takeLast(2).toInt(),
        airDate = air_date,
        characters = characters.map {
            it.substring(it.lastIndexOf("/") + 1).toInt()
        }
    )
}
