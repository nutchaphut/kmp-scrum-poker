@file:OptIn(ExperimentalForeignApi::class, ExperimentalForeignApi::class)

package com.nuzchpt.scrumpoker

import DATA_STORE_FILE_NAME
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.nuzchpt.scrumpoker.data.local.Platform
import createDataStore
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask
import platform.UIKit.UIDevice

class IOSPlatform : Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
    override val deviceId: String = UIDevice.currentDevice.identifierForVendor?.UUIDString ?: "unknown_device_id"
}


fun createDataStoreIOS(): DataStore<Preferences> {
    return createDataStore {
        val directory = NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null
        )
        requireNotNull(directory).path + "/$DATA_STORE_FILE_NAME"
    }
}