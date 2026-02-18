package com.example.domain.repo

import com.example.domain.models.EpisodeModel

interface EpisodeRepo {

    suspend fun getEpisode(episodeId: Int): EpisodeModel

    suspend fun getEpisodes(episodeIds: List<Int>): List<EpisodeModel>


    // جلب جميع الحلقات
    suspend fun getAllEpisodes(): List<EpisodeModel>
}