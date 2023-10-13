package com.example.artisticgx.data

import android.app.Application
import android.graphics.Picture
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.google.ar.core.Frame
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ArtisticViewModel(application: Application) : AndroidViewModel(application) {
    private val db = ArtisticDB.getInstance(application)

    // Get all models from the DB
    fun getAllModels(): LiveData<List<Models>> {
        val allModels: Flow<List<Models>> = db.ModelsDao().getAll()
        return allModels.asLiveData()
    }

    // Gets a specific Model's cloudAnchor value by giving the Model's ID as a parameter
    fun getCloudAnchor(id: Int): LiveData<String> {
        val cloudAnchor: Flow<String> = db.ModelsDao().getCloudAnchor(id)
        return cloudAnchor.asLiveData()
    }

    // Adds a new cloud anchor to the specific model. Parameters are Model's ID and cloudAnchorID as a string
    fun addNewCloudAnchor(cloudAnchor: String, id: Int) {
        viewModelScope.launch { db.ModelsDao().addCloudAnchor(cloudAnchor, id) }
    }

    // Add a new model to the DB
    fun addNewModel(modelURL: String, modelName: String, modelImage: ByteArray) {
        val newModel = Models(0, modelURL, modelName, null, modelImage)
        viewModelScope.launch { db.ModelsDao().addModel(newModel) }
    }

    // Check if there are no columns in the Models table. Used when adding models to the DB
    fun isEmpty(): LiveData<Int> {
        val isEmpty: Flow<Int> = db.ModelsDao().isEmpty()
        return isEmpty.asLiveData()
    }

    // Get all frames from the DB
    fun getAllFrames(): LiveData<List<Frames>>{
        val allFrames: Flow<List<Frames>> = db.FramesDao().getAll()
        return allFrames.asLiveData()
    }

    // Add a new frame to the DB
    fun addNewFrame(modelURL: String, modelName: String, modelImage: ByteArray) {
        val newFrame = Frames(0, modelURL, modelName, modelImage)
        viewModelScope.launch { db.FramesDao().addFrame(newFrame)}
    }

    // Check if there are no columns in the Frames table. Used when adding frames to the DB
    fun framesIsEmpty(): LiveData<Int> {
        val isEmpty: Flow<Int> = db.FramesDao().isEmpty()
        return isEmpty.asLiveData()
    }

}