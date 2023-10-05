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

    // Add a new model to the DB
    fun addNewModel(modelURL: String, modelName: String, modelImage: ByteArray) {
        val newModel = Models(0, modelURL, modelName, modelImage)
        viewModelScope.launch { db.ModelsDao().addModel(newModel)}
    }
}