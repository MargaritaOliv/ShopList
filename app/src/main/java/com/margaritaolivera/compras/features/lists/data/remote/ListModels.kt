package com.margaritaolivera.compras.features.lists.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateListRequest(
    val name: String,
    @SerialName("owner_id") val ownerId: String
)

@Serializable
data class UpdateListRequest(
    val name: String
)

@Serializable
data class UpdateItemRequest(
    val title: String,
    val quantity: String,
    val note: String
)

@Serializable
data class ShoppingListDto(
    val id: String,
    val name: String,
    @SerialName("owner_id") val ownerId: String,
    @SerialName("total_items") val totalItems: Int = 0,
    @SerialName("completed_items") val completedItems: Int = 0,
    @SerialName("updated_at") val updatedAt: String? = null
)