package com.nuzchpt.scrumpoker.domain.usecase

import RoomRepository
import kotlinx.coroutines.flow.Flow

class JoinRoomUseCase(private val repository: RoomRepository) : UseCase<JoinRoomUseCase.Request, Unit>() {

    data class Request(val roomId: String)

    override suspend fun execute(parameters: Request): Flow<Unit> {
        return repository.joinRoom(roomId = parameters.roomId)
    }
}