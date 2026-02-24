package com.margaritaolivera.compras.features.lists.domain.repository

import com.margaritaolivera.compras.features.lists.domain.model.ShoppingList

interface ListRepository {
    suspend fun getUserLists(userId: String): Result<List<ShoppingList>>
    suspend fun createList(name: String, ownerId: String): Result<ShoppingList>
    suspend fun updateList(listId: String, name: String): Result<Boolean>
    suspend fun deleteList(listId: String): Result<Boolean>
    suspend fun updateItem(itemId: String, title: String, quantity: String, note: String): Result<Boolean>
}