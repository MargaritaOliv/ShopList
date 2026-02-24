package com.margaritaolivera.compras.features.lists.domain.model

data class ShoppingList(
    val id: String,
    val name: String,
    val ownerId: String,
    val totalItems: Int,
    val completedItems: Int,
    val updatedAt: String?
) {
    val progress: Float
        get() = if (totalItems > 0) completedItems.toFloat() / totalItems else 0f
}