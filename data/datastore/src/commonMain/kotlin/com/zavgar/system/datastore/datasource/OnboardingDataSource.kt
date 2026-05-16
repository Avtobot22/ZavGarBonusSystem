package com.zavgar.system.datastore.datasource

import kotlinx.coroutines.flow.Flow

interface OnboardingDataSource {

    val isOnboardingCompleted: Flow<Boolean>

    suspend fun setOnboardingCompleted()
}
