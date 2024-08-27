package com.nuzchpt.scrumpoker.domain.usecase

import com.nuzchpt.scrumpoker.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class SaveUserInfoUseCase(private val repository: UserRepository) : UseCase<SaveUserInfoUseCase.Request, Unit>() {

    data class Request(val userName: String)

    override suspend fun execute(parameters: Request): Flow<Unit> {
        return repository.createUser(parameters.userName)
    }
}