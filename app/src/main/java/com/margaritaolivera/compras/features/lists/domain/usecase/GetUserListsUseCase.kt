package com.margaritaolivera.compras.features.lists.domain.usecase

import com.margaritaolivera.compras.features.lists.domain.model.ShoppingList
import com.margaritaolivera.compras.features.lists.domain.repository.ListRepository
import javax.inject.Inject

class GetUserListsUseCase @Inject constructor(
    private val repository: ListRepository
) {
    suspend operator fun invoke(userId: String): Result<List<ShoppingList>> {
        return repository.getUserLists(userId)
    }
}