package com.example.weatherforecastmvvm.data.db.entity


import com.google.gson.annotations.SerializedName

data class Condition(
    val code: Int,
    val icon: String,
    val text: String
)