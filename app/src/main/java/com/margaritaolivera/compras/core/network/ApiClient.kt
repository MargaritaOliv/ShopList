package com.margaritaolivera.compras.core.network

import com.margaritaolivera.compras.features.auth.data.remote.*
import com.margaritaolivera.compras.features.lists.data.remote.*
import com.margaritaolivera.compras.features.lists.domain.model.ListItem
import retrofit2.http.*

interface ApiClient {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @POST("auth/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Unit

    @PUT("auth/profile/{id}")
    suspend fun updateProfile(@Path("id") id: String, @Body request: UpdateProfileRequest): Unit

    @GET("auth/profile/{id}")
    suspend fun getProfile(@Path("id") id: String): UserDto

    @DELETE("auth/profile/{id}")
    suspend fun deleteAccount(@Path("id") id: String)

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

    @GET("api/invitations/{userId}")
    suspend fun getPendingInvitations(@Path("userId") userId: String): List<InvitationDto>

    @POST("api/invitations/respond")
    suspend fun respondToInvitation(@Body request: HandleInvitationRequest): Unit
}