package dev.sandrocaseiro.sacocheiotv.services

import android.content.Context
import dev.sandrocaseiro.sacocheiotv.data.AppDatabase
import dev.sandrocaseiro.sacocheiotv.mappings.toEEpisode
import dev.sandrocaseiro.sacocheiotv.mappings.toVEpisode
import dev.sandrocaseiro.sacocheiotv.models.entities.EEpisode
import dev.sandrocaseiro.sacocheiotv.models.entities.EShow
import dev.sandrocaseiro.sacocheiotv.models.views.VEpisode

class EpisodeService {
    private val apiService = ApiService()

    suspend fun getAllFromShow(show: EShow, context: Context): List<VEpisode> {
        val dao = AppDatabase.buildDatabase(context).episodeDao()
        val localEps = dao.getAllEpisodesFromShow(show.id)
        val apiEps = apiService.getEpisodes(show.id)
        val eps = mutableListOf<VEpisode>()

        for (ep in apiEps) {
            if (localEps.any() && ep.id == localEps[0].id)
                break

            eps.add(ep.toVEpisode(show.id, show.imageUrl))
            dao.insert(ep.toEEpisode(show.id))
        }

        for (ep in localEps) {
            eps.add(ep.toVEpisode(show.id, show.imageUrl))
        }

        return eps
    }

    suspend fun get(showId: String, episodeId: Int, context: Context): EEpisode {
        val dao = AppDatabase.buildDatabase(context).episodeDao()

        return dao.get(showId, episodeId)
    }

    suspend fun getVideoUrl(show: String, episodeSlug: String): String? {
        return apiService.getEpisodeVideoUrl(show, episodeSlug)
    }

    suspend fun toggleWatchedStatus(showId: String, episodeId: Int, context: Context) {
        val dao = AppDatabase.buildDatabase(context).episodeDao()
        dao.toggleWatched(showId, episodeId)
    }

    suspend fun updateTimeWatched(
        showId: String,
        episodeId: Int,
        totalTime: Long,
        timeWatched: Long,
        context: Context
    ) {
        val dao = AppDatabase.buildDatabase(context).episodeDao()
        dao.updateTimes(showId, episodeId, totalTime, timeWatched)

        val watchedPercentage = (timeWatched * 100) / totalTime
        if (watchedPercentage >= 95) {
            dao.markAsWatched(showId, episodeId)
        }
    }
}
