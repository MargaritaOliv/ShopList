package com.margaritaolivera.compras.features.lists.data.repository

import com.margaritaolivera.compras.core.network.ApiClient
import com.margaritaolivera.compras.features.lists.data.remote.CreateListRequest
import com.margaritaolivera.compras.features.lists.data.remote.UpdateItemRequest
import com.margaritaolivera.compras.features.lists.data.remote.UpdateListRequest
import com.margaritaolivera.compras.features.lists.domain.model.ShoppingList
import com.margaritaolivera.compras.features.lists.domain.repository.ListRepository
import javax.inject.Inject

class ListRepositoryImpl @Inject constructor(
    private val api: ApiClient
) : ListRepository {

    override suspend fun getUserLists(userId: String): Result<List<ShoppingList>> {
        return try {
            val dtos = api.getLists(userId)
            val lists = dtos.map { dto ->
                ShoppingList(
                    id = dto.id,
                    name = dto.name,
                    ownerId = dto.ownerId,
                    totalItems = dto.totalItems,
                    completedItems = dto.completedItems,
                    updatedAt = dto.updatedAt
                )
            }
            Result.success(lists)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createList(name: String, ownerId: String): Result<ShoppingList> {
        return try {
            val response = api.createList(CreateListRequest(name, ownerId))
            val newList = ShoppingList(
                id = response.id,
                name = response.name,
                ownerId = response.ownerId,
                totalItems = 0,
                completedItems = 0,
                updatedAt = null
            )
            Result.success(newList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateList(listId: String, name: String): Result<Boolean> {
        return try {
            api.updateList(listId, UpdateListRequest(name))
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteList(listId: String): Result<Boolean> {
        return try {
            api.deleteList(listId)
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateItem(itemId: String, title: String, quantity: String, note: String): Result<Boolean> {
        return try {
            api.updateItem(itemId, UpdateItemRequest(title, quantity, note))
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}