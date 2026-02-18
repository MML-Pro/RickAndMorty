package com.example.rickandmorty.ui.screens.all_episodes

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.models.EpisodeModel
import com.example.domain.usecase.GetAllEpisodesUseCase
import com.example.rickandmorty.ui.utils.Resource
import com.example.rickandmorty.ui.utils.toResourceError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllEpisodesViewModel @Inject constructor(
    private val getAllEpisodesUseCase: GetAllEpisodesUseCase
) : ViewModel() {

    companion object {
        private const val TAG = "AllEpisodesViewModel"
    }

    private val _episodes =
        MutableStateFlow<Resource<Map<String, List<EpisodeModel>>>>(Resource.Initial)
    val episodes = _episodes.asStateFlow()

    fun getAllEpisodes(forceRefresh: Boolean = false) {
        if (!forceRefresh && _episodes.value is Resource.Success) return

        _episodes.value = Resource.Loading

        viewModelScope.launch {
            try {
                val episodeList = getAllEpisodesUseCase()

                // قسّم الحلقات حسب الموسم
                val groupedBySeasons = episodeList
                    .groupBy { episode ->
                        "Season ${episode.seasonNumber}"
                    }
                    .toSortedMap() // ترتيب المواسم

                _episodes.value = Resource.Success(groupedBySeasons)
            } catch (e: Exception) {
                Log.e(TAG, "getAllEpisodes error", e)
                _episodes.value = e.toResourceError()
            }
        }
    }
}