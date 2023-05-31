package com.udacity.asteroidradar.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.udacity.asteroidradar.Asteroid

@Dao
interface AsteroidDatabaseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg asteroidData: AsteroidData)

    @Update
    suspend fun update(vararg asteroidData: AsteroidData)

    @Query("SELECT * FROM asteroid_radar_table ORDER BY close_approach_date ASC")
    fun getAllAsteroid(): LiveData<List<AsteroidData>>

    @Query("SELECT * FROM asteroid_radar_table WHERE close_approach_date = :today ORDER BY close_approach_date ASC")
    fun getAsteroidToday(today: String): LiveData<List<AsteroidData>>

    @Query("SELECT * FROM asteroid_radar_table WHERE close_approach_date BETWEEN :startOfWeek AND :endOfWeek ORDER BY close_approach_date ASC")
    fun getAsteroidsThisWeek(startOfWeek: String, endOfWeek: String): LiveData<List<AsteroidData>>

    @Query("DELETE FROM asteroid_radar_table")
    suspend fun clear()
}