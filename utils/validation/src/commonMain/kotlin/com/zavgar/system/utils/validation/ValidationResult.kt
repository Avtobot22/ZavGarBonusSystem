package com.zavgar.system.utils.validation

sealed interface ValidationResult<out E> {
    data object Valid : ValidationResult<Nothing>
    data class Invalid<E>(val error: E) : ValidationResult<E>
}

/**
 * Перекладывает доменную ошибку [ValidationResult] в presentation-тип.
 *
 * Чистая трансформация без привязки к UI — presentation-мапперы (`asUiText`) живут в `:core`.
 */
fun <D, P> ValidationResult<D>.toPresentation(mapper: (D) -> P): ValidationResult<P> {
    return when (this) {
        is ValidationResult.Valid -> ValidationResult.Valid
        is ValidationResult.Invalid -> ValidationResult.Invalid(mapper(this.error))
    }
}
