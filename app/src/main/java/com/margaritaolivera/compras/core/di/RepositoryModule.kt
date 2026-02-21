package com.margaritaolivera.compras.core.di

import com.margaritaolivera.compras.features.auth.data.repository.AuthRepositoryImpl
import com.margaritaolivera.compras.features.auth.domain.repository.AuthRepository
import com.margaritaolivera.compras.features.lists.data.repository.ListRepositoryImpl
import com.margaritaolivera.compras.features.lists.domain.repository.ListRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindListRepository(
        listRepositoryImpl: ListRepositoryImpl
    ): ListRepository
}