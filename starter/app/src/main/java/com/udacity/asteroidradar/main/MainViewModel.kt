package com.udacity.asteroidradar.main

import android.app.Application
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager


import androidx.lifecycle.*
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.AsteroidRadarApi
import com.udacity.asteroidradar.api.NasaApi
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidData
import com.udacity.asteroidradar.database.AsteroidDatabase.Companion.getInstance
import com.udacity.asteroidradar.repository.AsteroidRepository
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.await
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel(
    application: Application
) : ViewModel() {

    private val applicationInfo: ApplicationInfo = application.packageManager
        .getApplicationInfo(application.packageName, PackageManager.GET_META_DATA)
    private val apiKey = applicationInfo.metaData["YOUR_API_KEY_NAME"] as String

    private val database = getInstance(application)

    private val repository = AsteroidRepository(database)

    private val _filter = MutableLiveData("SAVED")

    val listAsteroid: LiveData<List<Asteroid>> = Transformations.switchMap(_filter) { filter ->
        Transformations.map(
            when (filter) {
                "TODAY" -> repository.getListAsteroidToday()
                "WEEK" -> repository.getListAsteroidWeek()
                "SAVED" -> repository.listAsteroid
                else -> repository.listAsteroid
            }
        ) {
            it.map { data ->
                Asteroid(
                    data.asteroidId,
                    data.codename,
                    data.closeApproachDate,
                    data.absoluteMagnitude,
                    data.estimatedDiameter,
                    data.relativeVelocity,
                    data.distanceFromEarth,
                    data.isPotentiallyHazardous,
                )
            }
        }
    }

    private val _nasaResponse = MutableLiveData<PictureOfDay>()

    val nasaProperty: LiveData<PictureOfDay>
        get() = _nasaResponse

    private val _navigateToDetail = MutableLiveData<Asteroid>()

    val navigateToDetail: LiveData<Asteroid>
        get() = _navigateToDetail

    init {
        getNasaProperty(apiKey)
        getAsteroidProperty()
    }

    fun onAsteroidClicked(asteroid: Asteroid) {
        _navigateToDetail.value = asteroid
    }

    fun onAsteroidNavigated() {
        _navigateToDetail.value = null
    }

    private fun getAsteroidProperty() {
        viewModelScope.launch {
            try {
                val calendar = Calendar.getInstance()
                val dateFormat =
                    SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())
                val today = dateFormat.format(calendar.time)
                calendar.add(Calendar.DAY_OF_YEAR, 7)
                val endDataOfWeek = dateFormat.format(calendar.time)
                val dataResult = AsteroidRadarApi.retrofitService.getProperties(
                    today,
                    endDataOfWeek,
                    apiKey
                ).await()
                val obj = JSONObject(dataResult)
                val data = parseAsteroidsJsonResult(obj)
                val asteroidList = data.map { asteroid ->
                    AsteroidData(
                        id = asteroid.id,
                        asteroid.id,
                        asteroid.codename,
                        asteroid.closeApproachDate,
                        asteroid.absoluteMagnitude,
                        asteroid.estimatedDiameter,
                        asteroid.relativeVelocity,
                        asteroid.distanceFromEarth,
                        asteroid.isPotentiallyHazardous,
                    )
                }
                for (asteroid in asteroidList) {
                    database.asteroidDatabaseDao.insertAll(asteroid)
                }

            } catch (e: Exception) {

            }
        }
    }

    private fun getNasaProperty(apiKey: String) {
        viewModelScope.launch {
            try {
                val dataRes = NasaApi.nasaRetrofitService.getNasaProperties(apiKey).await()
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

    fun updateFilter(filter: String) {
        when (filter) {
            "TODAY" -> _filter.value = "TODAY"
            "WEEK" -> _filter.value = "WEEK"
            "SAVED" -> _filter.value = "SAVED"
            else -> _filter.value = "SAVED"
        }

    }
}