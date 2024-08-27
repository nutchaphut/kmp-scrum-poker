package com.nuzchpt.scrumpoker.domain.usecase

import RoomRepository
import com.nuzchpt.scrumpoker.model.participant.Participant
import kotlinx.coroutines.flow.Flow

class GetParticipantsUseCase(private val repository: RoomRepository) :
    UseCase<GetParticipantsUseCase.Request, List<Participant>>() {
    data class Request(val roomId: String)

    override suspend fun execute(parameters: Request): Flow<List<Participant>> {
        return repository.getParticipantDetail(parameters.roomId)
    }
}