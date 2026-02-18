package com.example.rickandmorty.di

import com.example.data.remote.ApiService
import com.example.data.repo.CharacterRepoImpl
import com.example.data.repo.EpisodeRepoImpl
import com.example.domain.repo.CharacterRepo
import com.example.domain.repo.EpisodeRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RepoModule {

    @Provides
    fun provideCharacterRepo(apiService: ApiService): CharacterRepo {
        return CharacterRepoImpl(apiService)
    }

    @Provides
    fun provideEpisodeRepo(apiService: ApiService): EpisodeRepo {
        return EpisodeRepoImpl(apiService)
    }
}