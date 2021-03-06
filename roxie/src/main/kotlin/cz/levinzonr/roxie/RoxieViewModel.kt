package cz.levinzonr.roxie

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * Store which manages business data and viewState.
 */
abstract class RoxieViewModel<A : BaseAction, S : BaseState, E : BaseEvent, C : BaseChange>() :
    ViewModel() {
    private val changes: MutableSharedFlow<C> = MutableSharedFlow()

    protected abstract val initialState: S
    protected abstract val reducer: Reducer<S, C>

    protected val currentState: S
        get() = _stateFlow.value


    private val tag by lazy { javaClass.simpleName }

    /**
     * Returns the current viewState. It is equal to the last value returned by the store's reducer.
     */

    private val _stateFlow by lazy { MutableStateFlow<S>(this.initialState) }
    private val _eventFlow by lazy { MutableSharedFlow<E>() }


    val stateFlow: Flow<S> = _stateFlow
    val eventFlow: Flow<E> = _eventFlow
    protected fun startActionsObserver() = viewModelScope.launch {
        changes.scan(initialState, reducer)
            .distinctUntilChanged()
            .collect { _stateFlow.emit(it) }

    }

    /**
     * Dispatches an action. This is the only way to trigger a viewState change.
     */
    fun dispatch(action: A) {
        Roxie.log("$tag: Received action: $action")
        viewModelScope.launch {
            emitAction(action)
                .collect { changes.emit(it) }
        }
    }

    fun S.withEvent(e: E): S {
        viewModelScope.launch { _eventFlow.emit(e) }
        return this
    }

    protected abstract fun emitAction(action: A): Flow<C>
}