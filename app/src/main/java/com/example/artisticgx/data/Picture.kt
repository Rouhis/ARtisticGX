package com.example.artisticgx.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Picture(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "frame_id") val frameId: Int? = null,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB, name = "picture") val picture: ByteArray?
)