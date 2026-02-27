package com.margaritaolivera.compras.features.lists.data.repository

import com.margaritaolivera.compras.core.network.ApiClient
import com.margaritaolivera.compras.features.lists.data.remote.*
import com.margaritaolivera.compras.features.lists.domain.model.ShoppingList
import com.margaritaolivera.compras.features.lists.domain.model.Invitation
import com.margaritaolivera.compras.features.lists.domain.repository.ListRepository
import javax.inject.Inject

class ListRepositoryImpl @Inject constructor(
    private val api: ApiClient
) : ListRepository {

    override suspend fun getUserLists(userId: String): Result<List<ShoppingList>> {
        return try {
            val dtos = api.getLists(userId)
            val lists = dtos.map { dto ->
                ShoppingList(dto.id, dto.name, dto.ownerId, dto.totalItems, dto.completedItems, dto.updatedAt)
            }
            Result.success(lists)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createList(name: String, ownerId: String): Result<ShoppingList> {
        return try {
            val response = api.createList(CreateListRequest(name, ownerId))
            Result.success(ShoppingList(response.id, response.name, response.ownerId, 0, 0, null))
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

    override suspend fun getPendingInvitations(userId: String): Result<List<Invitation>> {
        return try {
            val dtos = api.getPendingInvitations(userId)
            Result.success(dtos.map {
                Invitation(
                    id = it.id,
                    listName = it.listName,
                    invitedByName = it.ownerName
                )
            })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun respondToInvitation(listId: String, userId: String, accept: Boolean): Result<Boolean> {
        return try {
            api.respondToInvitation(
                HandleInvitationRequest(
                    listId = listId,
                    userId = userId,
                    accept = accept
                )
            )
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}