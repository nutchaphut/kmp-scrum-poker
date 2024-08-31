package com.nuzchpt.scrumpoker.domain.usecase

import RoomRepository
import com.nuzchpt.scrumpoker.model.participant.Participant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest

class JoinRoomUseCase(private val repository: RoomRepository) : UseCase<JoinRoomUseCase.Request, Unit>() {

    data class Request(val roomId: String)

    override suspend fun execute(parameters: Request): Flow<Unit> {
        return repository.getParticipantInfo(roomId = parameters.roomId).flatMapLatest { participant ->
            repository.joinRoom(
                roomId = parameters.roomId,
                role = participant?.role ?: Participant.ParticipantRole.MEMBER
            )
        }
    }

}