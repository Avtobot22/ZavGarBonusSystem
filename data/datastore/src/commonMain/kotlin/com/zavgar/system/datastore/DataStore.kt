package com.zavgar.system.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath

private lateinit var dataStoreInstance: DataStore<Preferences>

fun getDataStore(producePath: () -> String): DataStore<Preferences> {
    if (!::dataStoreInstance.isInitialized) {
        dataStoreInstance = PreferenceDataStoreFactory.createWithPath { producePath().toPath() }
    }
    return dataStoreInstance
}

internal const val DataStoreFileName = "zavgar_settings.preferences_pb"