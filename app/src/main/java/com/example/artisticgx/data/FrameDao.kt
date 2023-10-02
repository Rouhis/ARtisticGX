package com.example.artisticgx.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FrameDao {
    @Query("SELECT * FROM Frame")
    fun getAll(): Flow<List<Frame>>

    @Query("SELECT frame.frame FROM Frame WHERE frame.id LIKE :id")
    fun getFrame(id: Int): Flow<ByteArray>

    @Insert
    suspend fun addFrame(frame: Frame)

    @Delete
    suspend fun delete(frame: Frame)
}