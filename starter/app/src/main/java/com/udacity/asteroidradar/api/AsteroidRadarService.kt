package com.udacity.asteroidradar.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.asteroidradar.Constants.BASE_URL
import com.udacity.asteroidradar.PictureOfDay
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .baseUrl(BASE_URL)
    .build()

private val nasaRetrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface AsteroidRadarApiService {
    @GET("neo/rest/v1/feed?api_key=8udechMYeqV8WCwwnlKK9TGQjMICB7FaapveeDOp")
    fun getProperties():
            Call<String>
}

interface NaSaApiService {
    @GET("planetary/apod?api_key=8udechMYeqV8WCwwnlKK9TGQjMICB7FaapveeDOp")
    fun getNasaProperties():
            Call<PictureOfDay>
}

object AsteroidRadarApi {
    val retrofitService: AsteroidRadarApiService by lazy {
        retrofit.create(AsteroidRadarApiService::class.java)
    }
}

object NasaApi {
    val nasaRetrofitService: NaSaApiService by lazy {
        nasaRetrofit.create(NaSaApiService::class.java)
    }
}