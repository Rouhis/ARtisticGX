package com.example.artisticgx

import android.content.Context
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

// Frames and Pictures are saved to the DB as ByteArrays
@Entity
data class Frame(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB, name = "frame") val frame: ByteArray?
)

@Entity
data class Picture(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "frame_id") val frameId: Int? = null,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB, name = "picture") val picture: ByteArray?
)

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

@Dao
interface PictureDao {
    @Query("SELECT * FROM Picture")
    fun getAll(): Flow<List<Picture>>

    @Insert
    suspend fun addPicture(picture: Picture)

    @Delete
    suspend fun delete(picture: Picture)
}

@Database(entities = [Frame::class, Picture::class], version = 1)
abstract class ArtisticDB : RoomDatabase() {
    abstract fun FrameDao(): FrameDao
    abstract fun PictureDao(): PictureDao

    companion object {
        @Volatile
        private var INSTANCE: ArtisticDB? = null
        // A function, that creates an instance of the DB
        fun getInstance(context: Context): ArtisticDB {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(context,
                        ArtisticDB::class.java, "ArtisticDB")
                        .fallbackToDestructiveMigration().build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}