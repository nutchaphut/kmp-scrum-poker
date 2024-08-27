import com.nuzchpt.scrumpoker.model.participant.Participant
import dev.gitlive.firebase.firestore.DocumentSnapshot
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull

interface FireStoreService {
    fun getParticipantList(roomId: String): Flow<QuerySnapshot>
    fun getRoom(roomId: String): Flow<DocumentSnapshot>
    fun joinRoom(roomId: String, participantId: String, participantName: String): Flow<Unit>
    fun startVoting(roomId: String, state: String): Flow<Unit>
    fun clearPoint(roomId: String): Flow<Unit>
    fun setPointVoting(roomId: String, participantId: String, point: String?): Flow<Unit>
}

class FireStoreServiceImpl(private val firestore: FirebaseFirestore) : FireStoreService {
    override fun getRoom(roomId: String) = firestore.collection("rooms").document(roomId).snapshots
    override fun joinRoom(
        roomId: String,
        participantId: String,
        participantName: String,
    ): Flow<Unit> = callbackFlow {
        firestore.collection("rooms")
            .document(roomId)
            .collection("participants")
            .document(participantId).set(
                Participant(
                    participantId = participantId,
                    participantName = participantName,
                    role = Participant.ParticipantRole.MEMBER
                ),
                merge = true
            )
        trySend(Unit)
        awaitClose {
            channel.close()
        }
    }

    override fun startVoting(roomId: String, state: String): Flow<Unit> = callbackFlow {
        firestore.collection("rooms")
            .document(roomId).update("state" to state)
        trySend(Unit)
        awaitClose {
            channel.close()
        }
    }

    override fun getParticipantList(roomId: String): Flow<QuerySnapshot> =
        firestore.collection("rooms")
            .document(roomId)
            .collection("participants").snapshots


    override fun clearPoint(roomId: String): Flow<Unit> = callbackFlow {
        val batch = firestore.batch()

        val snapshot = firestore.collection("rooms")
            .document(roomId)
            .collection("participants").snapshots
        snapshot.firstOrNull()?.let { querySnapshot ->
            querySnapshot.documents.forEach { document ->
                val documentRef = firestore.collection("rooms")
                    .document(roomId)
                    .collection("participants").document(document.id)
                batch.update(documentRef, "point" to null)
            }
            batch.commit()
            trySend(Unit)
            channel.close()
        }
        awaitClose {
            channel.close()
        }
    }

    override fun setPointVoting(roomId: String, participantId: String, point: String?): Flow<Unit> = callbackFlow {
        firestore.collection("rooms")
            .document(roomId)
            .collection("participants")
            .document(participantId)
            .update("point" to point)
        trySend(Unit)
        awaitClose {
            channel.close()
        }
    }

}