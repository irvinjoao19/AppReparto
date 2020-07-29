package com.calida.dsige.reparto.data.apiServices

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ConexionRetrofit {

//    private val BASE_URL = "http://192.168.0.3:8090/api/"
    private val BASE_URL = "http://www.cobra-peru.com/webApiReparto/api/"
    private var retrofit: Retrofit? = null

    val api: Retrofit
        get() {
            val client = OkHttpClient.Builder()
                    .connectTimeout(2, TimeUnit.MINUTES)
                    .readTimeout(1, TimeUnit.MINUTES)
                    .writeTimeout(1, TimeUnit.MINUTES)
                    .build()
            if (retrofit == null) {
                retrofit = Retrofit.Builder().baseUrl(BASE_URL)
                        .client(client)
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create()).build()
            }
            return retrofit!!
        }
}