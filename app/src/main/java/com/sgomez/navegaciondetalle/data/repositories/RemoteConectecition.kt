package com.sgomez.navegaciondetalle.data.repositories

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

object RemoteConectecition {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://digimon-api.vercel.app/api/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service:RemoteService = retrofit.create()
}