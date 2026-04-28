package com.zavgar.system.core.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

abstract class BaseViewModel<S, I, E>(initialState: S) : ViewModel() {

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<S> = _state.asStateFlow()

    protected val currentState: S
        get() = _state.value

    private val _event = MutableSharedFlow<E>(
        extraBufferCapacity = 64,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    val event: SharedFlow<E> = _event.asSharedFlow()

    abstract fun handleIntent(intent: I)

    protected fun setState(reduce: S.() -> S) {
        _state.update { oldState -> oldState.reduce() }
    }

    protected fun setEvent(builder: () -> E) {
        _event.tryEmit(builder())
    }

    protected fun launchTry(tryBlock: suspend CoroutineScope.() -> Unit): LaunchBuilder =
        LaunchBuilder(tryBlock, viewModelScope)

    inner class LaunchBuilder(
        private val tryBlock: suspend CoroutineScope.() -> Unit,
        private val scope: CoroutineScope,
    ) {
        infix fun catch(catchBlock: suspend (Exception) -> Unit): Job =
            scope.launch {
                try {
                    tryBlock()
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    catchBlock(e)
                }
            }
    }
}
