package com.nuzchpt.scrumpoker.domain.usecase

import com.nuzchpt.scrumpoker.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class GetUserInfoUseCase(private val repository: UserRepository) : UseCase<Unit, String?>() {

    override suspend fun execute(parameters: Unit): Flow<String?> {
        return repository.getUserName()
    }
}