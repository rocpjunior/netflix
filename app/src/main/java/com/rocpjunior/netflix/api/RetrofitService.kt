package com.rocpjunior.netflix.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitService {
    const val URL_BASE = "https://api.themoviedb.org/3/"
    const val URL_BASE_IMAGENS = "https://image.tmdb.org/t/p/"
    const val API_KEY = "2ed5afd5d50ffe27facb16fe5a924ceb"

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .writeTimeout(10, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .addInterceptor(AuthInterceptor())
        .build()

    val retrofit = Retrofit.Builder()
        .baseUrl( URL_BASE )
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .build()

    val filmeAPI = retrofit.create( FilmeAPI::class.java )
}