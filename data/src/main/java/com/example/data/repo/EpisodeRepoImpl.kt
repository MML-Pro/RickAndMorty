package com.example.data.repo

import android.util.Log
import com.example.data.remote.ApiService
import com.example.data.remote.toDomainEpisode
import com.example.domain.models.EpisodeModel
import com.example.domain.repo.EpisodeRepo

class EpisodeRepoImpl(private val apiService: ApiService) : EpisodeRepo {

    companion object {
        private const val TAG = "EpisodeRepoImpl"
    }

    override suspend fun getEpisode(episodeId: Int): EpisodeModel {
        val remoteResponse = apiService.getEpisode(episodeId)
        return remoteResponse.toDomainEpisode()
    }

    override suspend fun getEpisodes(episodeIds: List<Int>): List<EpisodeModel> {
        return if (episodeIds.size == 1) {
            listOf(getEpisode(episodeIds[0]))
        } else {
            val idsCommaSeparated = episodeIds.joinToString(",")
            apiService.getEpisodesByIds(idsCommaSeparated).map { it.toDomainEpisode() }
        }
    }

    override suspend fun getAllEpisodes(): List<EpisodeModel> {
        val allEpisodes = mutableListOf<EpisodeModel>()
        var currentPage = 1
        var hasMorePages = true

        while (hasMorePages) {
            try {
                val response = apiService.getAllEpisodes(currentPage)

                // تحويل وإضافة الحلقات
                allEpisodes.addAll(response.results.map { it.toDomainEpisode() })

                // التحقق من وجود صفحة تالية
                hasMorePages = response.info.next != null
                currentPage++

            } catch (e: Exception) {
                // في حالة حدوث خطأ، نوقف الـ loop ونرجع ما تم جلبه
                Log.e(TAG, "getAllEpisodes: ${e.message}")
                Log.e(TAG, "getAllEpisodes: ${e.toString()}")
                hasMorePages = false
            }
        }

        return allEpisodes
    }
}