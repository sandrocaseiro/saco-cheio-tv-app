package dev.sandrocaseiro.sacocheiotv.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.provider.ContactsContract.Data
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import dev.sandrocaseiro.sacocheiotv.data.converters.DateConverter
import dev.sandrocaseiro.sacocheiotv.models.entities.EEpisode
import dev.sandrocaseiro.sacocheiotv.models.entities.EShow

@Database(entities = [EShow::class, EEpisode::class], version = 1)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun episodeDao(): EpisodeDao
    abstract fun showDao(): ShowDao

    companion object {
        private var mRoomDatabase: AppDatabase? = null

        fun buildDatabase(context: Context): AppDatabase {
            if (mRoomDatabase == null) {
                mRoomDatabase = Room
                    .databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "sacocheiotv_db"
                    )
                    .addCallback(DatabaseCallback())
                    .build()
            }

            return mRoomDatabase!!
        }
    }

    private class DatabaseCallback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            db.run {
                beginTransaction()
                try {
                    insert("show",
                        SQLiteDatabase.CONFLICT_IGNORE,
                        ContentValues(4).apply {
                            put("id", "saco-cheio")
                            put("name", "Saco Cheio")
                            put("image_url", "https://sacocheioassets.b-cdn.net/imgs/1/perfil.jpg")
                            put("`order`", 1)
                        }
                    )
                    insert("show",
                        SQLiteDatabase.CONFLICT_IGNORE,
                        ContentValues(4).apply {
                            put("id", "desinformacao")
                            put("name", "Desinformacao")
                            put("image_url", "https://sacocheioassets.b-cdn.net/imgs/4/perfil.jpg")
                            put("`order`", 2)
                        }
                    )
                    insert("show",
                        SQLiteDatabase.CONFLICT_IGNORE,
                        ContentValues(4).apply {
                            put("id", "cagando-e-andando")
                            put("name", "Cagando e Andando")
                            put("image_url", "https://sacocheioassets.b-cdn.net/imgs/3/perfil.jpg")
                            put("`order`", 3)
                        }
                    )
                    insert("show",
                        SQLiteDatabase.CONFLICT_IGNORE,
                        ContentValues(4).apply {
                            put("id", "notas-sobre-notas")
                            put("name", "Notas Sobre Notas")
                            put("image_url", "https://sacocheioassets.b-cdn.net/imgs/11977/perfil.jpg")
                            put("`order`", 4)
                        }
                    )
                    insert("show",
                        SQLiteDatabase.CONFLICT_IGNORE,
                        ContentValues(4).apply {
                            put("id", "saco-animado")
                            put("name", "Saco Animado")
                            put("image_url", "https://sacocheioassets.b-cdn.net/imgs/23453/perfil.jpg")
                            put("`order`", 5)
                        }
                    )
                    insert("show",
                        SQLiteDatabase.CONFLICT_IGNORE,
                        ContentValues(4).apply {
                            put("id", "tarja-preta-fm")
                            put("name", "Tarja Preta FM")
                            put("image_url", "https://sacocheioassets.b-cdn.net/imgs/5849/perfil.jpg")
                            put("`order`", 6)
                        }
                    )
                    setTransactionSuccessful()
                } finally {
                    endTransaction()
                }
            }
        }
    }
}
