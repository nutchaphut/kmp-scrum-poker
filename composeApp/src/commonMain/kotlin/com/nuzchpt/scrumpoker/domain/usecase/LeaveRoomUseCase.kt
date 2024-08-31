package com.nuzchpt.scrumpoker.domain.usecase

import RoomRepository
import com.nuzchpt.scrumpoker.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class LeaveRoomUseCase(private val repository: RoomRepository) : UseCase<Unit, Unit>() {

    override suspend fun execute(parameters: Unit): Flow<Unit> {
        return repository.leaveRoom()
    }
}