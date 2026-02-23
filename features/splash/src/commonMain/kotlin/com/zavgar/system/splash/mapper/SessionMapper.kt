package com.zavgar.system.splash.mapper

import com.zavgar.system.domain.model.response.Session as DomainSession
import com.zavgar.system.splash.model.Session as PresentationSession

fun DomainSession.toPresentation() = PresentationSession(
    phone = this.phone,
    accessToken = this.accessToken,
    refreshToken = this.refreshToken
)

fun Result<DomainSession>.toPresentation(): Result<PresentationSession> =
    this.map { it.toPresentation() }