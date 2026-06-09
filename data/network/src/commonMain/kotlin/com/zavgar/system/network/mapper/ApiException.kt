package com.zavgar.system.network.mapper

import com.zavgar.system.network.model.ApiErrorCode

/**
 * Прикладное исключение, в которое сетевой слой переводит любой неуспешный HTTP-ответ.
 *
 * Тело ответа (`ErrorResponse`) и заголовок `Retry-After` читаются один раз в Ktor-валидаторе
 * ([com.zavgar.system.network.di.networkModule]), после чего классификация ([classifyNetworkError])
 * остаётся синхронной и ветвит ошибки по машиночитаемому [errorCode], а не по одному статусу.
 *
 * @property statusCode HTTP-статус ответа.
 * @property errorCode Код ошибки из тела `ErrorResponse` либо `null`, если тело отсутствует/не распознано
 *   (например фреймворковый rate-limit 429 с пустым телом).
 * @property retryAfterSeconds Значение заголовка `Retry-After` в секундах (если присутствует).
 */
class ApiException(
    val statusCode: Int,
    val errorCode: ApiErrorCode?,
    val retryAfterSeconds: Long?,
    val errorMessage: String,
    cause: Throwable? = null,
) : Exception(errorMessage, cause)
