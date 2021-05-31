package com.example.weatherforecastmvvm.data.network

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.weatherforecastmvvm.data.network.response.CurrentWeatherResponse
import com.example.weatherforecastmvvm.data.network.response.FutureWeatherResponse
import com.example.weatherforecastmvvm.internal.NoConnectivityExceptions


val FORECAST_DAYS_COUNT =7
class WeatherNetworkDataSourceImpl(
    private val weatherApiService: WeatherApiService
) : WeatherNetworkDataSource {

    private val _downloadedCurrentWeather =MutableLiveData<CurrentWeatherResponse>()
    private val _downloadedFutureWeather =MutableLiveData<FutureWeatherResponse>()

    override val downloadedCurrentWeather: LiveData<CurrentWeatherResponse>
        get() = _downloadedCurrentWeather
    override val downloadedFutureWeather: LiveData<FutureWeatherResponse>
        get() =  _downloadedFutureWeather

    override suspend fun fetchCurrentWeather(location: String, languageCode: String) {
        try{
            val fetchedCurrentWeather =weatherApiService
                .getCurrentWeather(location,languageCode)
                .await()
            _downloadedCurrentWeather.postValue(fetchedCurrentWeather)
        }catch (e:NoConnectivityExceptions){
            Log.e("Connectivity","No Internet Connection",e)

        }
    }

    override suspend fun fetchFutureWeather(location: String, languageCode: String) {
        try {
            val fetchedFutureWeather = weatherApiService
                .getFutureWeather(location, FORECAST_DAYS_COUNT,languageCode)
                .await()
            _downloadedFutureWeather.postValue(fetchedFutureWeather)

        }catch (e:NoConnectivityExceptions){
            Log.e("Connectivity","No Internet Connection",e)

        }

    }
}