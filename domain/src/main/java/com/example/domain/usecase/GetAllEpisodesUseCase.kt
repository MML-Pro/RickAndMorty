package com.example.domain.usecase

import com.example.domain.repo.EpisodeRepo

class GetAllEpisodesUseCase (private val episodeRepository: EpisodeRepo){

    suspend operator fun invoke() = episodeRepository.getAllEpisodes()
}