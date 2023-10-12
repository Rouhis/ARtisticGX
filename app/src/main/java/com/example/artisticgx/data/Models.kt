package com.example.artisticgx.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Models(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "model_url") val modelUrl: String?,
    @ColumnInfo(name = "name") val name: String?,
    @ColumnInfo(name = "cloud_anchor") val cloudAnchor: String?,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB, name = "image") val image: ByteArray?
)
@Entity
data class Frames(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "frame_url") val modelUrl: String?,
    @ColumnInfo(name = "name") val name: String?,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB, name = "image") val image: ByteArray?
)

