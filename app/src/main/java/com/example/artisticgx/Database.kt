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

@Entity
data class FramedPicture(
        @PrimaryKey(autoGenerate = true) val id: Int = 0,
        @ColumnInfo(typeAffinity = ColumnInfo.BLOB, name = "framed_picture") val framedPicture: ByteArray?
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

    @Query("SELECT Picture.picture FROM Picture WHERE Picture.id LIKE :id")
    fun getPicture(id: Int): Flow<ByteArray>

    @Insert
    suspend fun addPicture(picture: Picture)

    @Delete
    suspend fun delete(picture: Picture)
}

@Dao
interface FramedPictureDao {
    @Query("SELECT * FROM FramedPicture")
    fun getAll(): Flow<List<FramedPicture>>

    @Query("SELECT FramedPicture.framed_picture FROM FramedPicture WHERE FramedPicture.id LIKE :id")
    fun getFramedPicture(id: Int): Flow<ByteArray>

    @Insert
    suspend fun addFramedPicture(framedPicture: FramedPicture)

    @Delete
    suspend fun delete(framedPicture: FramedPicture)
}

@Database(entities = [Frame::class, Picture::class, FramedPicture::class], version = 2)
abstract class ArtisticDB : RoomDatabase() {
    abstract fun FrameDao(): FrameDao
    abstract fun PictureDao(): PictureDao
    abstract fun FramedPictureDao(): FramedPictureDao
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