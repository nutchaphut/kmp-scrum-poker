package com.nuzchpt.scrumpoker.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.nuzchpt.scrumpoker.ui.main.viewmodel.JoinRoomState
import com.nuzchpt.scrumpoker.ui.main.viewmodel.MainViewModel
import com.nuzchpt.scrumpoker.ui.main.viewmodel.UserInfoState
import com.nuzchpt.scrumpoker.ui.navigation.NavigationActions
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@KoinExperimentalAPI
@Composable
fun MainScreen(
    viewModel: MainViewModel = koinViewModel(),
    navigationActions: NavigationActions,
) {
    val joinRoomState by viewModel.joinRoomState.collectAsState()
    val userInfoState by viewModel.getUserInfoState.collectAsState()
    val roomId = remember { mutableStateOf("") }
    val bottomSheetUserInputName = remember { mutableStateOf("") }
    val userInputRoomName = remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("KMP Scrum Poker")
                    }
                },
                navigationIcon = {
                    IconButton({
                        // TODO: navigate to menu
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = "menu items"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        viewModel.input.openCreateRoomDialog()
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "create room",
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {

            Column(modifier = Modifier.wrapContentSize().align(Alignment.Center)) {
                // TODO: revamp Ui
                TextField(
                    value = roomId.value,
                    onValueChange = {
                        roomId.value = it
                    }
                )
                TextButton(onClick = {
                    if (roomId.value.isNotEmpty()) {
                        viewModel.input.joinRoom(roomId.value)
                    }
                }) {
                    Text(
                        text = "Join",
                        textAlign = TextAlign.Center,
                    )
                }
            }

            when (userInfoState) {

                is UserInfoState.UnRegister -> {

                    Dialog(onDismissRequest = {}) {
                        // TODO: revamp ui?
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .padding(16.dp),
                            shape = RoundedCornerShape(16.dp),
                        ) {
                            Column(modifier = Modifier.wrapContentSize().align(Alignment.Center)) {
                                TextField(
                                    value = bottomSheetUserInputName.value,
                                    onValueChange = {
                                        bottomSheetUserInputName.value = it
                                    }
                                )
                                TextButton(onClick = {
                                    if (bottomSheetUserInputName.value.isNotEmpty()) {
                                        viewModel.input.saveUserName(bottomSheetUserInputName.value)
                                    }
                                }) {
                                    Text(
                                        text = "Save",
                                        textAlign = TextAlign.Center,
                                    )
                                }
                            }
                        }
                    }
                }

                else -> {

                }

            }
        }
    }

    when (val state = joinRoomState) {
        is JoinRoomState.OpenDialogCreateRoom -> {
            Dialog(onDismissRequest = {}) {
                // TODO: revamp ui?
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Column(modifier = Modifier.wrapContentSize()) {
                        TextField(
                            value = userInputRoomName.value,
                            onValueChange = {
                                userInputRoomName.value = it
                            }
                        )
                        Row {
                            TextButton(onClick = {
                                viewModel.input.resetJoinState()
                            }) {
                                Text(
                                    text = "Cancel",
                                    textAlign = TextAlign.Center,
                                )
                            }

                            TextButton(onClick = {
                                if (userInputRoomName.value.isNotEmpty()) {
                                    viewModel.input.createRoom(userInputRoomName.value)
                                }
                            }) {
                                Text(
                                    text = "Create",
                                    textAlign = TextAlign.Center,
                                )
                            }
                        }
                    }
                }
            }
        }

        is JoinRoomState.Idle -> {

        }

        is JoinRoomState.Loading -> {
            // TODO: show loading
        }

        is JoinRoomState.Success -> {
            navigationActions.navigateToRoom(state.roomDetail.roomId)
            viewModel.input.resetJoinState()
        }

        is JoinRoomState.Error -> {
            // TODO: show error
        }
    }
}