package com.example.weatherforecastmvvm.ui.weather.current

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer

import com.example.weatherforecastmvvm.R
import com.example.weatherforecastmvvm.data.network.ConnectivityInterceptorImpl

import com.example.weatherforecastmvvm.data.network.WeatherApiService
import com.example.weatherforecastmvvm.data.network.WeatherNetworkDataSourceImpl
import com.example.weatherforecastmvvm.internal.glide.GlideApp
import com.example.weatherforecastmvvm.ui.base.ScopedFragment
import kotlinx.android.synthetic.main.current_weather_fragment.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class CurrentWeatherFragment : ScopedFragment(), KodeinAware {

    override val kodein by closestKodein()
    private val viewModelFactory: CurrentWeatherViewModelFactory by instance()


    private lateinit var viewModel: CurrentWeatherViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.current_weather_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel =
            ViewModelProviders.of(this, viewModelFactory).get(CurrentWeatherViewModel::class.java)
        // TODO: Use the ViewModel

        bindUI()

//        val apiService = WeatherApiService(ConnectivityInterceptorImpl(this.context!!))
//        val weatherNetworkDataSource =WeatherNetworkDataSourceImpl(apiService)
//
//        weatherNetworkDataSource.downloadedCurrentWeather.observe(this, Observer{
//            textView.text = it.toString()
//        })
//
//
//
//        GlobalScope.launch(Dispatchers.Main) {
////            val currentWeatherResponse = apiService.getCurrentWeather("Accra").await()
////            textView.text = currentWeatherResponse.toString()
//            weatherNetworkDataSource.fetchCurrentWeather("Accra","en")
//        }
    }

    private fun bindUI() = launch {
        val currentWeather = viewModel.weather.await()

        val weatherLocation = viewModel.weatherLocation.await()

        weatherLocation.observe(
            this@CurrentWeatherFragment, Observer {location->
                if (location== null )return@Observer
                updateLocation(location.name)

            }

        )


        currentWeather.observe(this@CurrentWeatherFragment, Observer {

            if (it == null) return@Observer

            group_loading.visibility = View.GONE

            updateDateToday()
            updatePrecipitation(it.precipitationVolume)
            updateTemperatures(it.temperature,it.feelsLikeTemperature)
            updateVisibility(it.visibilityDistance)
            updateWind(it.windDirection,it.windSpeed)

            GlideApp.with(this@CurrentWeatherFragment)
                .load("${it.conditionIconUrl}")
                .into(imageView_condition_icon)




        })
    }

    private fun chooseLocalizedUnitAbbreviation(metric: String, imperial: String): String {
        return if (viewModel.isMetric) metric else imperial

    }

    private fun updateLocation(location: String) {
        (activity as AppCompatActivity)?.supportActionBar?.title = location


    }

    private fun updateDateToday() {
        (activity as AppCompatActivity)?.supportActionBar?.subtitle = "Today"
    }


    private fun updateTemperatures(temperature: Double, feelsLike: Double) {
        val unitAbbreviation = chooseLocalizedUnitAbbreviation("°C", "°F")
        textView_temperature.text = "$temperature $unitAbbreviation"
        textView_feels_like_temperature.text = "$feelsLike $unitAbbreviation"

    }

    private fun updatePrecipitation(precipitationVolume: Double) {
        val unitAbbreviation = chooseLocalizedUnitAbbreviation("mm", "in")
        textView_precipitation.text = "Preciptiation: $precipitationVolume $unitAbbreviation"

    }

    private fun updateWind(windDirection: String, windSpeed: Double) {
        val unitAbbreviation = chooseLocalizedUnitAbbreviation("kph", "mph")
        textView_wind.text = "Wind: $windDirection, $windSpeed $unitAbbreviation"
    }

    private fun updateVisibility(visibilityDistance: Double) {
        val unitAbbreviation = chooseLocalizedUnitAbbreviation("km", "mi.")
        textView_visibility.text = "Visibility: $visibilityDistance $unitAbbreviation"
    }

}
