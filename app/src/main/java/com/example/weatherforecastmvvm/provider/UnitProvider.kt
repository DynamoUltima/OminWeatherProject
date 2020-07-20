package com.example.weatherforecastmvvm.provider

import com.example.weatherforecastmvvm.internal.UnitSystem

interface UnitProvider {
    fun getUnitSystem():UnitSystem
}