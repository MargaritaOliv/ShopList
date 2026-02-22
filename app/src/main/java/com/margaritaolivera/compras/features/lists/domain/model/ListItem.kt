package com.margaritaolivera.compras.features.lists.domain.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class ListItem(
    val id: String = "",
    @SerialName("list_id") val listId: String = "",
    val title: String,
    val quantity: String = "",
    val note: String = "",
    val status: String = "pending",
    val version: Int = 1
) {
    val isCompleted: Boolean get() = status == "completed"
}