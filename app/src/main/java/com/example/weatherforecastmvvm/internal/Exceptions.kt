package com.example.weatherforecastmvvm.internal

import java.io.IOException
import java.lang.Exception

class NoConnectivityExceptions:IOException()
class LocationPermissionNotGrantedException:Exception()