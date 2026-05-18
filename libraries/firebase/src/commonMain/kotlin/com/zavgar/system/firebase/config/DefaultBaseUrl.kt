package com.zavgar.system.firebase.config

/**
 * Платформенный URL сервера по умолчанию.
 *
 * Используется как дефолт Remote Config до первого успешного фетча и при оффлайне.
 */
internal expect val defaultBaseUrl: String
