package com.cascade.easy.manager

import com.cascade.easy.BuildConfig
import com.cascade.easy.model.network.request.Error
import com.cascade.easy.network.MockServer
import com.cascade.easy.network.base.ListenerCallAdapterFactory
import com.cascade.easy.service.NetworkService
import com.google.gson.GsonBuilder
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

object NetworkManager {
    const val LANGUAGE_HEADER_KEY = "Accept-Language"
    const val ERROR_PATH = "log/error"

    private var requestInterceptor = object : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val modifiedRequest = chain.request()
                .newBuilder()
                .addHeader(LANGUAGE_HEADER_KEY, Locale.getDefault().language)
                .build()

            return chain.proceed(modifiedRequest)
        }
    }

    private var monitorInterceptor = object : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            val response = chain.proceed(request)

            val path = request.url.encodedPath.substring(1)
            if (!response.isSuccessful && path != ERROR_PATH) {
                Error(response.message).let {
                    NETWORK_SERVICE.logError(it).send()
                }
            }

            return response
        }
    }

    val NETWORK_SERVICE: NetworkService by lazy {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(requestInterceptor)
            .addInterceptor(monitorInterceptor)
            .build()

        val gson = GsonBuilder()
            .setLenient()
            .create()

        val url = if (BuildConfig.MOCK_SERVER_ENABLED) {
            MockServer.url()
        } else {
            BuildConfig.SERVER_URL.toHttpUrl()
        }

        Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(ListenerCallAdapterFactory())
            .client(client)
            .build()
            .create(NetworkService::class.java)
    }
}