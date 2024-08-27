package com.nuzchpt.scrumpoker.ui.room.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nuzchpt.scrumpoker.domain.usecase.ClearRoomParticipatesPointUseCase
import com.nuzchpt.scrumpoker.domain.usecase.GetParticipantsUseCase
import com.nuzchpt.scrumpoker.domain.usecase.GetRoomDetailUseCase
import com.nuzchpt.scrumpoker.domain.usecase.SetPointVotingUseCase
import com.nuzchpt.scrumpoker.domain.usecase.SetVotingStateUseCase
import com.nuzchpt.scrumpoker.model.participant.Participant
import com.nuzchpt.scrumpoker.model.point.PointVotingAvailable
import com.nuzchpt.scrumpoker.model.point.VotingModel
import com.nuzchpt.scrumpoker.model.room.RoomDetail
import com.nuzchpt.scrumpoker.model.room.RoomState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

abstract class RoomViewModel : ViewModel() {
    abstract val input: Input
    abstract val roomDetail: StateFlow<RoomDetailState>
    abstract val participants: StateFlow<List<Participant>>
    abstract val pointList: StateFlow<List<VotingModel>>

    interface Input {
        fun getRoomDetail()
        fun getParticipants()
        fun startVoting()
        fun showVoteResult()
        fun votePoint(model: PointVotingAvailable)
    }
}

class RoomViewModelImpl(
    private val roomId: String,
    private val getRoomDetailUseCase: GetRoomDetailUseCase,
    private val getParticipantsUseCase: GetParticipantsUseCase,
    private val setVotingStateUseCase: SetVotingStateUseCase,
    private val clearRoomParticipatesPointUseCase: ClearRoomParticipatesPointUseCase,
    private val setPointVotingUseCase: SetPointVotingUseCase,
) : RoomViewModel(), RoomViewModel.Input {
    override val input: Input
        get() = this

    private val _roomDetail = MutableStateFlow<RoomDetailState>(RoomDetailState.Loading)
    override val roomDetail: StateFlow<RoomDetailState>
        get() = _roomDetail

    private val _participants = MutableStateFlow<List<Participant>>(emptyList())
    override val participants: StateFlow<List<Participant>>
        get() = _participants

    private val defaultPointList = PointVotingAvailable.getPointVotingAvailableList()
        .map { VotingModel(point = it, isVoted = false) }
    private val _pointList = MutableStateFlow(defaultPointList)
    override val pointList: StateFlow<List<VotingModel>>
        get() = _pointList

    init {
        getRoomDetail()
        getParticipants()
    }

    override fun getRoomDetail() {
        viewModelScope.launch {
            getRoomDetailUseCase.execute(GetRoomDetailUseCase.Request(roomId))
                .onStart {
                    _roomDetail.emit(RoomDetailState.Loading)
                }.catch {
                    _roomDetail.emit(RoomDetailState.Error("error"))
                }.collect {
                    if (it.state == RoomState.VOTING) {
                        _pointList.value = defaultPointList
                    }
                    _roomDetail.emit(RoomDetailState.Success(it))
                }
        }
    }

    override fun getParticipants() {
        viewModelScope.launch {
            getParticipantsUseCase.execute(GetParticipantsUseCase.Request(roomId)).catch {
                _participants.emit(emptyList())
            }.collect {
                _participants.emit(it)
            }
        }
    }

    override fun startVoting() {
        viewModelScope.launch {
            combine(
                clearRoomParticipatesPointUseCase.execute(ClearRoomParticipatesPointUseCase.Request(roomId)).catch { },
                setVotingStateUseCase.execute(SetVotingStateUseCase.Request(roomId, RoomState.VOTING)).catch {}
            ) { clearRoom, setVotingState -> }.catch { }.collect {

            }
        }
    }

    override fun showVoteResult() {
        viewModelScope.launch {
            setVotingStateUseCase.execute(SetVotingStateUseCase.Request(roomId, RoomState.END)).catch {}.collect {
            }
        }
    }

    override fun votePoint(model: PointVotingAvailable) {
        viewModelScope.launch {
            var isClear = false
            _pointList.value = _pointList.value.map { votingModel ->
                when {
                    votingModel.point == model -> {
                        if (votingModel.isVoted) {
                            isClear = true
                        }
                        votingModel.copy(isVoted = !votingModel.isVoted)
                    }

                    else -> votingModel.copy(isVoted = false)
                }
            }
            val point = if (isClear) null else model.key
            setPointVotingUseCase.execute(SetPointVotingUseCase.Request(roomId, point = point)).catch { }.collect {

            }
        }
    }

}

interface RoomDetailState {
    data object Loading : RoomDetailState
    data class Error(val error: String) : RoomDetailState
    data class Success(val data: RoomDetail) : RoomDetailState
}