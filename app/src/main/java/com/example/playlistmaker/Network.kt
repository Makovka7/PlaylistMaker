package com.example.playlistmaker

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface ItunesApi {
    @GET("search?entity=song")
    fun search(@Query("term") text: String): Call<SearchResponse>
}

object Api {
    val service: ItunesApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ItunesApi::class.java)
    }
}
