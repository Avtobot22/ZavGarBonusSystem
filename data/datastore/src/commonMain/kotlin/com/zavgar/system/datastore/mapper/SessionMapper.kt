package com.zavgar.system.datastore.mapper

import com.zavgar.system.datastore.model.Session as DataStoreSession
import com.zavgar.system.repository.model.response.Session as RepoSession


fun RepoSession.toDataStore() = DataStoreSession(
    accessToken = this.accessToken,
    refreshToken = this.refreshToken,
    phone = this.phone
)

fun DataStoreSession.toRepo() = RepoSession(
    accessToken = this.accessToken,
    refreshToken = this.refreshToken,
    phone = this.phone
)