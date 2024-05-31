package dev.sandrocaseiro.sacocheiotv.presenters

import androidx.leanback.widget.AbstractDetailsDescriptionPresenter
import dev.sandrocaseiro.sacocheiotv.extensions.toStringFormat
import dev.sandrocaseiro.sacocheiotv.extensions.toTimeString
import dev.sandrocaseiro.sacocheiotv.models.views.VEpisode
import kotlin.time.toDuration

class EpisodeDetailsPresenter : AbstractDetailsDescriptionPresenter() {

    override fun onBindDescription(
            viewHolder: ViewHolder,
            item: Any) {
        val episode = item as VEpisode

        val builder = StringBuilder("Date: ${episode.date.toStringFormat("yyyy-MM-dd")}")
        if (episode.totalTime > 0) {
            builder.append(" | ")
            builder.append("Time: ${episode.totalTime.toTimeString()}")
        }

        if (episode.timeWatched > 0) {
            builder.append(" | ")
            builder.append("Watched: ${episode.timeWatched.toTimeString()}")
        }

        viewHolder.title.text = episode.name
        viewHolder.subtitle.text = builder.toString()
        viewHolder.body.text = episode.description
    }
}
