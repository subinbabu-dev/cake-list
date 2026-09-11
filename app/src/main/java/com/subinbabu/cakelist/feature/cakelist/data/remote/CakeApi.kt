package com.subinbabu.cakelist.feature.cakelist.data.remote

import retrofit2.http.GET

interface CakeApi {

    @GET("Waracle/mobile-coding-test-api/refs/heads/main/cakes")
    suspend fun getCakes(): List<CakeDto>
}