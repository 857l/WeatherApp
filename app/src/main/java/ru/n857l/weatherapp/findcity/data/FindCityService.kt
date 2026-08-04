package ru.n857l.weatherapp.findcity.data

import retrofit2.http.GET
import retrofit2.http.Query

interface FindCityService {

    @GET("geo/1.0/direct")
    suspend fun findCity(
        @Query("q") query: String,
        @Query("limit") limit: Int = 20,
        @Query("appid") apiKey: String
    ): List<FoundCityCloud>
}