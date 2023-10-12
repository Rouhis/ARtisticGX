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

    @Query("SELECT cloud_anchor FROM Models WHERE id = :id")
    fun getCloudAnchor(id: Int): Flow<String>

    @Query("UPDATE Models SET cloud_anchor = :cloudAnchor WHERE id = :id")
    suspend fun addCloudAnchor(cloudAnchor: String, id: Int)

    @Query("SELECT count(*) FROM Models")
    fun isEmpty(): Flow<Int>

    @Insert
    suspend fun addModel(model: Models)

    @Delete
    suspend fun delete(model: Models)
}

@Dao
interface FramesDao {
    @Query("SELECT * FROM Frames")
    fun getAll(): Flow<List<Frames>>

    @Query("SELECT count(*) FROM Frames")
    fun isEmpty(): Flow<Int>

    @Insert
    suspend fun addFrame(frames: Frames)

    @Delete
    suspend fun delete(frames: Frames)
}