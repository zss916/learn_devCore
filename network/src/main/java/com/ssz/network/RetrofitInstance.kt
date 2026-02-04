package com.ssz.network

import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstance {
    private const val WAN_ANDROID_BASE_URL = "https://www.wanandroid.com/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    @Volatile
    private var cookieJar: PersistentCookieJar? = null
    @Volatile
    private var wanAndroidOkHttpClient: OkHttpClient? = null

    /**
     * 初始化CookieJar，需要在Application或Activity中调用
     */
    fun initCookieJar(context: Context) {
        if (cookieJar == null) {
            synchronized(this) {
                if (cookieJar == null) {
                    cookieJar = PersistentCookieJar(context)
                    // 如果已经创建了 OkHttpClient，需要重新创建
                    wanAndroidOkHttpClient = null
                }
            }
        }
    }


    private fun getWanAndroidOkHttpClient(): OkHttpClient {
        if (wanAndroidOkHttpClient == null) {
            synchronized(this) {
                if (wanAndroidOkHttpClient == null) {
                    wanAndroidOkHttpClient = OkHttpClient.Builder()
                        .addInterceptor(loggingInterceptor)
                        .addInterceptor(AuthInterceptor())
                        .apply {
                            cookieJar?.let { cookieJar(it) }
                        }
                        .connectTimeout(30, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .writeTimeout(30, TimeUnit.SECONDS)
                        .build()
                }
            }
        }
        return wanAndroidOkHttpClient!!
    }


    private val wanAndroidRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(WAN_ANDROID_BASE_URL)
            .client(getWanAndroidOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val wanAndroidApiService: ApiService by lazy {
        wanAndroidRetrofit.create(ApiService::class.java)
    }
}

