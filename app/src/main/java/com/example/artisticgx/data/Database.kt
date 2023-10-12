package com.example.artisticgx.data

import android.content.Context
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow


@Database(entities = [Models::class, Frames::class], version = 9)
abstract class ArtisticDB : RoomDatabase() {
    abstract fun ModelsDao(): ModelsDao
    abstract fun FramesDao(): FramesDao
    companion object {
        @Volatile
        private var INSTANCE: ArtisticDB? = null
        // A function, that creates an instance of the DB
        fun getInstance(context: Context): ArtisticDB {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(context,
                        ArtisticDB::class.java, "ArtisticDB")
                        .fallbackToDestructiveMigration().build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}