package com.udacity.asteroidradar.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface AsteroidDatabaseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg asteroidData: AsteroidData) : LongArray

    @Update
    suspend fun update(vararg asteroidData: AsteroidData)

    @Query("SELECT * FROM asteroid_radar_table")
    fun getAllAsteroid(): LiveData<List<AsteroidData>>

    @Query("DELETE FROM asteroid_radar_table")
    suspend fun clear()
}