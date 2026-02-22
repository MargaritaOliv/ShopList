package com.margaritaolivera.compras.features.lists.data.repository

import com.margaritaolivera.compras.core.network.ApiClient
import com.margaritaolivera.compras.features.lists.data.remote.CreateListRequest
import com.margaritaolivera.compras.features.lists.domain.model.ShoppingList
import com.margaritaolivera.compras.features.lists.domain.repository.ListRepository
import javax.inject.Inject

class   ListRepositoryImpl @Inject constructor(
    private val api: ApiClient
) : ListRepository {

    override suspend fun getUserLists(userId: String): Result<List<ShoppingList>> {
        return try {
            val dtos = api.getLists(userId)

            val lists = dtos.map { dto ->
                ShoppingList(
                    id = dto.id,
                    name = dto.name,
                    ownerId = dto.ownerId
                )
            }
            Result.success(lists)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(Exception("Error al cargar tus listas"))
        }
    }

    override suspend fun createList(name: String, ownerId: String): Result<ShoppingList> {
        return try {
            val response = api.createList(CreateListRequest(name, ownerId))

            val newList = ShoppingList(
                id = response.id,
                name = response.name,
                ownerId = response.ownerId
            )
            Result.success(newList)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(Exception("Error al crear la lista"))
        }
    }
}