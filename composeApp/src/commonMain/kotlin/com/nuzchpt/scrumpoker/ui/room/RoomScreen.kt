package com.nuzchpt.scrumpoker.ui.room

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nuzchpt.scrumpoker.model.participant.Participant
import com.nuzchpt.scrumpoker.model.room.RoomState
import com.nuzchpt.scrumpoker.ui.component.CardFace
import com.nuzchpt.scrumpoker.ui.component.CircularLayout
import com.nuzchpt.scrumpoker.ui.component.FlipCard
import com.nuzchpt.scrumpoker.ui.navigation.NavigationActions
import com.nuzchpt.scrumpoker.ui.room.viewmodel.RoomDetailState
import com.nuzchpt.scrumpoker.ui.room.viewmodel.RoomViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class)
@Composable
fun RoomScreen(
    viewModel: RoomViewModel = koinViewModel(),
    navigationActions: NavigationActions,
) {
    val roomDetail = viewModel.roomDetail.collectAsState()
    val participants = viewModel.participants.collectAsState()
    val pointList = viewModel.pointList.collectAsState()
    val cardFaceState = remember {
        val state = (roomDetail.value as? RoomDetailState.Success)?.data?.state
        val cardFace = if (state == RoomState.END) CardFace.Back else CardFace.Front
        mutableStateOf(cardFace)
    }
    LaunchedEffect(roomDetail.value) {
        val state = (roomDetail.value as? RoomDetailState.Success)?.data?.state
        val cardFace = if (state == RoomState.END) CardFace.Back else CardFace.Front
        cardFaceState.value = cardFace
    }
    val roomState = (roomDetail.value as? RoomDetailState.Success)?.data?.state
    MaterialTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = (roomDetail.value as? RoomDetailState.Success)?.data?.roomName.orEmpty(),
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton({
                            //TODO show popup leave
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "menu items"
                            )
                        }
                    }
                )
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
                Box(modifier = Modifier.wrapContentSize()) {
                    if (participants.value.isNotEmpty()) {
                        CircularLayout(
                            radius = 460f,
                            content = {
                                participants.value.map { participant ->
                                    Participants(
                                        participant = participant,
                                        cardFaceState = cardFaceState
                                    )
                                }
                            })
                        Card(modifier = Modifier.height(108.dp).width(128.dp).align(Alignment.Center)) {
                            val buttonText = when (roomState) {
                                RoomState.VOTING -> "Show Vote"
                                else -> "Start Voting"
                            }

                            val onClickAction = when (roomState) {
                                RoomState.VOTING -> {
                                    { viewModel.input.showVoteResult() }
                                }

                                else -> {
                                    { viewModel.input.startVoting() }
                                }
                            }

                            TextButton(
                                modifier = Modifier.align(Alignment.Center),
                                onClick = onClickAction
                            ) {
                                Text(
                                    text = buttonText,
                                    textAlign = TextAlign.Center
                                )
                            }

                        }
                    }
                }

                if (roomState == RoomState.END) {
                    Column(modifier = Modifier.align(Alignment.BottomCenter)) {
                        Text("Voting Result")
                        LazyRow {
                            val points = participants.value
                                .groupBy { it.point }
                                .mapValues { it.value.size }
                                .toList()
                            items(points) { (point, count) ->
                                point?.let {
                                    PointCard(point, count)
                                }
                            }
                        }
                    }
                } else {
                    LazyRow(
                        modifier = Modifier.align(Alignment.BottomStart).fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(pointList.value) { item ->
                            Card(
                                backgroundColor = if (item.isVoted) Color.Blue else Color.White,
                                modifier = Modifier
                                    .height(64.dp)
                                    .width(48.dp)
                                    .clickable {
                                        viewModel.input.votePoint(item.point)
                                    }
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Text(
                                        text = item.point.display,
                                        color = if (item.isVoted) Color.White else Color.Black
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PointCard(point: String, count: Int) {
    Box(
        modifier = Modifier
            .height(80.dp)
            .width(64.dp)
    ) {
        Card(
            backgroundColor = Color.White,
            modifier = Modifier
                .height(64.dp)
                .width(48.dp)
                .align(Alignment.Center)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = point,
                    color = Color.Black
                )
            }
        }

        if (count > 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .background(Color.Red, shape = CircleShape)
                    .size(24.dp)
                    .padding(4.dp)
            ) {
                Text(
                    text = count.toString(),
                    color = Color.White,
                    style = TextStyle(fontSize = 12.sp),
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@Composable
fun Participants(participant: Participant, cardFaceState: MutableState<CardFace>) {
    val cardBaseModifier = Modifier
        .height(64.dp)
        .width(48.dp)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .wrapContentWidth()
            .padding(8.dp)
    ) {
        FlipCard(
            cardFace = cardFaceState.value,
            modifier = cardBaseModifier,
            back = {
                Box(
                    modifier = cardBaseModifier
                        .wrapContentSize(Alignment.Center)
                ) {
                    Text(
                        text = participant.point.orEmpty(),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            },
            front = {
                Box(
                    modifier = cardBaseModifier
                        .background(if (participant.point != null) Color.Black else Color.White)
                )
            }
        )
        Text(
            text = participant.participantName.orEmpty().take(16),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}


