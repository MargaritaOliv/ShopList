package com.margaritaolivera.compras.features.auth.domain.usecase

import com.margaritaolivera.compras.features.auth.domain.repository.AuthRepository
import javax.inject.Inject

class DeleteAccountUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(id: String): Result<Boolean> {
        return repository.deleteAccount(id)
    }
}