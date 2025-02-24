package com.sgomez.navegaciondetalle.data.repositories

import com.sgomez.navegaciondetalle.data.repositories.model.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RemoteService {
    @GET("digimon/")
    suspend fun getDigimonAll(): List<Response>

    @GET("digimon/name/{name}")
    suspend fun getDigimon(@Path("name") name: String): List<Response>
}