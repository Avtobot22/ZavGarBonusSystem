package com.zavgar.system.core.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

abstract class BaseViewModel<S, I, E>(initialState: S) : ViewModel() {

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<S> = _state.asStateFlow()

    protected val currentState: S
        get() = _state.value

    private val _event = Channel<E>()
    val event = _event.receiveAsFlow()

    abstract fun handleIntent(intent: I)

    protected fun setState(reduce: S.() -> S) {
        _state.update { oldState -> oldState.reduce() }
    }

    protected fun setEvent(builder: () -> E) {
        viewModelScope.launch {
            _event.send(builder())
        }
    }
}