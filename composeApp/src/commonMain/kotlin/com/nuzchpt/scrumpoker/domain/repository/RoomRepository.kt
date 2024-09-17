import com.nuzchpt.scrumpoker.data.local.LocalStorageDatasource
import com.nuzchpt.scrumpoker.model.participant.Participant
import com.nuzchpt.scrumpoker.model.point.PointVotingAvailable
import com.nuzchpt.scrumpoker.model.room.RoomDetail
import kotlin.random.Random
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
    fun createRoom(roomName: String): Flow<RoomDetail>
    suspend fun getLocalRoomId(): String?
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
            emit(documentSnapshot.data<Participant?>())
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

    override fun createRoom(roomName: String): Flow<RoomDetail> = flow {
        val roomId = generateRoomUUID()
        fireStoreService.createRoom(roomName = roomName, roomId = roomId).collect {
            emit(RoomDetail(roomId = roomId, roomName = roomName))
        }
    }

    override suspend fun getLocalRoomId(): String? {
        return localStorageDatasource.getRoomId()
    }


    private fun generateRoomUUID(): String {
        val firstPart = (Random.nextDouble() * 46656).toInt()
        val secondPart = (Random.nextDouble() * 46656).toInt()
        val firstPartStr = firstPart.toString(36).padStart(3, '0')
        val secondPartStr = secondPart.toString(36).padStart(3, '0')

        return firstPartStr + secondPartStr
    }
}