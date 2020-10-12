package cz.levinzonr.roxie

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.launch

/**
 * Store which manages business data and viewState.
 */
abstract class RoxieViewModel<A : BaseAction, S : BaseState, C : BaseChange> : ViewModel() {
    protected val changes: Channel<C> = Channel()

    protected abstract val initialState: S
    protected abstract val reducer: Reducer<S, C>

    protected val currentState: S
        get() = viewState.value ?: initialState

    protected val viewState = MutableLiveData<S>()

    private val tag by lazy { javaClass.simpleName }

    /**
     * Returns the current viewState. It is equal to the last value returned by the store's reducer.
     */

    protected var _viewState = MediatorLiveData<S>().apply {
        addSource(viewState) { state ->
            Roxie.log("$tag: Received state: $state")
            setValue(state)
        }
    }


    open val observableState: LiveData<S> = _viewState

    protected fun <T> addStateSource(source: LiveData<T>, onChanged: (T) -> Unit) {
        _viewState.addSource(source, onChanged)
    }

    protected fun startActionsObserver() = viewModelScope.launch {
        changes.consumeAsFlow()
            .flowOn(Dispatchers.Main)
            .scan(initialState, reducer)
            .distinctUntilChanged()
            .collect {
                viewState.postValue(it)
            }
    }

    /**
     * Dispatches an action. This is the only way to trigger a viewState change.
     */
    fun dispatch(action: A) {
        Roxie.log("$tag: Received action: $action")
        viewModelScope.launch {
            emitAction(action)
                .collect { changes.send(it) }
        }
    }

    protected abstract fun emitAction(action: A): Flow<C>
}