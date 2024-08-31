package com.nuzchpt.scrumpoker.domain.usecase

import RoomRepository
import com.nuzchpt.scrumpoker.model.room.RoomDetail
import kotlinx.coroutines.flow.Flow

class CreateRoomUseCase(private val repository: RoomRepository) :
    UseCase<CreateRoomUseCase.Request, RoomDetail>() {
    data class Request(val roomName: String)

    override suspend fun execute(parameters: Request): Flow<RoomDetail> {
        return repository.createRoom(roomName = parameters.roomName)
    }

}