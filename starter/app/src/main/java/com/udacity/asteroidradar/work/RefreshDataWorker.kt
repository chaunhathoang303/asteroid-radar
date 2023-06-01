package com.udacity.asteroidradar.work

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.database.AsteroidDatabase.Companion.getInstance
import com.udacity.asteroidradar.repository.AsteroidRepository
import retrofit2.HttpException
import java.text.SimpleDateFormat
import java.util.*

class RefreshDataWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {
    private val applicationInfo: ApplicationInfo = appContext.packageManager
        .getApplicationInfo(appContext.packageName, PackageManager.GET_META_DATA)
    private val apiKey = applicationInfo.metaData["YOUR_API_KEY_NAME"] as String
    override suspend fun doWork(): Result {
        val database = getInstance(applicationContext)
        val repository = AsteroidRepository(database)
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        val currentTime = calendar.time
        val startDate = dateFormat.format(currentTime)
        calendar.add(Calendar.DAY_OF_YEAR, 7)
        val nextTime = calendar.time
        val endDate = dateFormat.format(nextTime)
        return try {
            repository.refreshData(startDate, endDate, apiKey)
            Result.success()
        } catch (e: HttpException) {
            Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "RefreshDataWorker"
    }
}