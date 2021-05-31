package com.example.weatherforecastmvvm.ui.weather.current

import androidx.lifecycle.ViewModel
import com.example.weatherforecastmvvm.data.repository.ForecastRepository
import com.example.weatherforecastmvvm.internal.UnitSystem
import com.example.weatherforecastmvvm.internal.lazyDeferred
import com.example.weatherforecastmvvm.provider.UnitProvider
import com.example.weatherforecastmvvm.ui.base.WeatherViewModel

class CurrentWeatherViewModel(
    private val forecastRepository: ForecastRepository,
    unitProvider: UnitProvider
) : WeatherViewModel(forecastRepository,unitProvider) {



    val weather by lazyDeferred{
        forecastRepository.getCurrentWeather(super.isMetricUnit)
    }



}
