package com.udacity.asteroidradar.repository

import androidx.lifecycle.LiveData
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.api.AsteroidRadarApi
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidData
import com.udacity.asteroidradar.database.AsteroidDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.await
import java.text.SimpleDateFormat
import java.util.*

class AsteroidRepository(private val database: AsteroidDatabase) {

    val listAsteroid: LiveData<List<AsteroidData>> = database.asteroidDatabaseDao.getAllAsteroid()

    fun getListAsteroidToday(): LiveData<List<AsteroidData>> {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())
        val today = dateFormat.format(calendar.time)

        return database.asteroidDatabaseDao.getAsteroidToday(today)
    }

    fun getListAsteroidWeek(): LiveData<List<AsteroidData>> {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        val startTime = dateFormat.format(calendar.time)
        calendar.add(Calendar.DAY_OF_YEAR, 7)
        val endTime = dateFormat.format(calendar.time)

        return database.asteroidDatabaseDao.getAsteroidsThisWeek(startTime, endTime)
    }


    suspend fun refreshData(startDate: String, endDate: String, apiKey: String) {
        withContext(Dispatchers.IO) {
            database.asteroidDatabaseDao.clear()
            val listResult =
                AsteroidRadarApi.retrofitService.getProperties(startDate, endDate, apiKey).await()
            val obj = JSONObject(listResult)
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

        }
    }
}