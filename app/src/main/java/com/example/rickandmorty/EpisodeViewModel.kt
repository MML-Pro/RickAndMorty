package com.example.rickandmorty.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.models.EpisodeModel
import com.example.domain.usecase.GetEpisodeUseCase
import com.example.domain.usecase.GetEpisodesUseCase
import com.example.rickandmorty.ui.utils.Resource
import com.example.rickandmorty.ui.utils.toResourceError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EpisodeViewModel @Inject constructor(
    private val getEpisodeUseCase: GetEpisodeUseCase,
    private val getEpisodesUseCase: GetEpisodesUseCase
) : ViewModel() {

    companion object {
        private const val TAG = "EpisodeViewModel"
    }

    private val _episode = MutableStateFlow<Resource<EpisodeModel>>(Resource.Initial)
    val episode: StateFlow<Resource<EpisodeModel>> = _episode


    private val _episodes = MutableStateFlow<Resource<List<EpisodeModel>>>(Resource.Initial)
    val episodes: StateFlow<Resource<List<EpisodeModel>>> = _episodes

    fun getEpisode(episodeId: Int) {
        _episode.value = Resource.Loading

        viewModelScope.launch {
            try {
                val response = getEpisodeUseCase(episodeId)
                _episode.value = Resource.Success(response)
            } catch (e: Exception) {
                Log.e(TAG, "getEpisode error", e)
                _episode.value = e.toResourceError()
            }
        }
    }

    fun getEpisodes(episodeIds: List<Int>) {
        _episodes.value = Resource.Loading

        viewModelScope.launch {
            try {
                val data = getEpisodesUseCase(episodeIds)
                _episodes.value = Resource.Success(data)
            } catch (e: Exception) {
                _episodes.value = e.toResourceError()
            }
        }
    }
}
