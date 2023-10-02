package com.example.artisticgx.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PictureDao {
    @Query("SELECT * FROM Picture")
    fun getAll(): Flow<List<Picture>>

    @Query("SELECT Picture.picture FROM Picture WHERE Picture.id LIKE :id")
    fun getPicture(id: Int): Flow<ByteArray>

    @Insert
    suspend fun addPicture(picture: Picture)

    @Delete
    suspend fun delete(picture: Picture)
}