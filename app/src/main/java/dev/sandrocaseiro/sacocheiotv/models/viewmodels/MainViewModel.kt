package dev.sandrocaseiro.sacocheiotv.models.viewmodels

import android.content.Context
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.sandrocaseiro.sacocheiotv.mappings.toVEpisode
import dev.sandrocaseiro.sacocheiotv.models.entities.EShow
import dev.sandrocaseiro.sacocheiotv.models.views.VEpisode
import dev.sandrocaseiro.sacocheiotv.services.EpisodeService
import dev.sandrocaseiro.sacocheiotv.services.ShowService
import kotlinx.coroutines.launch

class MainViewModel: ViewModel() {
    private val showService = ShowService()
    private val episodeService = EpisodeService()

    val shows = MutableLiveData<List<EShow>>()
    val episodeInfo = MutableLiveData<VEpisode>()

    fun getAllShows(context: Context) {
        viewModelScope.launch {
            shows.value = showService.getAll(context)
        }
    }

    fun getAllEpisodes(show: EShow, adapter: ArrayObjectAdapter, context: Context) {
        viewModelScope.launch {
            val eps = episodeService.getAllFromShow(show, context)
            adapter.clear()
            adapter.addAll(0, eps)
            adapter.notifyArrayItemRangeChanged(0, eps.size)
        }
    }

    fun getUpdatedEpisodeInfo(ep: VEpisode, context: Context) {
        viewModelScope.launch {
            episodeInfo.value = episodeService.get(ep.showId, ep.id, context)
                .toVEpisode(ep.showId, ep.showImageUrl)
        }
    }
}
