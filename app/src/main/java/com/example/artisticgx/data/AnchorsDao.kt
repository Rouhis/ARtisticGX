package com.example.artisticgx.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AnchorsDao {
    @Query("SELECT * FROM Anchors")
    fun getAll(): Flow<List<Anchors>>

    @Insert
    suspend fun addAnchor(anchor: Anchors)
}