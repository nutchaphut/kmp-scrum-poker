package com.nuzchpt.scrumpoker.domain.repository

import com.nuzchpt.scrumpoker.data.local.LocalStorageDatasource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface UserRepository {
    fun createUser(name: String): Flow<Unit>
    fun getUserName(): Flow<String?>
}

class UserRepositoryImpl(private val localStorageDatasource: LocalStorageDatasource) : UserRepository {
    override fun createUser(name: String): Flow<Unit> = flow {
        emit(localStorageDatasource.saveUserName(userName = name))
    }

    override fun getUserName(): Flow<String?> = flow {
        emit(localStorageDatasource.getUserName())
    }
}