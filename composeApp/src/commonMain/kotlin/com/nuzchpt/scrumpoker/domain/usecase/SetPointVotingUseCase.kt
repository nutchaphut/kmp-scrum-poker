package com.nuzchpt.scrumpoker.domain.usecase

import RoomRepository
import kotlinx.coroutines.flow.Flow

class SetPointVotingUseCase(private val repository: RoomRepository) :
    UseCase<SetPointVotingUseCase.Request, Unit>() {
    data class Request(val roomId: String, val point:String?)

    override suspend fun execute(parameters: Request): Flow<Unit> {
        return repository.setPointVoting(
            roomId = parameters.roomId,
            point = parameters.point
        )
    }
}