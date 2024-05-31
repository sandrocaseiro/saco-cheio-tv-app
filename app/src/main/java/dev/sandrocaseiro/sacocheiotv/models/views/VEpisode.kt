package dev.sandrocaseiro.sacocheiotv.models.views

import java.io.Serializable
import java.util.Date

data class VEpisode(
    val id: Int,
    val name: String,
    val slug: String,
    val description: String?,
    val imageUrl: String,
    val showId: String,
    val showImageUrl: String,
    val date: Date,
    var isWatched: Boolean = false,
    var timeWatched: Long = 0,
    var totalTime: Long = 0,
): Serializable {
    val watchedPercentage: Float
        get() {
            if (timeWatched == 0.toLong() || totalTime == 0.toLong())
                return 0.0F

            return ((timeWatched * 100) / totalTime).toFloat()
        }
}
