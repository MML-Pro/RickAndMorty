package com.example.rickandmorty.di

import com.example.domain.repo.CharacterRepo
import com.example.domain.repo.EpisodeRepo
import com.example.domain.usecase.GetAllEpisodesUseCase
import com.example.domain.usecase.GetCharacterByIdUseCase
import com.example.domain.usecase.GetCharacterByPageUseCase
import com.example.domain.usecase.GetEpisodeUseCase
import com.example.domain.usecase.GetEpisodesUseCase
import com.example.domain.usecase.SearchAllCharactersByNameUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    fun provideCharacterUseCaseUseCase(characterRepo: CharacterRepo): GetCharacterByIdUseCase {
        return GetCharacterByIdUseCase(characterRepo)
    }

    @Provides
    fun provideEpisodeUseCase(episodeRepo: EpisodeRepo): GetEpisodeUseCase {
        return GetEpisodeUseCase(episodeRepo)
    }

    @Provides
    fun provideEpisodesUseCase(episodeRepo: EpisodeRepo): GetEpisodesUseCase {
        return GetEpisodesUseCase(episodeRepo)
    }

    @Provides
    fun provideGetCharacterByPageUseCase(characterRepo: CharacterRepo): GetCharacterByPageUseCase {
        return GetCharacterByPageUseCase(characterRepo)
    }

    @Provides
    fun provideGetAllEpisodesUseCase(episodeRepo: EpisodeRepo): GetAllEpisodesUseCase {
        return GetAllEpisodesUseCase(episodeRepo)
    }


    @Provides
    fun provideSearchAllCharactersUseCase(characterRepo: CharacterRepo): SearchAllCharactersByNameUseCase {
        return SearchAllCharactersByNameUseCase(characterRepo)


    }
}