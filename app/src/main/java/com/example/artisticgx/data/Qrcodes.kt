package com.example.artisticgx.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Qrcodes(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "model_id") val modelId: Int?
)
