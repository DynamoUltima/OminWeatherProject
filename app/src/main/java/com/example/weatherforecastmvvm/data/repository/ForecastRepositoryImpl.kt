package com.example.weatherforecastmvvm.data.repository


import android.util.Log
import androidx.lifecycle.LiveData
import com.example.weatherforecastmvvm.data.db.CurrrentWeatherDao
import com.example.weatherforecastmvvm.data.db.FutureWeatherDao
import com.example.weatherforecastmvvm.data.db.WeatherLocationDao
import com.example.weatherforecastmvvm.data.db.entity.WeatherLocation
import com.example.weatherforecastmvvm.data.db.unitlocalized.current.UnitSpecificCurrentWeatherEntry
import com.example.weatherforecastmvvm.data.db.unitlocalized.future.UnitSpecificSimpleFutureWeatherEntry
import com.example.weatherforecastmvvm.data.network.FORECAST_DAYS_COUNT
import com.example.weatherforecastmvvm.data.network.WeatherNetworkDataSource
import com.example.weatherforecastmvvm.data.network.response.CurrentWeatherResponse
import com.example.weatherforecastmvvm.data.network.response.FutureWeatherResponse
import com.example.weatherforecastmvvm.provider.LocationProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZonedDateTime
import java.util.*

class ForecastRepositoryImpl(
    private val currentWeatherDao: CurrrentWeatherDao,
    private val futureWeatherDao: FutureWeatherDao,
    private val weatherLocationDao: WeatherLocationDao,
    private val weatherNetworkDataSource: WeatherNetworkDataSource,
    private val locationProvider: LocationProvider
) : ForecastRepository {

    init {
        weatherNetworkDataSource.apply {

            downloadedCurrentWeather.observeForever { newCurrentWeather ->
                persistFetchedCurrentWeather(newCurrentWeather)
        }

            downloadedFutureWeather.observeForever { newFutureWeather ->
                persistFetchedFutureWeather(newFutureWeather)
            }

        }
    }

    private fun persistFetchedFutureWeather(fetchedWeather: FutureWeatherResponse) {

        fun deleteOldForecastData(){
            val today = LocalDate.now()
            futureWeatherDao.deleteOldEntries(today)
        }

        GlobalScope.launch(Dispatchers.IO) {
            val futureWeatherList =fetchedWeather.futureWeatherEntries.entries
            futureWeatherDao.insert(futureWeatherList)
            weatherLocationDao.upsert(fetchedWeather.location)

        }

    }


    override suspend fun getCurrentWeather(metric: Boolean): LiveData<out UnitSpecificCurrentWeatherEntry> {
        //out means we dont only accept class that directly produce UnitSpecific entry but also classes that implement UnitSpecific entry
        return withContext(Dispatchers.IO) {
            initWeatherData()
            return@withContext if (metric) currentWeatherDao.getWeatherMetric()
            else currentWeatherDao.getWeatherImperial()
        }
    }

    override suspend fun getFutureWeatherList(
        startDate: LocalDate,
        metric: Boolean
    ): LiveData<out List<UnitSpecificSimpleFutureWeatherEntry>> {
       return withContext(Dispatchers.IO){
           initWeatherData()
           return@withContext if (metric) futureWeatherDao.getSimpleWeatherForecastsMetric(startDate)
           else futureWeatherDao.getSimpleWeatherForecastsImperial(startDate)
       }
    }

    override suspend fun getWeatherLocation(): LiveData<WeatherLocation> {
        return withContext(Dispatchers.IO) {
            return@withContext weatherLocationDao.getLocation()
        }
    }


    private fun persistFetchedCurrentWeather(fetchedWeather: CurrentWeatherResponse) {
        GlobalScope.launch(Dispatchers.IO) {
            currentWeatherDao.upsert(fetchedWeather.currentWeatherEntry)
            weatherLocationDao.upsert(fetchedWeather.location)
        }

    }

    private suspend fun initWeatherData() {
        val lastWeatherLocation = weatherLocationDao.getLocationNonLive()

        if (lastWeatherLocation == null || locationProvider.hasLocationChanged(lastWeatherLocation)) {
            fetchCurrentWeather()
            fetchFutureWeather()
            return
        }


        if (isFetchCurrentNeeded(lastWeatherLocation.zonedDateTime)) {
            fetchCurrentWeather()
        }

        if (isFetchFutureNeeded()){
            fetchFutureWeather()
        }

//        fetchFutureWeather()

    }

    private suspend fun  fetchFutureWeather() {
        weatherNetworkDataSource.fetchFutureWeather(
            locationProvider.getPreferredLocationString(),
            Locale.getDefault().language
        )

    }


    private suspend fun fetchCurrentWeather() {
        weatherNetworkDataSource.fetchCurrentWeather(
            locationProvider.getPreferredLocationString(),
            Locale.getDefault().language
        )
    }

    private fun isFetchFutureNeeded():Boolean{
        val today =LocalDate.now()
//        print("Printing locale date")
//        Log.e("Printing locale date", today.toString())

        val futureWeatherCount =futureWeatherDao.countFutureWeather(today)
        return futureWeatherCount < FORECAST_DAYS_COUNT

    }

    private fun isFetchCurrentNeeded(lastFetchTime: ZonedDateTime): Boolean {
        val thirtyMinutesAgo = ZonedDateTime.now().minusMinutes(30)
        return lastFetchTime.isBefore(thirtyMinutesAgo)

    }
}