package com.example.artisticgx.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ModelsDao {

    // Get everything from the Models table
    @Query("SELECT * FROM Models")
    fun getAll(): Flow<List<Models>>

    // Get a cloudAnchor for the selected model using ID
    @Query("SELECT cloud_anchor FROM Models WHERE id = :id")
    fun getCloudAnchor(id: Int): Flow<String>

    // Update the Models table to add a new cloudAnchor to the selected model using ID
    @Query("UPDATE Models SET cloud_anchor = :cloudAnchor WHERE id = :id")
    suspend fun addCloudAnchor(cloudAnchor: String, id: Int)

    // Count all the rows for the Models table
    @Query("SELECT count(*) FROM Models")
    fun isEmpty(): Flow<Int>

    // Add a new model to the DB
    @Insert
    suspend fun addModel(model: Models)

    // Delete a model from the DB
    @Delete
    suspend fun delete(model: Models)
}

@Dao
interface FramesDao {

    // Get everything from the Frames table
    @Query("SELECT * FROM Frames")
    fun getAll(): Flow<List<Frames>>

    // Count all the rows for the Frames table
    @Query("SELECT count(*) FROM Frames")
    fun isEmpty(): Flow<Int>

    // Add a new frame to the DB
    @Insert
    suspend fun addFrame(frames: Frames)

    // Delete a frame from the DB
    @Delete
    suspend fun delete(frames: Frames)
}