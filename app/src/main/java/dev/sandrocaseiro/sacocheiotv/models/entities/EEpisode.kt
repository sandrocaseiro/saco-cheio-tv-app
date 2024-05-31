package dev.sandrocaseiro.sacocheiotv.models.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "episode")
data class EEpisode(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    @ColumnInfo(name = "show_id")
    val showId: String,
    @ColumnInfo
    val name: String,
    @ColumnInfo
    val slug: String,
    @ColumnInfo
    val description: String?,
    @ColumnInfo(name = "image_url")
    val imageUrl: String,
    @ColumnInfo
    val date: Date,
    @ColumnInfo(name = "is_watched", defaultValue = "false")
    val isWatched: Boolean = false,
    @ColumnInfo(name = "total_time", defaultValue = "0")
    val totalTime: Long = 0,
    @ColumnInfo(name = "time_watched", defaultValue = "0")
    val timeWatched: Long = 0,
)
