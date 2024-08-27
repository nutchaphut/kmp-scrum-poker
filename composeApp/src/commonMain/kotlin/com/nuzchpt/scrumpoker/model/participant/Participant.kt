package com.nuzchpt.scrumpoker.model.participant

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class Participant(
    val participantName: String? = null,
    val participantId: String,
    val point: String? = null,
    val role: ParticipantRole? = null,
    @Transient
    var isRevealed: Boolean = false,
) {
    @Transient
    var cardState: ParticipantState = when {
        isRevealed -> ParticipantState.REVEALED
        point != null -> ParticipantState.VOTED
        else -> ParticipantState.IDLE
    }

    enum class ParticipantState {
        IDLE,
        VOTED,
        REVEALED
    }

    enum class ParticipantRole {
        HOST,
        MEMBER
    }
}