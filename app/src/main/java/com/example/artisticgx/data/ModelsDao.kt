package com.example.artisticgx.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ModelsDao {
    @Query("SELECT * FROM Models")
    fun getAll(): Flow<List<Models>>

    @Insert
    suspend fun addModel(model: Models)

    @Delete
    suspend fun delete(model: Models)
}