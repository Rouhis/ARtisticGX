package com.example.artisticgx.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ArtisticViewModel(application: Application) : AndroidViewModel(application) {
    private val db = ArtisticDB.getInstance(application)

    // Get all frames from the DB
    fun getAllFrames(): LiveData<List<Frame>> {
        val allFrames: Flow<List<Frame>> = db.FrameDao().getAll()
        return allFrames.asLiveData()
    }

    // Add a new frame to the DB
    fun addNewFrame(frame: ByteArray, rowCount: Int) {
        val newFrames = Frame(0, frame)
        if (rowCount <= 7) {
            viewModelScope.launch {
                db.FrameDao().addFrame(newFrames)
                println("new frame added $newFrames")
            }
        }
    }

    // Get a frame from the DB with the given ID
    fun getFrame(): LiveData<ByteArray> {
        val newFrame: Flow<ByteArray> = db.FrameDao().getFrame(1)
        return newFrame.asLiveData()
    }

    fun isEmpty(): LiveData<Int> {
        val isEmpty: Flow<Int> = db.FrameDao().isEmpty()
        return isEmpty.asLiveData()
    }

    fun getAllPictures(): LiveData<List<Picture>> {
        val allPictures: Flow<List<Picture>> = db.PictureDao().getAll()
        return allPictures.asLiveData()
    }

    fun getPicture(): LiveData<ByteArray> {
        val newPicture: Flow<ByteArray> = db.PictureDao().getPicture(1)
        return newPicture.asLiveData()
    }

    fun addNewPicture(picture: ByteArray) {
        val addNewPicture = Picture(0, null, picture)
        viewModelScope.launch { db.PictureDao().addPicture(addNewPicture) }
    }

}