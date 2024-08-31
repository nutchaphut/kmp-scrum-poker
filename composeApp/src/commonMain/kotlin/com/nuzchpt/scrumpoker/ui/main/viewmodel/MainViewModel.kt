package com.nuzchpt.scrumpoker.ui.main.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nuzchpt.scrumpoker.domain.usecase.CreateRoomUseCase
import com.nuzchpt.scrumpoker.domain.usecase.GetRoomDetailUseCase
import com.nuzchpt.scrumpoker.domain.usecase.GetUserInfoUseCase
import com.nuzchpt.scrumpoker.domain.usecase.JoinRoomUseCase
import com.nuzchpt.scrumpoker.domain.usecase.SaveUserInfoUseCase
import com.nuzchpt.scrumpoker.model.room.RoomDetail
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

abstract class MainViewModel : ViewModel() {
    abstract val input: Input
    abstract val joinRoomState: StateFlow<JoinRoomState>
    abstract val getUserInfoState: StateFlow<UserInfoState>

    interface Input {
        fun getUserInfo()
        fun joinRoom(roomId: String)
        fun createRoom(roomName: String)
        fun resetJoinState()
        fun saveUserName(userName: String)
        fun openCreateRoomDialog()
    }
}

class MainViewModelImpl(
    private val getRoomDetailUseCase: GetRoomDetailUseCase,
    private val joinRoomUseCase: JoinRoomUseCase,
    private val saveUserInfoUseCase: SaveUserInfoUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val createRoomUseCase: CreateRoomUseCase,
) : MainViewModel(), MainViewModel.Input {
    override val input: Input
        get() = this

    private val _joinRoomState = MutableStateFlow<JoinRoomState>(JoinRoomState.Idle)
    override val joinRoomState: StateFlow<JoinRoomState>
        get() = _joinRoomState

    private val _getUserInfoState = MutableStateFlow<UserInfoState>(UserInfoState.Loading)
    override val getUserInfoState: StateFlow<UserInfoState>
        get() = _getUserInfoState

    init {
        getUserInfo()
    }

    override fun getUserInfo() {
        viewModelScope.launch {
            getUserInfoUseCase.execute(Unit).catch {
                _getUserInfoState.update { UserInfoState.UnRegister }
            }.collect { info ->
                _getUserInfoState.update {
                    if (info != null) {
                        UserInfoState.Registered
                    } else {
                        UserInfoState.UnRegister
                    }
                }

            }
        }
    }

    override fun joinRoom(roomId: String) {
        viewModelScope.launch {
            getRoomDetailUseCase.execute(GetRoomDetailUseCase.Request(roomId))
                .onStart {
                    _joinRoomState.update { JoinRoomState.Loading }
                }.catch {
                    _joinRoomState.update { JoinRoomState.Error("error") }
                }.collect { roomDetail ->
                    joinRoomUseCase.execute(
                        JoinRoomUseCase.Request(roomId = roomId)
                    ).catch {
                        _joinRoomState.update { JoinRoomState.Error("error") }
                    }.collect {
                        _joinRoomState.update { JoinRoomState.Success(roomDetail) }
                    }
                }

        }
    }

    override fun createRoom(roomName: String) {
        viewModelScope.launch {
            createRoomUseCase.execute(CreateRoomUseCase.Request(roomName))
                .catch {
                    println("error-> $it")
                    _joinRoomState.update { JoinRoomState.Error("error") }
                }
                .onStart { _joinRoomState.update { JoinRoomState.Loading } }
                .collect { roomDetail ->
                    joinRoomUseCase.execute(
                        JoinRoomUseCase.Request(
                            isHost = true,
                            roomId = roomDetail.roomId
                        )
                    )
                        .catch {
                            _joinRoomState.update { JoinRoomState.Error("error") }
                        }.collect {
                            _joinRoomState.update { JoinRoomState.Success(roomDetail) }
                        }

                }
        }
    }

    override fun resetJoinState() {
        viewModelScope.launch {
            _joinRoomState.emit(JoinRoomState.Idle)
        }
    }

    override fun saveUserName(userName: String) {
        viewModelScope.launch {
            _getUserInfoState.update { UserInfoState.Loading }
            saveUserInfoUseCase.execute(SaveUserInfoUseCase.Request(userName)).catch {
                _getUserInfoState.update { UserInfoState.UnRegister }
            }.collect {
                _getUserInfoState.update { UserInfoState.Registered }

            }
        }
    }

    override fun openCreateRoomDialog() {
        _joinRoomState.update { JoinRoomState.OpenDialogCreateRoom }
    }

}

interface JoinRoomState {
    data object Idle : JoinRoomState
    data object Loading : JoinRoomState
    data object OpenDialogCreateRoom : JoinRoomState
    data class Error(val error: String) : JoinRoomState
    data class Success(val roomDetail: RoomDetail) : JoinRoomState
}

interface UserInfoState {
    data object Loading : UserInfoState
    data object UnRegister : UserInfoState
    data object Registered : UserInfoState
}