package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.*
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.NasaApi
import com.udacity.asteroidradar.database.AsteroidDatabase.Companion.getInstance
import com.udacity.asteroidradar.repository.AsteroidRepository
import kotlinx.coroutines.launch
import retrofit2.await

class MainViewModel(
    application: Application
) : ViewModel() {

    private val database = getInstance(application)

    private val asteroidRepository = AsteroidRepository(database)

    val listAsteroid = asteroidRepository.listAsteroid

    private val _nasaResponse = MutableLiveData<PictureOfDay>()

    val nasaProperty: LiveData<PictureOfDay>
        get() = _nasaResponse

    private val _navigateToDetail = MutableLiveData<Asteroid>()

    val navigateToDetail: LiveData<Asteroid>
        get() = _navigateToDetail

    init {
        getNasaProperty()
    }

    fun onAsteroidClicked(asteroid: Asteroid) {
        _navigateToDetail.value = asteroid
    }

    fun onAsteroidNavigated() {
        _navigateToDetail.value = null
    }

    private fun getNasaProperty() {
        viewModelScope.launch {
            try {
                val dataRes = NasaApi.nasaRetrofitService.getNasaProperties().await()
                if (dataRes.mediaType == "image") {
                    _nasaResponse.value = dataRes
                } else {
                    _nasaResponse.value = PictureOfDay(
                        mediaType = "",
                        title = "",
                        url = "",
                    )
                }
            } catch (e: Exception) {
                _nasaResponse.value = null
            }
        }
    }
}