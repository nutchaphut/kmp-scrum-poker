package com.nuzchpt.scrumpoker.model.room

import kotlinx.serialization.Serializable

@Serializable
data class RoomDetail(
    val roomId: String,
    val roomName: String,
    val state: RoomState? = null,
)

enum class RoomState {
    END,
    VOTING
}