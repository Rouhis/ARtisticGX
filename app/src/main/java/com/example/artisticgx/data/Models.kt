package com.example.artisticgx.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Models(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "model_url") val modelUrl: String?
)

