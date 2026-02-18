package com.example.domain.usecase

import com.example.domain.models.EpisodeModel
import com.example.domain.repo.EpisodeRepo

class GetEpisodeUseCase(private val episodeRepo: EpisodeRepo)  {

    suspend operator fun invoke(episodeId: Int): EpisodeModel {
        return episodeRepo.getEpisode(episodeId)
    }

}