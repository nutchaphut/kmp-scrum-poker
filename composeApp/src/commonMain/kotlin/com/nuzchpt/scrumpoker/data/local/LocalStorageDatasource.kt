package com.nuzchpt.scrumpoker.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.firstOrNull

interface LocalStorageDatasource {
    suspend fun getUserId(): String
    suspend fun clearUser()
    suspend fun getUserName(): String?
    suspend fun saveUserName(userName: String)
    suspend fun getRoomId(): String?
    suspend fun saveRoomId(roomId: String)
    suspend fun clearRoomId()
}

class LocalStorageDatasourceImpl(
    private val pref: DataStore<Preferences>,
    private val platform: Platform,
) : LocalStorageDatasource {
    companion object {
        const val USER_ID = "USER_ID"
        const val USER_NAME = "USER_NAME"
        const val ROOM_ID = "ROOM_ID"
    }

    override suspend fun getUserId(): String {
        val preference = pref.data.firstOrNull()
        val userId: String? = preference?.get(stringPreferencesKey(USER_ID))
        if (userId == null) {
            pref.edit { dataStore ->
                dataStore[stringPreferencesKey(USER_ID)] = platform.deviceId
            }
        }
        return userId ?: platform.deviceId
    }

    override suspend fun clearUser() {
        pref.edit { dataStore ->
            dataStore.remove(stringPreferencesKey(USER_ID))
            dataStore.remove(stringPreferencesKey(USER_NAME))
        }
    }

    override suspend fun getUserName(): String? {
        val preference = pref.data.firstOrNull()
        return preference?.get(stringPreferencesKey(USER_NAME))
    }

    override suspend fun saveUserName(userName: String) {
        pref.edit { dataStore ->
            dataStore[stringPreferencesKey(USER_NAME)] = userName
        }
    }

    override suspend fun getRoomId(): String? {
        val preference = pref.data.firstOrNull()
        return preference?.get(stringPreferencesKey(ROOM_ID))
    }

    override suspend fun saveRoomId(roomId: String) {
        pref.edit { dataStore ->
            dataStore[stringPreferencesKey(ROOM_ID)] = roomId
        }
    }

    override suspend fun clearRoomId() {
        pref.edit { dataStore ->
            dataStore.remove(stringPreferencesKey(ROOM_ID))
        }
    }


}