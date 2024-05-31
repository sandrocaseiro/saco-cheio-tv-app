package dev.sandrocaseiro.sacocheiotv.models.viewmodels

import android.content.Context
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.sandrocaseiro.sacocheiotv.models.entities.EShow
import dev.sandrocaseiro.sacocheiotv.services.EpisodeService
import dev.sandrocaseiro.sacocheiotv.services.ShowService
import kotlinx.coroutines.launch

class MainViewModel: ViewModel() {
    private val showService = ShowService()
    private val episodeService = EpisodeService()

    val shows = MutableLiveData<List<EShow>>()

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
}
