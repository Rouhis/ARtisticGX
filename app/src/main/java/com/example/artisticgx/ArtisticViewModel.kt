package com.example.artisticgx

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
    fun addNewFrame(frame: ByteArray) {
        val newFrames = Frame(0, frame)
        viewModelScope.launch { db.FrameDao().addFrame(newFrames) }
    }

    // Get a frame from the DB with the given ID
    fun getFrame(): LiveData<ByteArray> {
        val newFrame: Flow<ByteArray> = db.FrameDao().getFrame(1)
        return newFrame.asLiveData()
    }
}