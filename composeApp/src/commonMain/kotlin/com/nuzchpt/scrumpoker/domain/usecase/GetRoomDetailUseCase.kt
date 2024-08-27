package com.nuzchpt.scrumpoker.domain.usecase

import RoomRepository
import com.nuzchpt.scrumpoker.model.room.RoomDetail
import kotlinx.coroutines.flow.Flow

class GetRoomDetailUseCase(private val repository: RoomRepository) :
    UseCase<GetRoomDetailUseCase.Request, RoomDetail>() {

    override suspend fun execute(parameters: Request): Flow<RoomDetail> {
        return repository.getRoomDetail(parameters.roomId)
    }

    data class Request(val roomId: String)
}