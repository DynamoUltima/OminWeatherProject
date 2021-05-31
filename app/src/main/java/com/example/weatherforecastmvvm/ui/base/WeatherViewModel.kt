package com.example.weatherforecastmvvm.ui.base

import androidx.lifecycle.ViewModel
import com.example.weatherforecastmvvm.data.repository.ForecastRepository
import com.example.weatherforecastmvvm.internal.UnitSystem
import com.example.weatherforecastmvvm.internal.lazyDeferred
import com.example.weatherforecastmvvm.provider.UnitProvider

abstract class WeatherViewModel(
    private val forecastRepository: ForecastRepository,
    unitProvider: UnitProvider
) : ViewModel() {
    private val unitSystem =unitProvider.getUnitSystem()

    val isMetricUnit: Boolean
        get() = unitSystem == UnitSystem.METRIC



    val weatherLocation by lazyDeferred{
        forecastRepository.getWeatherLocation()
    }

}