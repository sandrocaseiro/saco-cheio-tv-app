package dev.sandrocaseiro.sacocheiotv.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import dev.sandrocaseiro.sacocheiotv.models.entities.EEpisode

@Dao
interface EpisodeDao {
    @Insert
    suspend fun insert(episode: EEpisode)

    @Query("SELECT * from episode WHERE show_id = :showId AND id = :episodeId")
    suspend fun get(showId: String, episodeId: Int): EEpisode

    @Query("SELECT * from episode WHERE show_id = :showId ORDER BY date DESC")
    suspend fun getAllEpisodesFromShow(showId: String): List<EEpisode>

    @Query("UPDATE episode SET is_watched = NOT is_watched WHERE show_id = :showId AND id = :episodeId")
    suspend fun toggleWatched(showId: String, episodeId: Int)

    @Query("UPDATE episode SET is_watched = 1 WHERE show_id = :showId AND id = :episodeId")
    suspend fun markAsWatched(showId: String, episodeId: Int)

    @Query("UPDATE episode SET total_time = :totalTime, time_watched = :timeWatched WHERE show_id = :showId AND id = :episodeId")
    suspend fun updateTimes(showId: String, episodeId: Int, totalTime: Long, timeWatched: Long)
}
