package com.nuzchpt.scrumpoker.domain.usecase

import RoomRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class GetLocalRoomIdUseCase(private val repository: RoomRepository) : UseCase<Unit, String?>() {
    override suspend fun execute(parameters: Unit): Flow<String?> {
        return flowOf(repository.getLocalRoomId())
    }
}