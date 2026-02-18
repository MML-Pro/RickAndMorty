package com.example.domain.usecase

import com.example.domain.models.EpisodeModel
import com.example.domain.repo.EpisodeRepo

class GetEpisodesUseCase(private val episodeRepo: EpisodeRepo) {

    suspend operator fun invoke(episodeIds: List<Int>): List<EpisodeModel> {
        return episodeRepo.getEpisodes(episodeIds)
    }
}
