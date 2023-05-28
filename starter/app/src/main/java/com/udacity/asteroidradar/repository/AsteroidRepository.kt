package com.udacity.asteroidradar.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.api.AsteroidRadarApi
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidData
import com.udacity.asteroidradar.database.AsteroidDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.await

class AsteroidRepository(private val database: AsteroidDatabase) {

    val listAsteroid: LiveData<List<Asteroid>> =
        Transformations.map(database.asteroidDatabaseDao.getAllAsteroid()) {
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

    suspend fun refreshData() {
        withContext(Dispatchers.IO) {
            val listResult = AsteroidRadarApi.retrofitService.getProperties().await()
            val obj = JSONObject(listResult)
            val data = parseAsteroidsJsonResult(obj)
            val asteroidList = data.map { asteroid ->
                AsteroidData(
                    null,
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

        }
    }
}