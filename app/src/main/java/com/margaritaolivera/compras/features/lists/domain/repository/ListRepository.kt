package com.margaritaolivera.compras.features.lists.domain.repository

import com.margaritaolivera.compras.features.lists.domain.model.ShoppingList

interface ListRepository {
    suspend fun getUserLists(userId: String): Result<List<ShoppingList>>
    suspend fun createList(name: String, ownerId: String): Result<ShoppingList>
}