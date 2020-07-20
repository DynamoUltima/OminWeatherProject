package com.example.weatherforecastmvvm.data.network

import androidx.lifecycle.LiveData
import com.example.weatherforecastmvvm.data.network.response.CurrentWeatherResponse

interface WeatherNetworkDataSource {
    val downloadedCurrentWeather: LiveData<CurrentWeatherResponse>

    suspend fun fetchCurrentWeather(location: String, languageCode: String)
}