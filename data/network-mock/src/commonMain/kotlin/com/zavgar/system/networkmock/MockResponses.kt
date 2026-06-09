package com.zavgar.system.networkmock

/**
 * Заготовленные JSON-ответы mock-сервера.
 *
 * Тела заданы сырым текстом (а не сериализацией доменных DTO), чтобы модуль не зависел от
 * формата сериализаторов и оставался максимально простым «фикстур-хранилищем». Формат полей
 * совпадает с контрактом ZavGar Server API:
 *  - даты — `dd.MM.yyyy`;
 *  - дата-время — `dd.MM.yyyy HH:mm:ss`.
 *
 * Значения легко править под нужный сценарий проверки UI без реального бэкенда.
 */
internal object MockResponses {

    /** Пара access/refresh токенов — отдаётся на подтверждении входа и обновлении токена. */
    val loginResponse: String = """
        {
          "accessToken": "mock-access-token",
          "accessExpiresIn": 3600,
          "refreshToken": "mock-refresh-token",
          "refreshExpiresIn": 1209600
        }
    """.trimIndent()

    /** Текущий бонусный баланс пользователя. */
    val balanceResponse: String = """
        {
          "balance": 12450
        }
    """.trimIndent()

    /** Профиль пользователя. */
    val profileResponse: String = """
        {
          "name": "Иван Тестовый",
          "phone": "+79990001122",
          "birthDate": "15.04.1990"
        }
    """.trimIndent()

    /** Сумма начислений за период. */
    val accrualsSumResponse: String = """
        {
          "sum": 3200
        }
    """.trimIndent()

    /** Первая (и единственная) страница истории операций. */
    val transactionsPageResponse: String = """
        {
          "items": [
            {
              "id": 1,
              "operationType": "+",
              "date": "08.06.2026 12:30:15",
              "store": "ZavGar на Ленина",
              "amount": 1500,
              "pointsType": "BONUS",
              "phone": "+79990001122"
            },
            {
              "id": 2,
              "operationType": "-",
              "date": "05.06.2026 18:05:42",
              "store": "ZavGar на Мира",
              "amount": 700,
              "pointsType": "BONUS",
              "phone": "+79990001122"
            },
            {
              "id": 3,
              "operationType": "+",
              "date": "01.06.2026 09:12:00",
              "store": "ZavGar на Гагарина",
              "amount": 2000,
              "pointsType": "CASHBACK",
              "phone": "+79990001122"
            }
          ],
          "nextCursor": null,
          "hasMore": false
        }
    """.trimIndent()

    /** Универсальное тело ошибки в формате `ErrorResponse` контракта. */
    fun errorBody(code: String, message: String): String = """
        {
          "code": "$code",
          "message": "$message"
        }
    """.trimIndent()
}