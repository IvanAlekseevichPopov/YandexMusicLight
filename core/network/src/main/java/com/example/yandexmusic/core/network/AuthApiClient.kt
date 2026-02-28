package com.example.yandexmusic.core.network

import android.util.Log
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

data class SessionCookies(
    val sessionId: String,
    val sessionId2: String,
    val yandexLogin: String
)

object AuthApiClient {

    private const val TAG = "AuthApiClient"

    private val cookieStore = mutableMapOf<String, MutableList<Cookie>>()

    private val cookieJar = object : CookieJar {
        override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
            val host = url.host
            val hostCookies = cookieStore.getOrPut(host) { mutableListOf() }
            cookies.forEach { newCookie ->
                hostCookies.removeAll { it.name == newCookie.name }
                hostCookies.add(newCookie)
            }
            Log.d(TAG, "Saved ${cookies.size} cookies for $host: ${cookies.map { it.name }}")
        }

        override fun loadForRequest(url: HttpUrl): List<Cookie> {
            return cookieStore[url.host].orEmpty()
        }
    }

    private val authHeadersInterceptor = Interceptor { chain ->
        val request = chain.request().newBuilder()
            .addHeader("User-Agent", "com.yandex.mobile.auth.sdk/7.25.0")
            .addHeader("X-Requested-With", "XMLHttpRequest")
            .build()
        chain.proceed(request)
    }

    private val loggingInterceptor = HttpLoggingInterceptor { message ->
        Log.d(TAG, message)
    }.apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .cookieJar(cookieJar)
        .addInterceptor(authHeadersInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .followRedirects(false)
        .followSslRedirects(false)
        .build()

    private val passportRetrofit = Retrofit.Builder()
        .baseUrl("https://passport.yandex.ru/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val tokenRetrofit = Retrofit.Builder()
        .baseUrl("https://passport.yandex.ru/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val passportApi: YandexPassportApi = passportRetrofit.create(YandexPassportApi::class.java)
    val tokenApi: YandexTokenApi = tokenRetrofit.create(YandexTokenApi::class.java)

    fun getSessionCookies(): SessionCookies? {
        val passportCookies = cookieStore["passport.yandex.ru"].orEmpty()
        val sessionId = passportCookies.find { it.name == "Session_id" }?.value
        val sessionId2 = passportCookies.find { it.name == "sessionid2" }?.value ?: ""
        val yandexLogin = passportCookies.find { it.name == "yandex_login" }?.value ?: ""

        Log.d(TAG, "getSessionCookies: Session_id=${sessionId != null}, sessionid2=${sessionId2.isNotEmpty()}, yandex_login=$yandexLogin")

        return if (sessionId != null) {
            SessionCookies(sessionId, sessionId2, yandexLogin)
        } else null
    }

    fun clearCookies() {
        cookieStore.clear()
        Log.d(TAG, "Cookies cleared")
    }
}
