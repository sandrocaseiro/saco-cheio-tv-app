package dev.sandrocaseiro.sacocheiotv.services

import android.content.Context
import dev.sandrocaseiro.sacocheiotv.data.AppDatabase
import dev.sandrocaseiro.sacocheiotv.models.entities.EShow

class ShowService {
    suspend fun getAll(context: Context): List<EShow> {
        val dao = AppDatabase.buildDatabase(context).showDao()

        return dao.getAll()
    }
}
