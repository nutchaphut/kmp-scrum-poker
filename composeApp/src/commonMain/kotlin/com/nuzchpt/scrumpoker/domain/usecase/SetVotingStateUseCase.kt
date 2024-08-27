package com.nuzchpt.scrumpoker.domain.usecase

import RoomRepository
import com.nuzchpt.scrumpoker.model.room.RoomState
import kotlinx.coroutines.flow.Flow

class SetVotingStateUseCase(private val repository: RoomRepository) :
    UseCase<SetVotingStateUseCase.Request, Unit>() {
    data class Request(val roomId: String, val roomState: RoomState)

    override suspend fun execute(parameters: Request): Flow<Unit> {
        return repository.setVotingState(
            roomId = parameters.roomId,
            state = parameters.roomState.name
        )
    }
}