import com.nuzchpt.scrumpoker.data.local.LocalStorageDatasource
import com.nuzchpt.scrumpoker.model.participant.Participant
import com.nuzchpt.scrumpoker.model.point.PointVotingAvailable
import com.nuzchpt.scrumpoker.model.room.RoomDetail
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

interface RoomRepository {
    fun getRoomDetail(roomId: String): Flow<RoomDetail>
    fun getParticipantDetailList(roomId: String): Flow<List<Participant>>
    fun getListPointVoting(): List<PointVotingAvailable>
    fun getParticipantInfo(roomId: String): Flow<Participant?>
    fun joinRoom(roomId: String, role: Participant.ParticipantRole = Participant.ParticipantRole.MEMBER): Flow<Unit>
    fun setVotingState(roomId: String, state: String): Flow<Unit>
    fun clearParticipantPoints(roomId: String): Flow<Unit>
    fun setPointVoting(roomId: String, point: String?): Flow<Unit>
    fun leaveRoom(): Flow<Unit>
}

class RoomRepositoryImpl(
    private val fireStoreService: FireStoreService,
    private val localStorageDatasource: LocalStorageDatasource,
) : RoomRepository {
    override fun getRoomDetail(roomId: String): Flow<RoomDetail> = flow {
        fireStoreService.getRoom(roomId).collect { documentSnapshot ->
            emit(documentSnapshot.data<RoomDetail>())
        }
    }

    override fun getParticipantDetailList(roomId: String): Flow<List<Participant>> = flow {
        fireStoreService.getParticipantList(roomId).collect { querySnapShot ->
            val users = querySnapShot.documents.map { queryDocument ->
                queryDocument.data<Participant>()
            }
            emit(users)
        }
    }

    override fun getListPointVoting(): List<PointVotingAvailable> {
        return PointVotingAvailable.getPointVotingAvailableList()
    }

    override fun getParticipantInfo(roomId: String): Flow<Participant?> = flow {
        fireStoreService.getParticipantDetail(
            roomId = roomId,
            participantId = localStorageDatasource.getUserId(),
        ).catch { emit(null) }.collect { documentSnapshot ->
            emit(documentSnapshot.data<Participant>())
        }
    }

    override fun joinRoom(roomId: String, role: Participant.ParticipantRole): Flow<Unit> = flow {
        fireStoreService.joinRoom(
            roomId = roomId,
            participantId = localStorageDatasource.getUserId(),
            participantName = localStorageDatasource.getUserName() ?: localStorageDatasource.getUserId(),
            role = role
        ).collect {
            localStorageDatasource.saveRoomId(roomId)
            emit(Unit)
        }
    }

    override fun setVotingState(roomId: String, state: String): Flow<Unit> = flow {
        fireStoreService.startVoting(roomId = roomId, state = state).collect { emit(Unit) }
    }

    override fun clearParticipantPoints(roomId: String): Flow<Unit> = flow {
        fireStoreService.clearPoint(roomId = roomId).collect { emit(Unit) }
    }

    override fun setPointVoting(roomId: String, point: String?): Flow<Unit> = flow {
        fireStoreService.setPointVoting(
            roomId = roomId,
            participantId = localStorageDatasource.getUserId(),
            point = point
        ).collect {}
    }

    override fun leaveRoom(): Flow<Unit> = flow {
        emit(localStorageDatasource.clearRoomId())
    }
}