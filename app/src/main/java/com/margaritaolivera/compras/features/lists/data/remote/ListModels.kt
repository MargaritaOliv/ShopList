package com.margaritaolivera.compras.features.lists.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateListRequest(
    val name: String,
    @SerialName("owner_id") val ownerId: String
)

@Serializable
data class ShoppingListDto(
    val id: String,
    val name: String,
    @SerialName("owner_id") val ownerId: String
)