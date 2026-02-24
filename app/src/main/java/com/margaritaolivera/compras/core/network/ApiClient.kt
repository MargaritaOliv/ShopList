package com.margaritaolivera.compras.core.network

import com.margaritaolivera.compras.features.auth.data.remote.LoginRequest
import com.margaritaolivera.compras.features.auth.data.remote.RegisterRequest
import com.margaritaolivera.compras.features.auth.data.remote.AuthResponse
import com.margaritaolivera.compras.features.lists.data.remote.CreateListRequest
import com.margaritaolivera.compras.features.lists.data.remote.InviteRequest
import com.margaritaolivera.compras.features.lists.domain.model.ListItem
import com.margaritaolivera.compras.features.lists.data.remote.ShoppingListDto
import com.margaritaolivera.compras.features.lists.data.remote.UpdateItemRequest
import com.margaritaolivera.compras.features.lists.data.remote.UpdateListRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
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

    @PUT("api/lists/{id}")
    suspend fun updateList(@Path("id") listId: String, @Body request: UpdateListRequest)

    @DELETE("api/lists/{id}")
    suspend fun deleteList(@Path("id") listId: String)

    @PUT("api/items/{id}")
    suspend fun updateItem(@Path("id") itemId: String, @Body request: UpdateItemRequest)

    @GET("api/lists/{id}/items")
    suspend fun getItems(@Path("id") listId: String): List<ListItem>

    @POST("api/lists/{id}/invite")
    suspend fun inviteUser(@Path("id") listId: String, @Body request: InviteRequest)
}