package com.margaritaolivera.compras.features.lists.data.remote

import com.margaritaolivera.compras.features.lists.domain.model.ListItem
import kotlinx.serialization.Serializable

@Serializable
data class SocketResponse(
    val type: String,
    val item: ListItem? = null
)