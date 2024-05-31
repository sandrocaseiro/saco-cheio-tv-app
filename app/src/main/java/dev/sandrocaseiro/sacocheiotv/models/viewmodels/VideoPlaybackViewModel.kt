package dev.sandrocaseiro.sacocheiotv.models.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.sandrocaseiro.sacocheiotv.services.EpisodeService

import kotlinx.coroutines.launch

// TODO: Add showId, episode as constructor params
class VideoPlaybackViewModel : ViewModel() {
    private val episodeService = EpisodeService()

    fun updateWatchTime(
        showId: String,
        episodeId: Int,
        totalTime: Long,
        watchedTime: Long,
        context: Context
    ) {
        viewModelScope.launch {
            episodeService.updateTimeWatched(showId, episodeId, totalTime, watchedTime, context)
        }
    }
}
