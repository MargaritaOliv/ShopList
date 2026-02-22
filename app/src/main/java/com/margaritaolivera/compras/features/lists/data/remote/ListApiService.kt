package com.margaritaolivera.compras.features.lists.data.remote

import com.margaritaolivera.compras.features.lists.domain.model.ListItem
import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

@Serializable
data class InviteRequest(val email: String)

interface ListApiService {
    @GET("api/lists/{id}/items")
    suspend fun getItems(@Path("id") listId: String): List<ListItem>

    @POST("api/lists/{id}/invite")
    suspend fun inviteUser(
        @Path("id") listId: String,
        @Body request: InviteRequest
    )
}