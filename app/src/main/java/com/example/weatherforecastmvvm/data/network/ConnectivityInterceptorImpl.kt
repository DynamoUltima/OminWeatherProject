package com.example.weatherforecastmvvm.data.network

import android.content.Context
import android.net.ConnectivityManager
import androidx.core.content.getSystemService
import com.example.weatherforecastmvvm.internal.NoConnectivityExceptions
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class ConnectivityInterceptorImpl(context: Context) : ConnectivityInterceptor {

    private val appContext = context.applicationContext

    override fun intercept(chain: Interceptor.Chain): Response {
      if(!isfunOnline())
          throw NoConnectivityExceptions()
        return chain.proceed(chain.request())

    }

    private fun isfunOnline():Boolean {
        val connectivityManager = appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo =connectivityManager.activeNetworkInfo
        return  networkInfo != null && networkInfo.isConnected


    }
}