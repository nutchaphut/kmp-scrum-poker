package com.nuzchpt.scrumpoker.domain.usecase

import RoomRepository
import kotlinx.coroutines.flow.Flow

class ClearRoomParticipatesPointUseCase(private val repository: RoomRepository) :
    UseCase<ClearRoomParticipatesPointUseCase.Request, Unit>() {
    data class Request(val roomId: String)

    override suspend fun execute(parameters: Request): Flow<Unit> {
        return repository.clearParticipantPoints(roomId = parameters.roomId)
    }
}