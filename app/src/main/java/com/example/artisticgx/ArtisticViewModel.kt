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

    fun getAllFrames(): LiveData<List<Frame>> {
        val allFrames: Flow<List<Frame>> = db.FrameDao().getAll()
        return allFrames.asLiveData()
    }

    fun addNewFrame(frame: ByteArray) {
        val newFrame = Frame(0, frame)
        viewModelScope.launch { db.FrameDao().addFrame(newFrame) }
    }
}