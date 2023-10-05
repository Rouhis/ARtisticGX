package com.example.artisticgx.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface QrcodesDao {
    @Query("SELECT * FROM Qrcodes")
    fun getAll(): Flow<List<Qrcodes>>

    @Insert
    suspend fun addQrcode(qrcode: Qrcodes)

    @Delete
    suspend fun delete(qrcode: Qrcodes)
}