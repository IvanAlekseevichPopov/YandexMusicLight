package com.example.yandexmusic.core.network

import android.util.Log
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.UUID
import java.util.concurrent.TimeUnit

object ApiClient {

    private const val TAG = "YandexMusicAPI"

    private var oauthToken: String = ""

    fun setToken(token: String) {
        oauthToken = token
        Log.d(TAG, "Token set, length: ${token.length}")
    }

    private val musicHeadersInterceptor = Interceptor { chain ->
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "OAuth $oauthToken")
            .addHeader("X-Yandex-Music-Client", "YandexMusicAndroid/24023621")
            .addHeader("X-Request-Id", UUID.randomUUID().toString())
            .build()
        chain.proceed(request)
    }

    private val loggingInterceptor = HttpLoggingInterceptor { message ->
        Log.d(TAG, message)
    }.apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(musicHeadersInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.music.yandex.net/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: YandexMusicApi = retrofit.create(YandexMusicApi::class.java)
}
