package com.margaritaolivera.compras.core.network

import com.margaritaolivera.compras.features.auth.data.remote.LoginRequest
import com.margaritaolivera.compras.features.auth.data.remote.RegisterRequest
import com.margaritaolivera.compras.features.auth.data.remote.AuthResponse
import com.margaritaolivera.compras.features.lists.data.remote.CreateListRequest
import com.margaritaolivera.compras.features.lists.data.remote.ShoppingListDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiClient {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse


    @GET("api/lists")
    suspend fun getLists(@Query("userId") userId: String): List<ShoppingListDto>

    @POST("api/lists")
    suspend fun createList(@Body request: CreateListRequest): ShoppingListDto
}