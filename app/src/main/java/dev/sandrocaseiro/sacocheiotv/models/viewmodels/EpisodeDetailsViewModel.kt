package dev.sandrocaseiro.sacocheiotv.models.viewmodels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.sandrocaseiro.sacocheiotv.models.entities.EEpisode
import dev.sandrocaseiro.sacocheiotv.models.views.VEpisodeMedia
import dev.sandrocaseiro.sacocheiotv.services.EpisodeService

import kotlinx.coroutines.launch

// TODO: Add showId, episode as constructor params
class EpisodeDetailsViewModel : ViewModel() {
    private val episodeService = EpisodeService()

    val episode = MutableLiveData<EEpisode>()
    val media = MutableLiveData<Map<VEpisodeMedia, String>>()
    val watched = MutableLiveData<Boolean>()

    fun getEpisode(show: String, episodeId: Int, context: Context) {
        viewModelScope.launch {
            episode.value = episodeService.get(show, episodeId, context)
        }
    }

    fun getEpisodeMedia(show: String, episodeSlug: String, context: Context) {
        viewModelScope.launch {
            media.value = episodeService.getMediaUrls(show, episodeSlug)
        }
    }

    fun toggleEpisodeWatched(showId: String, episodeId: Int, context: Context) {
        viewModelScope.launch {
            episodeService.toggleWatchedStatus(showId, episodeId, context)
            watched.value = !watched.value!!
        }
    }
}
