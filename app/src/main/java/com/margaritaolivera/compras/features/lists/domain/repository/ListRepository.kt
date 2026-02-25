package com.margaritaolivera.compras.features.lists.domain.repository

import com.margaritaolivera.compras.features.lists.domain.model.ShoppingList
import com.margaritaolivera.compras.features.lists.domain.model.Invitation

interface ListRepository {
    suspend fun getUserLists(userId: String): Result<List<ShoppingList>>
    suspend fun createList(name: String, ownerId: String): Result<ShoppingList>
    suspend fun updateList(listId: String, name: String): Result<Boolean>
    suspend fun deleteList(listId: String): Result<Boolean>
    suspend fun updateItem(itemId: String, title: String, quantity: String, note: String): Result<Boolean>

    suspend fun getPendingInvitations(userId: String): Result<List<Invitation>>
    suspend fun respondToInvitation(listId: String, userId: String, accept: Boolean): Result<Boolean>
}