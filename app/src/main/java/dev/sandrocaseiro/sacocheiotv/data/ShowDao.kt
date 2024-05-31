package dev.sandrocaseiro.sacocheiotv.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import dev.sandrocaseiro.sacocheiotv.models.entities.EEpisode
import dev.sandrocaseiro.sacocheiotv.models.entities.EShow

@Dao
interface ShowDao {
    @Query("SELECT * FROM show ORDER BY `order`")
    suspend fun getAll(): List<EShow>
}
