package com.example.artisticgx.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Frame(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB, name = "frame") val frame: ByteArray?
)
