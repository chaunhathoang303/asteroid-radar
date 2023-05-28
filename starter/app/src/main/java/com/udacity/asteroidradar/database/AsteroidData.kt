package com.udacity.asteroidradar.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "asteroid_radar_table")
data class AsteroidData(
    @PrimaryKey(autoGenerate = true)
    var id: Long?,

    @ColumnInfo(name = "asteroid_id")
    var asteroidId: Long,

    @ColumnInfo(name = "name")
    var codename: String,

    @ColumnInfo(name = "close_approach_date")
    var closeApproachDate: String,

    @ColumnInfo(name = "absolute_magnitude")
    var absoluteMagnitude: Double,

    @ColumnInfo(name = "estimated_diameter")
    var estimatedDiameter: Double,

    @ColumnInfo(name = "relative_velocity")
    var relativeVelocity: Double,

    @ColumnInfo(name = "distance_fromEarth")
    var distanceFromEarth: Double,

    @ColumnInfo(name = "isPotentially_hazardous")
    var isPotentiallyHazardous: Boolean,
)